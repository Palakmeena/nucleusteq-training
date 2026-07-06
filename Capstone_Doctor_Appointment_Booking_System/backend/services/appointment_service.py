"""Appointment service operations."""

from datetime import datetime, timedelta

from constants.appointment_constants import CANCELLED_SUCCESS
from exceptions.appointment_exceptions import (
    AppointmentAlreadyCancelledException,
    AppointmentCancellationException,
    AppointmentNotFoundException,
    InvalidAppointmentStatusException,
)
from exceptions.doctor_exceptions import (
    DoctorInactiveException,
    DoctorNotFoundException,
)
from exceptions.slot_exceptions import (
    SlotAlreadyBookedException,
    SlotNotFoundException,
)
from mappers.appointment_mapper import AppointmentMapper
from models.appointment import (
    Appointment,
    AppointmentStatus,
    PaymentStatus,
)
from repositories.appointment_repository import AppointmentRepository
from repositories.doctor_repository import DoctorRepository
from repositories.slot_repository import SlotRepository
from schemas.request.appointment_request import (
    AppointmentBookRequest,
    AppointmentStatusRequest,
)
from schemas.response.appointment_response import (
    AppointmentResponse,
)
from utils.logger import get_logger

logger = get_logger(__name__)

appointment_repo = AppointmentRepository()
doctor_repo = DoctorRepository()
slot_repo = SlotRepository()


async def book_appointment(
    patient_id: str,
    data: AppointmentBookRequest,
) -> AppointmentResponse:
    """Book a slot for the given patient."""

    doctor = await doctor_repo.find_by_id(
        data.doctor_id,
    )

    if not doctor:
        raise DoctorNotFoundException()

    if not doctor.is_active:
        raise DoctorInactiveException()

    slot = await slot_repo.find_by_id(
        data.slot_id,
    )

    if not slot:
        raise SlotNotFoundException()

    # -----------------------------
    # Business Validations
    # -----------------------------

    if slot.doctor_id != data.doctor_id:
        raise InvalidAppointmentStatusException()

    if data.appointment_date != slot.date:
        raise InvalidAppointmentStatusException()

    appointment_date = datetime.strptime(
        data.appointment_date,
        "%Y-%m-%d",
    ).date()

    if appointment_date < datetime.utcnow().date():
        raise InvalidAppointmentStatusException()

    # -----------------------------
    # Booking Validation
    # -----------------------------

    if slot.is_booked:
        raise SlotAlreadyBookedException()

    existing = await appointment_repo.find_by_slot(
        data.slot_id,
    )

    if existing:
        raise SlotAlreadyBookedException()

    slot.is_booked = True

    await slot_repo.update(
        slot,
    )

    appointment = Appointment(
        patient_id=patient_id,
        doctor_id=data.doctor_id,
        slot_id=data.slot_id,
        appointment_date=data.appointment_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=AppointmentStatus.PENDING,
        payment_status=PaymentStatus.PENDING,
    )

    await appointment_repo.save(
        appointment,
    )

    logger.info(
        f"Appointment booked: {appointment.id}"
    )

    return AppointmentMapper.to_response(
        appointment,
    )


async def process_payment(
    appointment_id: str,
    patient_id: str,
) -> AppointmentResponse:
    """Mark a patient appointment as paid."""

    appointment = await appointment_repo.find_by_id_and_patient(
        appointment_id,
        patient_id,
    )

    if not appointment:
        raise AppointmentNotFoundException()

    appointment.payment_status = PaymentStatus.PAID
    appointment.status = AppointmentStatus.CONFIRMED
    appointment.updated_at = datetime.utcnow()

    await appointment_repo.update(
        appointment,
    )

    logger.info(
        f"Payment successful: {appointment.id}"
    )

    return AppointmentMapper.to_response(
        appointment,
    )


async def cancel_appointment(
    appointment_id: str,
    patient_id: str,
) -> dict:
    """Cancel a patient appointment when it is still allowed."""

    appointment = await appointment_repo.find_by_id_and_patient(
        appointment_id,
        patient_id,
    )

    if not appointment:
        raise AppointmentNotFoundException()

    if appointment.status == AppointmentStatus.CANCELLED:
        raise AppointmentAlreadyCancelledException()

    appointment_datetime = datetime.strptime(
        f"{appointment.appointment_date} {appointment.start_time}",
        "%Y-%m-%d %H:%M",
    )

    if datetime.utcnow() >= appointment_datetime - timedelta(hours=2):
        raise AppointmentCancellationException()

    slot = await slot_repo.find_by_id(
        appointment.slot_id,
    )

    if slot:
        slot.is_booked = False
        await slot_repo.update(
            slot,
        )

    appointment.status = AppointmentStatus.CANCELLED
    appointment.updated_at = datetime.utcnow()

    await appointment_repo.update(
        appointment,
    )

    logger.info(
        f"Appointment cancelled: {appointment.id}"
    )

    return {
        "message": CANCELLED_SUCCESS,
    }


async def get_patient_appointments(
    patient_id: str,
) -> list[AppointmentResponse]:
    """Return all appointments for a patient."""

    appointments = await appointment_repo.find_by_patient(
        patient_id,
    )

    return [
        AppointmentMapper.to_response(a)
        for a in appointments
    ]


async def get_doctor_appointments(
    doctor_id: str,
) -> list[AppointmentResponse]:
    """Return all appointments for a doctor."""

    appointments = await appointment_repo.find_by_doctor(
        doctor_id,
    )

    return [
        AppointmentMapper.to_response(a)
        for a in appointments
    ]


async def update_appointment_status(
    appointment_id: str,
    doctor_id: str,
    data: AppointmentStatusRequest,
) -> AppointmentResponse:
    """Update the status of a doctor's appointment."""

    appointment = await appointment_repo.find_by_id_and_doctor(
        appointment_id,
        doctor_id,
    )

    if not appointment:
        raise AppointmentNotFoundException()

    new_status = AppointmentStatus(
        data.status,
    )

    if new_status in (
        AppointmentStatus.COMPLETED,
        AppointmentStatus.NO_SHOW,
    ):
        appointment_end = datetime.strptime(
            f"{appointment.appointment_date} {appointment.end_time}",
            "%Y-%m-%d %H:%M",
        )

        if datetime.utcnow() < appointment_end:
            raise InvalidAppointmentStatusException()

    appointment.status = new_status
    appointment.updated_at = datetime.utcnow()

    await appointment_repo.update(
        appointment,
    )

    logger.info(
        f"Appointment {appointment.id} updated to {appointment.status}"
    )

    return AppointmentMapper.to_response(
        appointment,
    )