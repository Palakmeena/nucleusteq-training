"""Doctor service operations."""

from fastapi import HTTPException, status

from constants.doctor_constants import DoctorMessages
from repositories.doctor_repository import DoctorRepository
from schemas.request.doctor_request import DoctorUpdateRequest
from schemas.response.doctor_response import (
    DoctorListResponse,
    DoctorResponse,
)
from utils.logger import get_logger

logger = get_logger(__name__)

doctor_repo = DoctorRepository()


async def get_doctor_by_id(
    doctor_id: str,
) -> DoctorResponse:
    """Retrieve a doctor profile by id."""

    doctor = await doctor_repo.find_by_id(doctor_id)

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    return DoctorResponse.model_validate(doctor)


async def search_doctors(
    name: str | None = None,
    specialization: str | None = None,
) -> list[DoctorListResponse]:
    """Search active doctors by name or specialization."""

    doctors = await doctor_repo.search(
        name=name,
        specialization=specialization,
    )

    return [
        DoctorListResponse.model_validate(doctor)
        for doctor in doctors
    ]


async def update_doctor_profile(
    user_id: str,
    data: DoctorUpdateRequest,
) -> DoctorResponse:
    """Update the logged-in doctor's profile."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    update_data = data.model_dump(exclude_unset=True)

    for field, value in update_data.items():
        setattr(doctor, field, value)

    await doctor_repo.update(doctor)

    logger.info(
        f"Doctor profile updated: {doctor.user_id}"
    )

    return DoctorResponse.model_validate(doctor)