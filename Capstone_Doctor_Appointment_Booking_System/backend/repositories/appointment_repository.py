"""Appointment data access helpers."""

from typing import Optional

from enums.appointment_status import AppointmentStatus
from models.appointment import Appointment


class AppointmentRepository:
    """Repository methods for appointment documents."""

    async def find_by_id(self, appointment_id: str) -> Optional[Appointment]:
        return await Appointment.get(appointment_id)

    async def find_by_id_and_patient(
        self,
        appointment_id: str,
        patient_id: str,
    ) -> Optional[Appointment]:
        """Find appointment by id and verify it belongs to the patient."""

        appointment = await Appointment.get(appointment_id)

        if appointment and appointment.patient_id == patient_id:
            return appointment

        return None

    async def find_by_id_and_doctor(
        self,
        appointment_id: str,
        doctor_id: str,
    ) -> Optional[Appointment]:
        """Find appointment by id and verify it belongs to the doctor."""

        appointment = await Appointment.get(appointment_id)

        if appointment and appointment.doctor_id == doctor_id:
            return appointment

        return None

    async def find_by_patient(self, patient_id: str):
        return await Appointment.find(
            {
                "patient_id": patient_id,
            }
        ).to_list()

    async def find_by_doctor(self, doctor_id: str):
        return await Appointment.find(
            {
                "doctor_id": doctor_id,
            }
        ).to_list()

    async def find_by_slot(self, slot_id: str) -> Optional[Appointment]:
        return await Appointment.find_one(
            {
                "slot_id": slot_id,
            }
        )

    async def find_all(self):
        return await Appointment.find_all().to_list()

    async def count(self) -> int:
        return await Appointment.count()

    async def count_by_status(
        self,
        status: AppointmentStatus,
    ) -> int:
        return await Appointment.find(
            {
                "status": status,
            }
        ).count()

    async def save(self, appointment: Appointment) -> Appointment:
        await appointment.insert()
        return appointment

    async def update(self, appointment: Appointment) -> Appointment:
        await appointment.save()
        return appointment

    async def delete(self, appointment: Appointment) -> None:
        await appointment.delete()