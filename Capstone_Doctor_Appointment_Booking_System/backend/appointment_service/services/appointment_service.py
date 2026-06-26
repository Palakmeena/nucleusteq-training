from fastapi import HTTPException, status
from datetime import datetime, timedelta
from models.appointment import Appointment, AppointmentStatus, PaymentStatus
from schemas.request.appointment_request import AppointmentBookRequest, AppointmentStatusRequest
from schemas.response.appointment_response import AppointmentResponse
from repositories.appointment_repository import AppointmentRepository
from repositories.slot_repository import SlotRepository
from repositories.doctor_repository import DoctorRepository
from constants.messages import AppointmentMessages, SlotMessages, DoctorMessages
from utils.logger import get_logger

logger = get_logger(__name__)
appointment_repo = AppointmentRepository()
slot_repo = SlotRepository()
doctor_repo = DoctorRepository()


async def book_appointment(data: AppointmentBookRequest, patient_id: str) -> AppointmentResponse:
    # Check doctor exists and is active
    doctor = await doctor_repo.find_by_id(data.doctor_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )
    if not doctor.is_active:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Doctor is not active"
        )

    # Check slot exists and is available
    slot = await slot_repo.find_by_id(data.slot_id)
    if not slot:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=SlotMessages.SLOT_NOT_FOUND
        )
    if slot.is_booked:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=SlotMessages.SLOT_ALREADY_BOOKED
        )

    # Check existing appointment for same slot
    existing = await appointment_repo.find_by_slot(data.slot_id)
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=SlotMessages.SLOT_ALREADY_BOOKED
        )

    # Mark slot as booked
    slot.is_booked = True
    await slot_repo.update(slot)

    # Create appointment
    appointment = Appointment(
        patient_id=patient_id,
        doctor_id=data.doctor_id,
        slot_id=data.slot_id,
        appointment_date=data.appointment_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=AppointmentStatus.PENDING,
        payment_status=PaymentStatus.PENDING
    )
    await appointment_repo.save(appointment)
    logger.info(f"Appointment booked: patient {patient_id} with doctor {data.doctor_id}")

    return AppointmentResponse(
        id=str(appointment.id),
        patient_id=appointment.patient_id,
        doctor_id=appointment.doctor_id,
        slot_id=appointment.slot_id,
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        payment_status=appointment.payment_status,
        created_at=appointment.created_at,
        updated_at=appointment.updated_at
    )


async def process_payment(appointment_id: str, patient_id: str) -> AppointmentResponse:
    appointment = await appointment_repo.find_by_id(appointment_id)
    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND
        )

    if appointment.patient_id != patient_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized"
        )

    appointment.payment_status = PaymentStatus.PAID
    appointment.status = AppointmentStatus.CONFIRMED
    appointment.updated_at = datetime.utcnow()
    await appointment_repo.update(appointment)
    logger.info(f"Payment processed: appointment {appointment_id}")

    return AppointmentResponse(
        id=str(appointment.id),
        patient_id=appointment.patient_id,
        doctor_id=appointment.doctor_id,
        slot_id=appointment.slot_id,
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        payment_status=appointment.payment_status,
        created_at=appointment.created_at,
        updated_at=appointment.updated_at
    )


async def cancel_appointment(appointment_id: str, patient_id: str) -> dict:
    appointment = await appointment_repo.find_by_id(appointment_id)
    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND
        )

    if appointment.patient_id != patient_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized"
        )

    if appointment.status == AppointmentStatus.CANCELLED:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.ALREADY_CANCELLED
        )

    # 2 hour rule check
    appointment_datetime = datetime.strptime(
        f"{appointment.appointment_date} {appointment.start_time}",
        "%Y-%m-%d %H:%M"
    )
    if datetime.utcnow() >= appointment_datetime - timedelta(hours=2):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=AppointmentMessages.CANNOT_CANCEL
        )

    # Free up slot
    slot = await slot_repo.find_by_id(appointment.slot_id)
    if slot:
        slot.is_booked = False
        await slot_repo.update(slot)

    appointment.status = AppointmentStatus.CANCELLED
    appointment.updated_at = datetime.utcnow()
    await appointment_repo.update(appointment)
    logger.info(f"Appointment cancelled: {appointment_id}")

    return {"message": AppointmentMessages.CANCELLED_SUCCESS}


async def get_patient_appointments(patient_id: str):
    appointments = await appointment_repo.find_by_patient(patient_id)
    return [
        AppointmentResponse(
            id=str(a.id),
            patient_id=a.patient_id,
            doctor_id=a.doctor_id,
            slot_id=a.slot_id,
            appointment_date=a.appointment_date,
            start_time=a.start_time,
            end_time=a.end_time,
            status=a.status,
            payment_status=a.payment_status,
            created_at=a.created_at,
            updated_at=a.updated_at
        ) for a in appointments
    ]


async def get_doctor_appointments(doctor_id: str):
    appointments = await appointment_repo.find_by_doctor(doctor_id)
    return [
        AppointmentResponse(
            id=str(a.id),
            patient_id=a.patient_id,
            doctor_id=a.doctor_id,
            slot_id=a.slot_id,
            appointment_date=a.appointment_date,
            start_time=a.start_time,
            end_time=a.end_time,
            status=a.status,
            payment_status=a.payment_status,
            created_at=a.created_at,
            updated_at=a.updated_at
        ) for a in appointments
    ]


async def update_appointment_status(
    appointment_id: str,
    data: AppointmentStatusRequest,
    doctor_id: str
) -> AppointmentResponse:
    appointment = await appointment_repo.find_by_id(appointment_id)
    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=AppointmentMessages.APPOINTMENT_NOT_FOUND
        )

    if appointment.doctor_id != doctor_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized"
        )

    appointment.status = data.status
    appointment.updated_at = datetime.utcnow()
    await appointment_repo.update(appointment)
    logger.info(f"Appointment status updated: {appointment_id} -> {data.status}")

    return AppointmentResponse(
        id=str(appointment.id),
        patient_id=appointment.patient_id,
        doctor_id=appointment.doctor_id,
        slot_id=appointment.slot_id,
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        payment_status=appointment.payment_status,
        created_at=appointment.created_at,
        updated_at=appointment.updated_at
    )