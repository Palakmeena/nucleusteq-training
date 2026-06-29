from fastapi import HTTPException, status
from models.slot import Slot
from schemas.request.slot_request import SlotCreateRequest, SlotUpdateRequest
from schemas.response.slot_response import SlotResponse
from repositories.slot_repository import SlotRepository
from repositories.doctor_repository import DoctorRepository
from constants.messages import SlotMessages, DoctorMessages
from utils.logger import get_logger

logger = get_logger(__name__)
slot_repo = SlotRepository()
doctor_repo = DoctorRepository()


async def create_slot(data: SlotCreateRequest, user_id: str) -> SlotResponse:
    doctor = await doctor_repo.find_by_user_id(user_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )

    slot = Slot(
        doctor_id=str(doctor.id),
        date=data.date,
        start_time=data.start_time,
        end_time=data.end_time
    )
    await slot_repo.save(slot)
    logger.info(f"Slot created by doctor: {user_id}")

    return SlotResponse(
        id=str(slot.id),
        doctor_id=slot.doctor_id,
        date=slot.date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        is_booked=slot.is_booked,
        created_at=slot.created_at
    )


async def update_slot(slot_id: str, data: SlotUpdateRequest, user_id: str) -> SlotResponse:
    slot = await slot_repo.find_by_id(slot_id)
    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND
        )

    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.SLOT_ALREADY_BOOKED
        )

    if data.date:
        slot.date = data.date
    if data.start_time:
        slot.start_time = data.start_time
    if data.end_time:
        slot.end_time = data.end_time

    await slot_repo.update(slot)
    logger.info(f"Slot updated: {slot_id}")

    return SlotResponse(
        id=str(slot.id),
        doctor_id=slot.doctor_id,
        date=slot.date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        is_booked=slot.is_booked,
        created_at=slot.created_at
    )


async def delete_slot(slot_id: str, user_id: str) -> dict:
    slot = await slot_repo.find_by_id(slot_id)
    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND
        )

    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.CANNOT_DELETE_BOOKED
        )

    await slot_repo.delete(slot)
    logger.info(f"Slot deleted: {slot_id}")
    return {"message": SlotMessages.SLOT_DELETED}


async def get_slots_by_doctor(doctor_id: str):
    slots = await slot_repo.find_available_by_doctor(doctor_id)
    return [
        SlotResponse(
            id=str(s.id),
            doctor_id=s.doctor_id,
            date=s.date,
            start_time=s.start_time,
            end_time=s.end_time,
            is_booked=s.is_booked,
            created_at=s.created_at
        ) for s in slots
    ]