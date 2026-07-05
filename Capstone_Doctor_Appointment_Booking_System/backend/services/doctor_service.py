"""Doctor service operations."""

from exceptions.doctor_exceptions import DoctorNotFoundException
from mappers.doctor_mapper import DoctorMapper
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

    doctor = await doctor_repo.find_by_id(
        doctor_id,
    )

    if not doctor:
        raise DoctorNotFoundException()

    return DoctorMapper.to_response(
        doctor,
    )


async def search_doctors(
    name: str | None = None,
    specialization: str | None = None,
    location: str | None = None,
    min_experience: int | None = None,
    max_fee: float | None = None,
) -> list[DoctorListResponse]:
    """Search active doctors using optional filters."""

    doctors = await doctor_repo.search(
        name=name,
        specialization=specialization,
        location=location,
        min_experience=min_experience,
        max_fee=max_fee,
    )

    return [
        DoctorMapper.to_list_response(
            doctor,
        )
        for doctor in doctors
    ]


async def update_doctor_profile(
    user_id: str,
    data: DoctorUpdateRequest,
) -> DoctorResponse:
    """Update the logged-in doctor's profile."""

    doctor = await doctor_repo.find_by_user_id(
        user_id,
    )

    if not doctor:
        raise DoctorNotFoundException()

    update_data = data.model_dump(
        exclude_unset=True,
    )

    for field, value in update_data.items():
        setattr(
            doctor,
            field,
            value,
        )

    await doctor_repo.update(
        doctor,
    )

    logger.info(
        f"Doctor profile updated: {doctor.user_id}",
    )

    return DoctorMapper.to_response(
        doctor,
    )