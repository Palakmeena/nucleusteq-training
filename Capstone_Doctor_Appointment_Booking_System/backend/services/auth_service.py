from fastapi import HTTPException, status

from constants.messages import AuthMessages
from models.doctor import Doctor
from models.patient import Patient
from models.user import Role, User
from repositories.doctor_repository import DoctorRepository
from repositories.patient_repository import PatientRepository
from repositories.user_repository import UserRepository
from schemas.request.auth_request import (
    DoctorRegisterRequest,
    PatientRegisterRequest,
)
from schemas.response.auth_response import (
    LoginResponse,
    UserResponse,
)
from utils.jwt_utils import create_access_token
from utils.logger import get_logger
from utils.password_utils import hash_password, verify_password

logger = get_logger(__name__)

user_repo = UserRepository()
patient_repo = PatientRepository()
doctor_repo = DoctorRepository()


async def register_patient(
    data: PatientRegisterRequest,
) -> UserResponse:
    """
    Register a new patient.
    Creates both the User and Patient documents.
    """

    existing_user = await user_repo.find_by_email(data.email)

    if existing_user:
        logger.warning(
            f"Registration failed. Email already exists: {data.email}"
        )
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=AuthMessages.EMAIL_ALREADY_EXISTS,
        )

    user = User(
        full_name=data.full_name,
        email=data.email,
        password_hash=hash_password(data.password),
        phone=data.phone,
        role=Role.PATIENT,
        is_active=True,
    )

    await user_repo.save(user)

    patient = Patient(
        user_id=str(user.id),
        gender=data.gender,
        date_of_birth=data.date_of_birth,
    )

    await patient_repo.save(patient)

    logger.info(f"Patient registered successfully: {user.email}")

    return UserResponse.model_validate(user)


async def register_doctor(
    data: DoctorRegisterRequest,
) -> UserResponse:
    """
    Register a doctor.

    The doctor account remains inactive until approved
    by the administrator.
    """

    existing_user = await user_repo.find_by_email(data.email)

    if existing_user:
        logger.warning(
            f"Registration failed. Email already exists: {data.email}"
        )
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=AuthMessages.EMAIL_ALREADY_EXISTS,
        )

    existing_license = await doctor_repo.find_by_license(
        data.license_number
    )

    if existing_license:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="License number already registered.",
        )

    user = User(
        full_name=data.full_name,
        email=data.email,
        password_hash=hash_password(data.password),
        phone=data.phone,
        role=Role.DOCTOR,
        is_active=False,
    )

    await user_repo.save(user)

    doctor = Doctor(
        user_id=str(user.id),
        full_name=data.full_name,
        phone=data.phone,
        qualification=data.qualification,
        experience=data.experience,
        license_number=data.license_number,
        specialization=data.specialization,
        consultation_fee=data.consultation_fee,
        clinic_address=data.clinic_address,
        is_active=False,
    )

    await doctor_repo.save(doctor)

    logger.info(
        f"Doctor registered successfully: {user.email}. Awaiting admin approval."
    )

    return UserResponse.model_validate(user)


async def login_user(
    email: str,
    password: str,
) -> LoginResponse:
    """
    Authenticate a user and return a JWT access token.
    """

    user = await user_repo.find_by_email(email)

    if not user:
        logger.warning(f"Login failed. User not found: {email}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=AuthMessages.INVALID_CREDENTIALS,
        )

    if not verify_password(password, user.password_hash):
        logger.warning(f"Login failed. Invalid password for: {email}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=AuthMessages.INVALID_CREDENTIALS,
        )

    if not user.is_active:
        logger.warning(f"Inactive account login attempt: {email}")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=AuthMessages.ACCOUNT_INACTIVE,
        )

    access_token = create_access_token(
        user_id=str(user.id),
        email=user.email,
        role=user.role,
    )

    logger.info(
        f"Login successful: {user.email} ({user.role})"
    )

    return LoginResponse(
        access_token=access_token,
        role=user.role,
        user_id=str(user.id),
        full_name=user.full_name,
    )