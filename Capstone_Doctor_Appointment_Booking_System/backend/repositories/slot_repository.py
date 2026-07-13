"""Slot data access helpers."""

from typing import Optional
from bson import ObjectId

from models.slot import Slot


class SlotRepository:

    """Repository methods for slot documents."""

    async def find_by_id(
        self,
        slot_id: str,
    ) -> Optional[Slot]:
        return await Slot.get(slot_id)

    async def find_by_id_and_doctor(
        self,
        slot_id: str,
        doctor_id: str,
    ) -> Optional[Slot]:
        try:
            return await Slot.find_one(
                {
                    "_id": ObjectId(slot_id),
                    "doctor_id": doctor_id,
                }
            )
        except:
            return None

    async def find_by_doctor_and_date(
        self,
        doctor_id: str,
        date: str,
    ) -> list[Slot]:
        return await Slot.find(
            {
                "doctor_id": doctor_id,
                "date": date,
            }
        ).to_list()

    async def find_available_by_doctor(
        self,
        doctor_id: str,
    ) -> list[Slot]:
        return await Slot.find(
            {
                "doctor_id": doctor_id,
                "is_booked": False,
            }
        ).to_list()

    async def find_all_by_doctor(
        self,
        doctor_id: str,
    ) -> list[Slot]:
        return await Slot.find(
            {
                "doctor_id": doctor_id,
            }
        ).to_list()

    async def find_unbooked_in_date_range(
        self,
        doctor_id: str,
        start_date: str,
        end_date: str,
    ) -> list[Slot]:
        """Return unbooked slots for a doctor within a date range (inclusive)."""
        return await Slot.find(
            {
                "doctor_id": doctor_id,
                "is_booked": False,
                "date": {
                    "$gte": start_date,
                    "$lte": end_date,
                },
            }
        ).to_list()

    async def delete_unbooked_in_date_range(
        self,
        doctor_id: str,
        start_date: str,
        end_date: str,
    ) -> int:
        """Delete all unbooked slots for a doctor within a date range.
        Returns the number of slots deleted."""
        slots = await self.find_unbooked_in_date_range(
            doctor_id, start_date, end_date
        )
        count = 0
        for slot in slots:
            await slot.delete()
            count += 1
        return count

    async def save(
        self,
        slot: Slot,
    ) -> Slot:
        await slot.insert()
        return slot

    async def update(
        self,
        slot: Slot,
    ) -> Slot:
        await slot.save()
        return slot

    async def delete(
        self,
        slot: Slot,
    ) -> None:
        await slot.delete()