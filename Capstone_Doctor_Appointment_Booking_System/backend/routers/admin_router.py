from fastapi import APIRouter, Depends

from middleware.auth_middleware import require_admin
from schemas.response.doctor_response import DoctorResponse
from schemas.response.auth_response import UserResponse
from services.admin_service import (
    activate_doctor,
    deactivate_doctor,
    get_all_doctors,
    get_all_users,
    get_dashboard_stats,
)

router = APIRouter(
    prefix="/api/v1/admin",
    tags=["Admin"],
)


# ===========================
# Dashboard
# ===========================

@router.get("/dashboard")
async def dashboard(
    current_user: dict = Depends(require_admin),
):
    return await get_dashboard_stats()


# ===========================
# User Management
# ===========================

@router.get(
    "/users",
    response_model=list[UserResponse],
)
async def users(
    current_user: dict = Depends(require_admin),
):
    return await get_all_users()


# ===========================
# Doctor Management
# ===========================

@router.get(
    "/doctors",
    response_model=list[DoctorResponse],
)
async def doctors(
    current_user: dict = Depends(require_admin),
):
    return await get_all_doctors()


@router.patch(
    "/doctors/{doctor_id}/activate",
    response_model=DoctorResponse,
)
async def activate(
    doctor_id: str,
    current_user: dict = Depends(require_admin),
):
    return await activate_doctor(
        doctor_id
    )


@router.patch(
    "/doctors/{doctor_id}/deactivate",
    response_model=DoctorResponse,
)
async def deactivate(
    doctor_id: str,
    current_user: dict = Depends(require_admin),
):
    return await deactivate_doctor(
        doctor_id
    )