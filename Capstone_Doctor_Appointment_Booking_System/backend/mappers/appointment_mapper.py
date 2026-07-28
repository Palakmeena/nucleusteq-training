"""Appointment mapper."""

from models.appointment import Appointment
from schemas.response.appointment_response import AppointmentResponse


class AppointmentMapper:
    """Maps Appointment documents to response schemas."""

    @staticmethod
    def to_response(
        appointment: Appointment,
        patient_name: str | None = None,
        doctor_name: str | None = None,
    ) -> AppointmentResponse:
        return AppointmentResponse(
            id=str(appointment.id),
            patient_id=appointment.patient_id,
            doctor_id=appointment.doctor_id,
            patient_name=patient_name,
            doctor_name=doctor_name,
            slot_id=appointment.slot_id,
            appointment_date=appointment.appointment_date,
            start_time=appointment.start_time,
            end_time=appointment.end_time,
            status=appointment.status,
            payment_status=appointment.payment_status,
            created_at=appointment.created_at,
            updated_at=appointment.updated_at,
        )