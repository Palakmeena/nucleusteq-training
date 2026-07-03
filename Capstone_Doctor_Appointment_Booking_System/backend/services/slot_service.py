"""Slot service operations."""

from datetime import datetime

from fastapi import HTTPException, status

from constants.doctor_constants import DoctorMessages
from constants.slot_constants import SlotMessages
from models.slot import Slot
from repositories.doctor_repository import DoctorRepository
from repositories.slot_repository import SlotRepository
from schemas.request.slot_request import (
    SlotCreateRequest,
    SlotUpdateRequest,
)
from schemas.response.slot_response import SlotResponse
from utils.logger import get_logger

logger = get_logger(__name__)

slot_repo = SlotRepository()
doctor_repo = DoctorRepository()


def is_overlapping(
    start1: str,
    end1: str,
    start2: str,
    end2: str,
) -> bool:
    """Return True when two time intervals overlap."""

    start1 = datetime.strptime(start1, "%H:%M")
    end1 = datetime.strptime(end1, "%H:%M")

    start2 = datetime.strptime(start2, "%H:%M")
    end2 = datetime.strptime(end2, "%H:%M")

    return start1 < end2 and start2 < end1


async def create_slot(
    user_id: str,
    data: SlotCreateRequest,
) -> SlotResponse:
    """Create a new availability slot for the current doctor."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    start_time = datetime.strptime(
        data.start_time,
        "%H:%M",
    )

    end_time = datetime.strptime(
        data.end_time,
        "%H:%M",
    )

    if start_time >= end_time:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.INVALID_SLOT_TIME,
        )

    existing_slots = await slot_repo.find_by_doctor_and_date(
        str(doctor.id),
        data.date,
    )

    for existing_slot in existing_slots:
        if is_overlapping(
            data.start_time,
            data.end_time,
            existing_slot.start_time,
            existing_slot.end_time,
        ):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=SlotMessages.SLOT_OVERLAP,
            )

    slot = Slot(
        doctor_id=str(doctor.id),
        date=data.date,
        start_time=data.start_time,
        end_time=data.end_time,
    )

    await slot_repo.save(slot)

    logger.info(
        f"Slot created by doctor: {doctor.id}"
    )

    return SlotResponse.model_validate(slot)


async def update_slot(
    user_id: str,
    slot_id: str,
    data: SlotUpdateRequest,
) -> SlotResponse:
    """Update one of the current doctor's slots."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    slot = await slot_repo.find_by_id_and_doctor(
        slot_id,
        str(doctor.id),
    )

    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND,
        )

    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.SLOT_ALREADY_BOOKED,
        )

    new_date = data.date if data.date else slot.date
    new_start = data.start_time if data.start_time else slot.start_time
    new_end = data.end_time if data.end_time else slot.end_time

    start_time = datetime.strptime(
        new_start,
        "%H:%M",
    )

    end_time = datetime.strptime(
        new_end,
        "%H:%M",
    )

    if start_time >= end_time:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.INVALID_SLOT_TIME,
        )

    existing_slots = await slot_repo.find_by_doctor_and_date(
        str(doctor.id),
        new_date,
    )

    for existing_slot in existing_slots:
        if str(existing_slot.id) == str(slot.id):
            continue

        if is_overlapping(
            new_start,
            new_end,
            existing_slot.start_time,
            existing_slot.end_time,
        ):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=SlotMessages.SLOT_OVERLAP,
            )

    update_data = data.model_dump(exclude_unset=True)

    for field, value in update_data.items():
        setattr(slot, field, value)

    await slot_repo.update(slot)

    logger.info(
        f"Slot updated: {slot.id}"
    )

    return SlotResponse.model_validate(slot)


async def delete_slot(
    user_id: str,
    slot_id: str,
) -> dict:
    """Delete one of the current doctor's slots."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    slot = await slot_repo.find_by_id_and_doctor(
        slot_id,
        str(doctor.id),
    )

    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND,
        )

    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=SlotMessages.CANNOT_DELETE_BOOKED,
        )

    await slot_repo.delete(slot)

    logger.info(
        f"Slot deleted: {slot.id}"
    )

    return {
        "message": SlotMessages.SLOT_DELETED
    }


async def get_slots_by_doctor(
    doctor_id: str,
) -> list[SlotResponse]:
    """Retrieve all available slots for a doctor."""

    slots = await slot_repo.find_available_by_doctor(
        doctor_id
    )

    return [
        SlotResponse.model_validate(slot)
        for slot in slots
    ]