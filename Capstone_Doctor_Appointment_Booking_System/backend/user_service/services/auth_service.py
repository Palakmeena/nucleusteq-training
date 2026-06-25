from fastapi import HTTPException, status
from models.user import User, Role
from models.patient import Patient
from schemas.request.register_request import PatientRegisterRequest, DoctorRegisterRequest
from schemas.response.user_response import UserResponse, LoginResponse
from utils.password_utils import hash_password, verify_password
from utils.jwt_utils import create_access_token
from constants.messages import AuthMessages
from repositories.user_repository import UserRepository
from utils.logger import get_logger

logger = get_logger(__name__)
user_repo = UserRepository()


async def register_patient(data: PatientRegisterRequest) -> UserResponse:
    existing_user = await user_repo.find_by_email(data.email)
    if existing_user:
        logger.warning(f"Registration failed - email already exists: {data.email}")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=AuthMessages.EMAIL_ALREADY_EXISTS
        )

    # Save in users collection
    user = User(
        full_name=data.full_name,
        email=data.email,
        password_hash=hash_password(data.password),
        phone=data.phone,
        role=Role.PATIENT,
        is_active=True
    )
    await user_repo.save(user)

    # Save in patients collection
    patient = Patient(
        user_id=str(user.id),
        gender=data.gender,
        date_of_birth=data.date_of_birth
    )
    await patient.insert()

    logger.info(f"Patient registered successfully: {data.email}")

    return UserResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        role=user.role,
        is_active=user.is_active,
        created_at=user.created_at
    )


async def register_doctor(data: DoctorRegisterRequest) -> UserResponse:
    existing_user = await user_repo.find_by_email(data.email)
    if existing_user:
        logger.warning(f"Registration failed - email already exists: {data.email}")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=AuthMessages.EMAIL_ALREADY_EXISTS
        )

    # Save in users collection only
    # Doctor profile will be created in Appointment Service
    user = User(
        full_name=data.full_name,
        email=data.email,
        password_hash=hash_password(data.password),
        phone=data.phone,
        role=Role.DOCTOR,
        is_active=False  # Admin approve karega
    )
    await user_repo.save(user)
    logger.info(f"Doctor registered: {data.email} — pending admin approval")

    return UserResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        role=user.role,
        is_active=user.is_active,
        created_at=user.created_at
    )


async def login_user(email: str, password: str) -> LoginResponse:
    user = await user_repo.find_by_email(email)
    if not user:
        logger.warning(f"Login failed - user not found: {email}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=AuthMessages.INVALID_CREDENTIALS
        )

    if not verify_password(password, user.password_hash):
        logger.warning(f"Login failed - invalid password for: {email}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=AuthMessages.INVALID_CREDENTIALS
        )

    if not user.is_active:
        logger.warning(f"Login failed - account inactive: {email}")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=AuthMessages.ACCOUNT_INACTIVE
        )

    token = create_access_token(
        user_id=str(user.id),
        email=user.email,
        role=user.role
    )
    logger.info(f"Login successful: {email} | role: {user.role}")

    return LoginResponse(
        access_token=token,
        role=user.role,
        user_id=str(user.id),
        full_name=user.full_name
    )