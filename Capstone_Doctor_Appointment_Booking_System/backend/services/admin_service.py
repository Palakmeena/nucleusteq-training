from fastapi import HTTPException, status

from constants.messages import DoctorMessages
from models.appointment import AppointmentStatus
from repositories.appointment_repository import AppointmentRepository
from repositories.doctor_repository import DoctorRepository
from repositories.user_repository import UserRepository
from schemas.response.auth_response import UserResponse
from schemas.response.doctor_response import DoctorResponse
from utils.logger import get_logger

logger = get_logger(__name__)

user_repo = UserRepository()
doctor_repo = DoctorRepository()
appointment_repo = AppointmentRepository()


async def get_all_users() -> list[UserResponse]:
    users = await user_repo.find_all()

    return [
        UserResponse.model_validate(user)
        for user in users
    ]


async def get_all_doctors() -> list[DoctorResponse]:
    doctors = await doctor_repo.find_all()

    return [
        DoctorResponse.model_validate(doctor)
        for doctor in doctors
    ]


async def activate_doctor(
    doctor_id: str,
) -> DoctorResponse:

    doctor = await doctor_repo.find_by_id(
        doctor_id
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    user = await user_repo.find_by_id(
        doctor.user_id
    )

    doctor.is_active = True

    if user:
        user.is_active = True
        await user_repo.update(user)

    await doctor_repo.update(doctor)

    logger.info(
        f"Doctor approved: {doctor.id}"
    )

    return DoctorResponse.model_validate(
        doctor
    )


async def deactivate_doctor(
    doctor_id: str,
) -> DoctorResponse:

    doctor = await doctor_repo.find_by_id(
        doctor_id
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND,
        )

    user = await user_repo.find_by_id(
        doctor.user_id
    )

    doctor.is_active = False

    if user:
        user.is_active = False
        await user_repo.update(user)

    await doctor_repo.update(doctor)

    logger.info(
        f"Doctor deactivated: {doctor.id}"
    )

    return DoctorResponse.model_validate(
        doctor
    )


async def get_dashboard_stats() -> dict:

    total_users = await user_repo.find_all()
    total_doctors = await doctor_repo.find_all()

    total_appointments = await appointment_repo.count()

    completed = await appointment_repo.count_by_status(
        AppointmentStatus.COMPLETED
    )

    cancelled = await appointment_repo.count_by_status(
        AppointmentStatus.CANCELLED
    )

    active_doctors = len(
        [
            doctor
            for doctor in total_doctors
            if doctor.is_active
        ]
    )

    return {
        "total_users": len(total_users),
        "total_doctors": len(total_doctors),
        "active_doctors": active_doctors,
        "total_appointments": total_appointments,
        "completed_appointments": completed,
        "cancelled_appointments": cancelled,
    }