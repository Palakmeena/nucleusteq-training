"""Slot API routes."""

from fastapi import APIRouter, Depends, status

from middleware.auth_middleware import require_doctor
from schemas.request.slot_request import (
    SlotCreateRequest,
    SlotUpdateRequest,
)
from schemas.response.slot_response import SlotResponse
from services.slot_service import (
    create_slot,
    delete_slot,
    get_slots_by_doctor,
    update_slot,
)

router = APIRouter(
    prefix="/api/v1/slots",
    tags=["Slots"],
)


@router.post(
    "",
    response_model=SlotResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create(
    data: SlotCreateRequest,
    current_user: dict = Depends(require_doctor),
):
    """Create a new slot for the current doctor."""

    return await create_slot(
        user_id=current_user["sub"],
        data=data,
    )


@router.put(
    "/{slot_id}",
    response_model=SlotResponse,
)
async def update(
    slot_id: str,
    data: SlotUpdateRequest,
    current_user: dict = Depends(require_doctor),
):
    """Update one of the current doctor's slots."""

    return await update_slot(
        slot_id=slot_id,
        user_id=current_user["sub"],
        data=data,
    )


@router.delete(
    "/{slot_id}",
)
async def delete(
    slot_id: str,
    current_user: dict = Depends(require_doctor),
):
    """Delete one of the current doctor's slots."""

    return await delete_slot(
        slot_id=slot_id,
        user_id=current_user["sub"],
    )


@router.get(
    "/doctor/{doctor_id}",
    response_model=list[SlotResponse],
)
async def get_doctor_slots(
    doctor_id: str,
):
    """List the available slots for a doctor."""

    return await get_slots_by_doctor(
        doctor_id
    )