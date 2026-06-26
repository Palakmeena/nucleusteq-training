from models.slot import Slot
from typing import Optional


class SlotRepository:

    async def find_by_id(self, slot_id: str) -> Optional[Slot]:
        return await Slot.get(slot_id)

    async def find_by_doctor_and_date(self, doctor_id: str, date: str):
        return await Slot.find(
            {"doctor_id": doctor_id, "date": date}
        ).to_list()

    async def find_available_by_doctor(self, doctor_id: str):
        return await Slot.find(
            {"doctor_id": doctor_id, "is_booked": False}
        ).to_list()

    async def save(self, slot: Slot) -> Slot:
        await slot.insert()
        return slot

    async def update(self, slot: Slot) -> Slot:
        await slot.save()
        return slot

    async def delete(self, slot: Slot) -> None:
        await slot.delete()