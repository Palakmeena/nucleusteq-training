from fastapi import APIRouter, Depends
from schemas.response.doctor_response import DoctorResponse
from services.admin_service import (
    get_all_doctors,
    activate_doctor,
    deactivate_doctor,
    get_dashboard_stats
)
from middleware.auth_middleware import require_role

router = APIRouter(prefix="/api/v1/admin", tags=["Admin"])


@router.get("/doctors", response_model=list[DoctorResponse])
async def all_doctors(
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await get_all_doctors()


@router.patch("/doctors/{doctor_id}/activate", response_model=DoctorResponse)
async def activate(
    doctor_id: str,
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await activate_doctor(doctor_id)


@router.patch("/doctors/{doctor_id}/deactivate", response_model=DoctorResponse)
async def deactivate(
    doctor_id: str,
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await deactivate_doctor(doctor_id)


@router.get("/dashboard")
async def dashboard(
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await get_dashboard_stats()