from fastapi import APIRouter, Depends
from typing import Optional
from schemas.request.doctor_request import DoctorProfileRequest, DoctorUpdateRequest
from schemas.response.doctor_response import DoctorResponse, DoctorListResponse
from services.doctor_service import (
    create_doctor_profile,
    update_doctor_profile,
    search_doctors,
    get_doctor_by_id
)
from middleware.auth_middleware import require_role

router = APIRouter(prefix="/api/v1/doctors", tags=["Doctors"])


@router.post("/profile", response_model=DoctorResponse, status_code=201)
async def create_profile(
    data: DoctorProfileRequest,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await create_doctor_profile(data, current_user["sub"])


@router.put("/profile", response_model=DoctorResponse)
async def update_profile(
    data: DoctorUpdateRequest,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await update_doctor_profile(data, current_user["sub"])


@router.get("", response_model=list[DoctorListResponse])
async def search(
    name: Optional[str] = None,
    specialization: Optional[str] = None
):
    return await search_doctors(name, specialization)


@router.get("/{doctor_id}", response_model=DoctorResponse)
async def get_doctor(doctor_id: str):
    return await get_doctor_by_id(doctor_id)