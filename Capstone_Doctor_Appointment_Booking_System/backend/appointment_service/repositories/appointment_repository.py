from models.appointment import Appointment, AppointmentStatus
from typing import Optional


class AppointmentRepository:

    async def find_by_id(self, appointment_id: str) -> Optional[Appointment]:
        return await Appointment.get(appointment_id)

    async def find_by_patient(self, patient_id: str):
        return await Appointment.find(
            {"patient_id": patient_id}
        ).to_list()

    async def find_by_doctor(self, doctor_id: str):
        return await Appointment.find(
            {"doctor_id": doctor_id}
        ).to_list()

    async def find_by_slot(self, slot_id: str) -> Optional[Appointment]:
        return await Appointment.find_one({"slot_id": slot_id})

    async def save(self, appointment: Appointment) -> Appointment:
        await appointment.insert()
        return appointment

    async def update(self, appointment: Appointment) -> Appointment:
        await appointment.save()
        return appointment