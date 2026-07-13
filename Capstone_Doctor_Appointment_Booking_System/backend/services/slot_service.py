"""Slot service operations."""

from datetime import datetime, timezone, timedelta

from constants.slot_constants import (
    SLOT_DELETED,
)
from exceptions.doctor_exceptions import DoctorNotFoundException
from exceptions.slot_exceptions import (
    InvalidSlotTimeException,
    SlotAlreadyBookedException,
    SlotCannotBeDeletedException,
    SlotNotFoundException,
    SlotOverlapException,
    SlotInPastException,
)
from mappers.slot_mapper import SlotMapper
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
        raise DoctorNotFoundException()

    start_time = datetime.strptime(
        data.start_time,
        "%H:%M",
    )

    end_time = datetime.strptime(
        data.end_time,
        "%H:%M",
    )

    if start_time >= end_time:
        raise InvalidSlotTimeException()

    # Check if the selected date and start time is in the past
    ist_tz = timezone(timedelta(hours=5, minutes=30))
    slot_datetime_str = f"{data.date} {data.start_time}"
    slot_datetime = datetime.strptime(slot_datetime_str, "%Y-%m-%d %H:%M").replace(tzinfo=ist_tz)
    
    if slot_datetime < datetime.now(ist_tz):
        raise SlotInPastException()

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
            raise SlotOverlapException()

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

    return SlotMapper.to_response(slot)


async def update_slot(
    user_id: str,
    slot_id: str,
    data: SlotUpdateRequest,
) -> SlotResponse:
    """Update one of the current doctor's slots."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    slot = await slot_repo.find_by_id_and_doctor(
        slot_id,
        str(doctor.id),
    )

    if not slot:
        raise SlotNotFoundException()

    if slot.is_booked:
        raise SlotAlreadyBookedException()

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
        raise InvalidSlotTimeException()

    # Check if the selected date and start time is in the past
    ist_tz = timezone(timedelta(hours=5, minutes=30))
    slot_datetime_str = f"{new_date} {new_start}"
    slot_datetime = datetime.strptime(slot_datetime_str, "%Y-%m-%d %H:%M").replace(tzinfo=ist_tz)
    
    if slot_datetime < datetime.now(ist_tz):
        raise SlotInPastException()

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
            raise SlotOverlapException()

    update_data = data.model_dump(exclude_unset=True)

    for field, value in update_data.items():
        setattr(slot, field, value)

    await slot_repo.update(slot)

    logger.info(
        f"Slot updated: {slot.id}"
    )

    return SlotMapper.to_response(slot)


async def delete_slot(
    user_id: str,
    slot_id: str,
) -> dict:
    """Delete one of the current doctor's slots."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    slot = await slot_repo.find_by_id_and_doctor(
        slot_id,
        str(doctor.id),
    )

    if not slot:
        raise SlotNotFoundException()

    if slot.is_booked:
        raise SlotCannotBeDeletedException()

    await slot_repo.delete(slot)

    logger.info(
        f"Slot deleted: {slot.id}"
    )

    return {
        "message": SLOT_DELETED
    }


async def get_slots_by_doctor(
    doctor_id: str,
) -> list[SlotResponse]:
    """Retrieve all available slots for a doctor."""

    slots = await slot_repo.find_all_by_doctor(
        doctor_id
    )

    return [
        SlotMapper.to_response(slot)
        for slot in slots
    ]


async def get_my_slots(
    user_id: str,
) -> list[SlotResponse]:
    """Retrieve all slots for the current doctor (including booked)."""

    doctor = await doctor_repo.find_by_user_id(user_id)

    if not doctor:
        raise DoctorNotFoundException()

    slots = await slot_repo.find_all_by_doctor(
        str(doctor.id)
    )

    return [
        SlotMapper.to_response(slot)
        for slot in slots
    ]