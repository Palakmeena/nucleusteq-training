from datetime import datetime, timedelta

from fastapi import HTTPException, status

from constants.messages import (
    AppointmentMessages,
    DoctorMessages,
    SlotMessages,
)
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

    doctor = await doctor_repo.find_by_id(
        data.doctor_id,
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    if not doctor.is_active:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Doctor is not active.",
        )

    slot = await slot_repo.find_by_id(
        data.slot_id,
    )

    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND,
        )

    # -----------------------------
    # Business Validations
    # -----------------------------

    if slot.doctor_id != data.doctor_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.INVALID_SLOT,
        )

    if data.appointment_date != slot.date:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.SLOT_DATE_MISMATCH,
        )

    appointment_date = datetime.strptime(
        data.appointment_date,
        "%Y-%m-%d",
    ).date()

    if appointment_date < datetime.utcnow().date():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.APPOINTMENT_DATE_IN_PAST,
        )

    # -----------------------------
    # Booking Validation
    # -----------------------------

    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=SlotMessages.SLOT_ALREADY_BOOKED,
        )

    existing = await appointment_repo.find_by_slot(
        data.slot_id,
    )

    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=SlotMessages.SLOT_ALREADY_BOOKED,
        )

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

    return AppointmentResponse.model_validate(
        appointment,
    )


async def process_payment(
    appointment_id: str,
    patient_id: str,
) -> AppointmentResponse:

    appointment = await appointment_repo.find_by_id_and_patient(
        appointment_id,
        patient_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND,
        )

    appointment.payment_status = PaymentStatus.PAID
    appointment.status = AppointmentStatus.CONFIRMED
    appointment.updated_at = datetime.utcnow()

    await appointment_repo.update(
        appointment,
    )

    logger.info(
        f"Payment successful: {appointment.id}"
    )

    return AppointmentResponse.model_validate(
        appointment,
    )


async def cancel_appointment(
    appointment_id: str,
    patient_id: str,
) -> dict:

    appointment = await appointment_repo.find_by_id_and_patient(
        appointment_id,
        patient_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND,
        )

    if appointment.status == AppointmentStatus.CANCELLED:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.ALREADY_CANCELLED,
        )

    appointment_datetime = datetime.strptime(
        f"{appointment.appointment_date} {appointment.start_time}",
        "%Y-%m-%d %H:%M",
    )

    if datetime.utcnow() >= appointment_datetime - timedelta(hours=2):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.CANNOT_CANCEL,
        )

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
        "message": AppointmentMessages.CANCELLED_SUCCESS,
    }


async def get_patient_appointments(
    patient_id: str,
) -> list[AppointmentResponse]:

    appointments = await appointment_repo.find_by_patient(
        patient_id,
    )

    return [
        AppointmentResponse.model_validate(a)
        for a in appointments
    ]


async def get_doctor_appointments(
    doctor_id: str,
) -> list[AppointmentResponse]:

    appointments = await appointment_repo.find_by_doctor(
        doctor_id,
    )

    return [
        AppointmentResponse.model_validate(a)
        for a in appointments
    ]


async def update_appointment_status(
    appointment_id: str,
    doctor_id: str,
    data: AppointmentStatusRequest,
) -> AppointmentResponse:

    appointment = await appointment_repo.find_by_id_and_doctor(
        appointment_id,
        doctor_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND,
        )

    appointment.status = AppointmentStatus(
        data.status,
    )

    appointment.updated_at = datetime.utcnow()

    await appointment_repo.update(
        appointment,
    )

    logger.info(
        f"Appointment {appointment.id} updated to {appointment.status}"
    )

    return AppointmentResponse.model_validate(
        appointment,
    )