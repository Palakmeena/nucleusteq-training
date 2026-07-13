"""Doctor deactivation request service operations."""

from datetime import datetime

from constants.deactivation_constants import (
    DOCTOR_REACTIVATED,
    DEACTIVATION_REQUEST_SUBMITTED,
)
from enums.doctor_status import DeactivationRequestStatus
from exceptions.doctor_exceptions import DoctorNotFoundException
from exceptions.deactivation_exceptions import (
    DeactivationRequestNotFoundException,
    InvalidDateRangeException,
)
from models.deactivation_request import DeactivationRequest
from repositories.deactivation_repository import DeactivationRequestRepository
from repositories.doctor_repository import DoctorRepository
from schemas.request.deactivation_request import DeactivationRequestCreate
from schemas.response.deactivation_response import DeactivationRequestResponse
from utils.logger import get_logger

logger = get_logger(__name__)

deactivation_repo = DeactivationRequestRepository()
doctor_repo = DoctorRepository()


def _to_response(req: DeactivationRequest) -> DeactivationRequestResponse:
    """Map a DeactivationRequest document to its response schema."""
    return DeactivationRequestResponse(
        id=str(req.id),
        doctor_id=req.doctor_id,
        user_id=req.user_id,
        start_date=req.start_date,
        end_date=req.end_date,
        reason=req.reason,
        status=req.status,
        created_at=req.created_at,
        reviewed_at=req.reviewed_at,
    )


async def submit_deactivation_request(
    user_id: str,
    data: DeactivationRequestCreate,
) -> DeactivationRequestResponse:
    """Submit a temporary deactivation request. Doctor is NOT deactivated yet."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    if data.start_date > data.end_date:
        raise InvalidDateRangeException()

    req = DeactivationRequest(
        doctor_id=str(doctor.id),
        user_id=user_id,
        start_date=data.start_date,
        end_date=data.end_date,
        reason=data.reason,
    )

    await deactivation_repo.save(req)

    logger.info(
        f"Deactivation request submitted by doctor {doctor.id}: "
        f"{data.start_date} to {data.end_date}"
    )

    return _to_response(req)


async def get_my_deactivation_requests(
    user_id: str,
) -> list[DeactivationRequestResponse]:
    """Return all deactivation requests submitted by the current doctor."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    requests = await deactivation_repo.find_all_by_doctor(
        str(doctor.id)
    )

    return [_to_response(r) for r in requests]


async def reactivate_self(
    user_id: str,
) -> dict:
    """Allow a doctor to reactivate themselves immediately.

    Does NOT require admin approval — only deactivation requests do.
    """

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    doctor.is_active = True
    doctor.unavailable_from = None
    doctor.unavailable_to = None

    await doctor_repo.update(doctor)

    logger.info(f"Doctor reactivated self: {doctor.id}")

    return {"message": DOCTOR_REACTIVATED}
