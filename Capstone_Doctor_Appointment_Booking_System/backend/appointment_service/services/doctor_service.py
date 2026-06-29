from fastapi import HTTPException, status
from models.doctor import Doctor
from schemas.request.doctor_request import DoctorProfileRequest, DoctorUpdateRequest
from schemas.response.doctor_response import DoctorResponse, DoctorListResponse
from repositories.doctor_repository import DoctorRepository
from constants.messages import DoctorMessages
from utils.logger import get_logger

logger = get_logger(__name__)
doctor_repo = DoctorRepository()


async def create_doctor_profile(data: DoctorProfileRequest, user_id: str) -> DoctorResponse:
    existing = await doctor_repo.find_by_user_id(user_id)
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=DoctorMessages.PROFILE_ALREADY_EXISTS
        )

    existing_license = await doctor_repo.find_by_license(data.license_number)
    if existing_license:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="License number already registered"
        )

    doctor = Doctor(
        user_id=user_id,
        full_name=data.full_name,
        phone=data.phone,
        qualification=data.qualification,
        experience=data.experience,
        license_number=data.license_number,
        specialization=data.specialization,
        consultation_fee=data.consultation_fee,
        clinic_address=data.clinic_address,
        is_active=False
    )
    await doctor_repo.save(doctor)
    logger.info(f"Doctor profile created: {user_id}")

    return DoctorResponse(
        id=str(doctor.id),
        user_id=doctor.user_id,
        full_name=doctor.full_name,
        phone=doctor.phone,
        qualification=doctor.qualification,
        experience=doctor.experience,
        license_number=doctor.license_number,
        specialization=doctor.specialization,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        is_active=doctor.is_active,
        created_at=doctor.created_at
    )


async def update_doctor_profile(data: DoctorUpdateRequest, user_id: str) -> DoctorResponse:
    doctor = await doctor_repo.find_by_user_id(user_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )

    if data.qualification:
        doctor.qualification = data.qualification
    if data.experience:
        doctor.experience = data.experience
    if data.specialization:
        doctor.specialization = data.specialization
    if data.consultation_fee:
        doctor.consultation_fee = data.consultation_fee
    if data.clinic_address:
        doctor.clinic_address = data.clinic_address

    await doctor_repo.update(doctor)
    logger.info(f"Doctor profile updated: {user_id}")

    return DoctorResponse(
        id=str(doctor.id),
        user_id=doctor.user_id,
        full_name=doctor.full_name,
        phone=doctor.phone,
        qualification=doctor.qualification,
        experience=doctor.experience,
        license_number=doctor.license_number,
        specialization=doctor.specialization,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        is_active=doctor.is_active,
        created_at=doctor.created_at
    )


async def search_doctors(name: str = None, specialization: str = None):
    doctors = await doctor_repo.search(name, specialization)
    return [
        DoctorListResponse(
            id=str(d.id),
            full_name=d.full_name,
            specialization=d.specialization,
            experience=d.experience,
            consultation_fee=d.consultation_fee,
            clinic_address=d.clinic_address,
            is_active=d.is_active
        ) for d in doctors
    ]


async def get_doctor_by_id(doctor_id: str) -> DoctorResponse:
    doctor = await doctor_repo.find_by_id(doctor_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )

    return DoctorResponse(
        id=str(doctor.id),
        user_id=doctor.user_id,
        full_name=doctor.full_name,
        phone=doctor.phone,
        qualification=doctor.qualification,
        experience=doctor.experience,
        license_number=doctor.license_number,
        specialization=doctor.specialization,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        is_active=doctor.is_active,
        created_at=doctor.created_at
    )