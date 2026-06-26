from fastapi import APIRouter, Depends
from schemas.request.slot_request import SlotCreateRequest, SlotUpdateRequest
from schemas.response.slot_response import SlotResponse
from services.slot_service import (
    create_slot,
    update_slot,
    delete_slot,
    get_slots_by_doctor
)
from middleware.auth_middleware import require_role

router = APIRouter(prefix="/api/v1/slots", tags=["Slots"])


@router.post("", response_model=SlotResponse, status_code=201)
async def create(
    data: SlotCreateRequest,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await create_slot(data, current_user["sub"])


@router.put("/{slot_id}", response_model=SlotResponse)
async def update(
    slot_id: str,
    data: SlotUpdateRequest,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await update_slot(slot_id, data, current_user["sub"])


@router.delete("/{slot_id}")
async def delete(
    slot_id: str,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await delete_slot(slot_id, current_user["sub"])


@router.get("/doctor/{doctor_id}", response_model=list[SlotResponse])
async def get_by_doctor(doctor_id: str):
    return await get_slots_by_doctor(doctor_id)