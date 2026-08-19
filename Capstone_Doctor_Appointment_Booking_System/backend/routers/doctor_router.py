"""Doctor API routes."""

from typing import Optional

from fastapi import APIRouter, Depends

from dependencies.authentication_dependency import require_doctor
from schemas.request.doctor_request import (
    DoctorUpdateRequest,
)
from schemas.response.doctor_response import (
    DoctorListResponse,
    DoctorResponse,
)
from services.doctor_service import (
    get_doctor_by_id,
    get_doctor_by_user_id,
    search_doctors,
    update_doctor_profile,
)

router = APIRouter(
    prefix="/api/v1/doctors",
    tags=["Doctors"],
)


@router.get(
    "/profile",
    response_model=DoctorResponse,
)
async def get_profile(
    current_user: dict = Depends(require_doctor),
):
    """Retrieve the current doctor's profile."""

    return await get_doctor_by_user_id(
        user_id=current_user["sub"],
    )


@router.put(
    "/profile",
    response_model=DoctorResponse,
)
async def update_profile(
    data: DoctorUpdateRequest,
    current_user: dict = Depends(require_doctor),
):
    """Update the current doctor's profile."""

    return await update_doctor_profile(
        data=data,
        user_id=current_user["sub"],
    )


@router.get(
    "",
    response_model=list[DoctorListResponse],
)
async def get_doctors(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
    location: Optional[str] = None,
    min_experience: Optional[int] = None,
    max_fee: Optional[float] = None,
):
    """Search active doctors using optional filters."""

    return await search_doctors(
        name=name,
        specialization=specialization,
        location=location,
        min_experience=min_experience,
        max_fee=max_fee,
    )


@router.get(
    "/{doctor_id}",
    response_model=DoctorResponse,
)
async def get_doctor(
    doctor_id: str,
):
    """Fetch a doctor profile by id."""

    return await get_doctor_by_id(
        doctor_id,
    )
