from typing import Optional

from models.slot import Slot


class SlotRepository:

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
        return await Slot.find_one(
            {
                "_id": slot_id,
                "doctor_id": doctor_id,
            }
        )

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