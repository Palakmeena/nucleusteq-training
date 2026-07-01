from fastapi import APIRouter, Depends, status

from middleware.auth_middleware import (
    require_doctor,
    require_patient,
)
from schemas.request.appointment_request import (
    AppointmentBookRequest,
    AppointmentStatusRequest,
)
from schemas.response.appointment_response import (
    AppointmentResponse,
)
from services.appointment_service import (
    book_appointment,
    cancel_appointment,
    get_doctor_appointments,
    get_patient_appointments,
    process_payment,
    update_appointment_status,
)

router = APIRouter(
    prefix="/api/v1/appointments",
    tags=["Appointments"],
)


@router.post(
    "",
    response_model=AppointmentResponse,
    status_code=status.HTTP_201_CREATED,
)
async def book(
    data: AppointmentBookRequest,
    current_user: dict = Depends(require_patient),
):
    return await book_appointment(
        patient_id=current_user["sub"],
        data=data,
    )


@router.post(
    "/{appointment_id}/pay",
    response_model=AppointmentResponse,
)
async def pay(
    appointment_id: str,
    current_user: dict = Depends(require_patient),
):
    return await process_payment(
        appointment_id=appointment_id,
        patient_id=current_user["sub"],
    )


@router.delete(
    "/{appointment_id}",
)
async def cancel(
    appointment_id: str,
    current_user: dict = Depends(require_patient),
):
    return await cancel_appointment(
        appointment_id=appointment_id,
        patient_id=current_user["sub"],
    )


@router.get(
    "/my",
    response_model=list[AppointmentResponse],
)
async def my_appointments(
    current_user: dict = Depends(require_patient),
):
    return await get_patient_appointments(
        patient_id=current_user["sub"],
    )


@router.get(
    "/doctor",
    response_model=list[AppointmentResponse],
)
async def doctor_appointments(
    current_user: dict = Depends(require_doctor),
):
    return await get_doctor_appointments(
        doctor_id=current_user["sub"],
    )


@router.patch(
    "/{appointment_id}/status",
    response_model=AppointmentResponse,
)
async def update_status(
    appointment_id: str,
    data: AppointmentStatusRequest,
    current_user: dict = Depends(require_doctor),
):
    return await update_appointment_status(
        appointment_id=appointment_id,
        doctor_id=current_user["sub"],
        data=data,
    )