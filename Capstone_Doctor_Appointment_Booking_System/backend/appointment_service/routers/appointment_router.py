from fastapi import APIRouter, Depends
from schemas.request.appointment_request import AppointmentBookRequest, AppointmentStatusRequest
from schemas.response.appointment_response import AppointmentResponse
from services.appointment_service import (
    book_appointment,
    process_payment,
    cancel_appointment,
    get_patient_appointments,
    get_doctor_appointments,
    update_appointment_status
)
from middleware.auth_middleware import require_role

router = APIRouter(prefix="/api/v1/appointments", tags=["Appointments"])


@router.post("", response_model=AppointmentResponse, status_code=201)
async def book(
    data: AppointmentBookRequest,
    current_user: dict = Depends(require_role("PATIENT"))
):
    return await book_appointment(data, current_user["sub"])


@router.post("/{appointment_id}/pay", response_model=AppointmentResponse)
async def pay(
    appointment_id: str,
    current_user: dict = Depends(require_role("PATIENT"))
):
    return await process_payment(appointment_id, current_user["sub"])


@router.delete("/{appointment_id}")
async def cancel(
    appointment_id: str,
    current_user: dict = Depends(require_role("PATIENT"))
):
    return await cancel_appointment(appointment_id, current_user["sub"])


@router.get("/my", response_model=list[AppointmentResponse])
async def my_appointments(
    current_user: dict = Depends(require_role("PATIENT"))
):
    return await get_patient_appointments(current_user["sub"])


@router.get("/doctor", response_model=list[AppointmentResponse])
async def doctor_appointments(
    current_user: dict = Depends(require_role("DOCTOR"))
):
    doctor_id = current_user["sub"]
    return await get_doctor_appointments(doctor_id)


@router.patch("/{appointment_id}/status", response_model=AppointmentResponse)
async def update_status(
    appointment_id: str,
    data: AppointmentStatusRequest,
    current_user: dict = Depends(require_role("DOCTOR"))
):
    return await update_appointment_status(
        appointment_id,
        data,
        current_user["sub"]
    )