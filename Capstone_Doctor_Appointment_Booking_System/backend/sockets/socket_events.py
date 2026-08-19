"""Helpers for sending real-time event payloads."""

from constants.socket_constants import SOCKET_ADMIN_ROOM
from schemas.response.appointment_response import AppointmentResponse
from schemas.response.slot_response import SlotResponse
from sockets.connection_manager import connection_manager


async def emit_appointment_event(
    event: str,
    appointment: AppointmentResponse,
    doctor_user_id: str,
) -> None:
    """Notify the appointment's patient, doctor, and connected admins."""

    payload = appointment.model_dump(mode="json")
    await connection_manager.send_to_user(appointment.patient_id, event, payload)
    await connection_manager.send_to_user(doctor_user_id, event, payload)
    await connection_manager.send_to_room(SOCKET_ADMIN_ROOM, event, payload)


async def emit_slot_event(
    event: str,
    slot: SlotResponse,
    doctor_user_id: str,
) -> None:
    """Notify the managing doctor and viewers of that doctor's profile."""

    payload = slot.model_dump(mode="json")
    await connection_manager.send_to_user(doctor_user_id, event, payload)
    await connection_manager.send_to_room(
        f"doctor:{slot.doctor_id}",
        event,
        payload,
    )
