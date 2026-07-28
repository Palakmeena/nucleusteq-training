"""Admin API routes."""

from fastapi import APIRouter, Depends

from middleware.auth_middleware import require_admin
from schemas.response.doctor_response import DoctorResponse
from schemas.response.auth_response import UserResponse
from schemas.response.admin_response import DashboardResponse
from schemas.response.deactivation_response import DeactivationRequestAdminResponse
from services.admin_service import (
    approve_doctor,
    reject_doctor,
    get_all_doctors,
    get_all_users,
    get_dashboard_stats,
    get_recent_appointments,
    get_all_deactivation_requests,
    approve_deactivation_request,
    reject_deactivation_request,
)

router = APIRouter(
    prefix="/api/v1/admin",
    tags=["Admin"],
)


@router.get(
    "/dashboard",
    response_model=DashboardResponse,
)
async def dashboard(
    current_user: dict = Depends(require_admin),
):
    """Return dashboard statistics."""

    return await get_dashboard_stats()


@router.get(
    "/users",
    response_model=list[UserResponse],
)
async def users(
    current_user: dict = Depends(require_admin),
):
    """List all users."""

    return await get_all_users()


@router.get(
    "/doctors",
    response_model=list[DoctorResponse],
)
async def doctors(
    current_user: dict = Depends(require_admin),
):
    """List all doctors."""

    return await get_all_doctors()



@router.patch(
    "/doctors/{doctor_id}/approve",
    response_model=DoctorResponse,
)
async def approve(
    doctor_id: str,
    current_user: dict = Depends(require_admin),
):
    """Approve a doctor's registration."""

    return await approve_doctor(doctor_id)


@router.patch(
    "/doctors/{doctor_id}/reject",
    response_model=DoctorResponse,
)
async def reject(
    doctor_id: str,
    current_user: dict = Depends(require_admin),
):
    """Reject a doctor's registration."""

    return await reject_doctor(doctor_id)


@router.get(
    "/deactivation-requests",
    response_model=list[DeactivationRequestAdminResponse],
)
async def list_deactivation_requests(
    current_user: dict = Depends(require_admin),
):
    """List all doctor deactivation requests."""

    return await get_all_deactivation_requests()


@router.patch(
    "/deactivation-requests/{request_id}/approve",
    response_model=DeactivationRequestAdminResponse,
)
async def approve_deactivation(
    request_id: str,
    current_user: dict = Depends(require_admin),
):
    """Approve a doctor's deactivation request and remove unbooked slots."""

    return await approve_deactivation_request(request_id)


@router.patch(
    "/deactivation-requests/{request_id}/reject",
    response_model=DeactivationRequestAdminResponse,
)
async def reject_deactivation(
    request_id: str,
    current_user: dict = Depends(require_admin),
):
    """Reject a doctor's deactivation request."""

    return await reject_deactivation_request(request_id)


@router.get(
    "/appointments/recent",
)
async def recent_appointments(
    current_user: dict = Depends(require_admin),
):
    """List recent appointments."""

    return await get_recent_appointments()