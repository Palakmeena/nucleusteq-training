"""Admin service operations."""

from enums.appointment_status import AppointmentStatus
from exceptions.doctor_exceptions import DoctorNotFoundException
from mappers.doctor_mapper import DoctorMapper
from mappers.user_mapper import UserMapper
from repositories.appointment_repository import AppointmentRepository
from repositories.doctor_repository import DoctorRepository
from repositories.patient_repository import PatientRepository
from repositories.user_repository import UserRepository
from schemas.response.admin_response import DashboardResponse
from schemas.response.auth_response import UserResponse
from schemas.response.doctor_response import DoctorResponse
from utils.logger import get_logger

logger = get_logger(__name__)

user_repo = UserRepository()
doctor_repo = DoctorRepository()
patient_repo = PatientRepository()
appointment_repo = AppointmentRepository()


async def get_all_users() -> list[UserResponse]:
    """Return all users in the system."""

    users = await user_repo.find_all()

    return [
        UserMapper.to_response(user)
        for user in users
    ]


async def get_all_doctors() -> list[DoctorResponse]:
    """Return all doctors in the system."""

    doctors = await doctor_repo.find_all()

    return [
        DoctorMapper.to_response(doctor)
        for doctor in doctors
    ]


async def activate_doctor(
    doctor_id: str,
) -> DoctorResponse:
    """Activate a doctor account and its related user account."""

    doctor = await doctor_repo.find_by_id(
        doctor_id
    )

    if not doctor:
        raise DoctorNotFoundException()

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

    return DoctorMapper.to_response(
        doctor
    )


async def deactivate_doctor(
    doctor_id: str,
) -> DoctorResponse:
    """Deactivate a doctor account and its related user account."""

    doctor = await doctor_repo.find_by_id(
        doctor_id
    )

    if not doctor:
        raise DoctorNotFoundException()

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

    return DoctorMapper.to_response(
        doctor
    )


async def get_dashboard_stats() -> DashboardResponse:
    """Return summary counts for the admin dashboard."""

    total_patients = await patient_repo.count()

    total_doctors = await doctor_repo.count()

    active_doctors = await doctor_repo.count_active()

    total_appointments = await appointment_repo.count()

    completed = await appointment_repo.count_by_status(
        AppointmentStatus.COMPLETED
    )

    cancelled = await appointment_repo.count_by_status(
        AppointmentStatus.CANCELLED
    )

    return DashboardResponse(
        total_patients=total_patients,
        total_doctors=total_doctors,
        active_doctors=active_doctors,
        total_appointments=total_appointments,
        completed_appointments=completed,
        cancelled_appointments=cancelled,
    )


async def get_recent_appointments():
    """Return recent appointments for the admin dashboard."""

    appointments = await appointment_repo.find_all()
    
    return appointments[:10]