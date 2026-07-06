"""Mapper for Slot model."""

from models.slot import Slot
from schemas.response.slot_response import SlotResponse


class SlotMapper:
    """Maps Slot documents to response schemas."""

    @staticmethod
    def to_response(slot: Slot) -> SlotResponse:
        """Convert Slot document to SlotResponse."""

        return SlotResponse(
            id=str(slot.id),
            doctor_id=slot.doctor_id,
            date=slot.date,
            start_time=slot.start_time,
            end_time=slot.end_time,
            is_booked=slot.is_booked,
            created_at=slot.created_at,
        )