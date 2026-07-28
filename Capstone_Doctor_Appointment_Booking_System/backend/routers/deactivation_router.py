"""Doctor deactivation request API routes."""

from fastapi import APIRouter, Depends

from middleware.auth_middleware import require_doctor
from schemas.request.deactivation_request import DeactivationRequestCreate
from schemas.response.deactivation_response import DeactivationRequestResponse
from services.deactivation_service import (
    get_my_deactivation_requests,
    reactivate_self,
    submit_deactivation_request,
)

router = APIRouter(
    prefix="/api/v1/doctors/deactivation",
    tags=["Doctor Deactivation"],
)


@router.post(
    "/request",
    response_model=DeactivationRequestResponse,
)
async def request_deactivation(
    data: DeactivationRequestCreate,
    current_user: dict = Depends(require_doctor),
):
    """Submit a temporary deactivation / leave request."""

    return await submit_deactivation_request(
        user_id=current_user["sub"],
        data=data,
    )


@router.get(
    "/requests",
    response_model=list[DeactivationRequestResponse],
)
async def my_deactivation_requests(
    current_user: dict = Depends(require_doctor),
):
    """List all deactivation requests submitted by the current doctor."""

    return await get_my_deactivation_requests(
        user_id=current_user["sub"],
    )


@router.patch(
    "/reactivate",
)
async def reactivate(
    current_user: dict = Depends(require_doctor),
):
    """Reactivate self immediately — no admin approval required."""

    return await reactivate_self(
        user_id=current_user["sub"],
    )
