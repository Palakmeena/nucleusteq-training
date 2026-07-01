from typing import Optional

from fastapi import APIRouter, Depends, status

from middleware.auth_middleware import require_doctor
from schemas.request.doctor_request import (
    DoctorProfileRequest,
    DoctorUpdateRequest,
)
from schemas.response.doctor_response import (
    DoctorListResponse,
    DoctorResponse,
)
from services.doctor_service import (
    create_doctor_profile,
    get_doctor_by_id,
    search_doctors,
    update_doctor_profile,
)

router = APIRouter(
    prefix="/api/v1/doctors",
    tags=["Doctors"],
)


@router.post(
    "/profile",
    response_model=DoctorResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create_profile(
    data: DoctorProfileRequest,
    current_user: dict = Depends(require_doctor),
):
    return await create_doctor_profile(
        data=data,
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
):
    return await search_doctors(
        name=name,
        specialization=specialization,
    )


@router.get(
    "/{doctor_id}",
    response_model=DoctorResponse,
)
async def get_doctor(
    doctor_id: str,
):
    return await get_doctor_by_id(
        doctor_id
    )