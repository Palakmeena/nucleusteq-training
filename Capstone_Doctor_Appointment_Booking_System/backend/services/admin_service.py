"""Admin service operations."""

from datetime import datetime

from enums.appointment_status import AppointmentStatus
from enums.doctor_status import DoctorStatus, DeactivationRequestStatus
from exceptions.doctor_exceptions import DoctorNotFoundException
from exceptions.deactivation_exceptions import (
    DeactivationRequestNotFoundException,
    DeactivationRequestAlreadyProcessedException,
)
from mappers.doctor_mapper import DoctorMapper
from mappers.user_mapper import UserMapper
from repositories.appointment_repository import AppointmentRepository
from repositories.deactivation_repository import DeactivationRequestRepository
from repositories.doctor_repository import DoctorRepository
from repositories.patient_repository import PatientRepository
from repositories.slot_repository import SlotRepository
from repositories.user_repository import UserRepository
from schemas.response.admin_response import DashboardResponse
from schemas.response.auth_response import UserResponse
from schemas.response.deactivation_response import (
    DeactivationRequestAdminResponse,
)
from schemas.response.doctor_response import DoctorResponse
from utils.logger import get_logger

logger = get_logger(__name__)

user_repo = UserRepository()
doctor_repo = DoctorRepository()
patient_repo = PatientRepository()
appointment_repo = AppointmentRepository()
deactivation_repo = DeactivationRequestRepository()
slot_repo = SlotRepository()


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


async def approve_doctor(
    doctor_id: str,
) -> DoctorResponse:
    """Approve a pending doctor registration and enable login."""

    doctor = await doctor_repo.find_by_id(doctor_id)

    if not doctor:
        raise DoctorNotFoundException()

    user = await user_repo.find_by_id(doctor.user_id)

    doctor.status = DoctorStatus.APPROVED
    doctor.is_active = True

    if user:
        user.is_active = True
        await user_repo.update(user)

    await doctor_repo.update(doctor)

    logger.info(f"Doctor approved: {doctor.id}")

    return DoctorMapper.to_response(doctor)


async def reject_doctor(
    doctor_id: str,
) -> DoctorResponse:
    """Reject a doctor registration — account remains unable to log in."""

    doctor = await doctor_repo.find_by_id(doctor_id)

    if not doctor:
        raise DoctorNotFoundException()

    doctor.status = DoctorStatus.REJECTED
    doctor.is_active = False

    await doctor_repo.update(doctor)

    logger.info(f"Doctor rejected: {doctor.id}")

    return DoctorMapper.to_response(doctor)


async def get_all_deactivation_requests() -> list[DeactivationRequestAdminResponse]:
    """Return all deactivation requests for the admin panel."""

    requests = await deactivation_repo.find_all()
    result = []

    for req in requests:
        doctor = await doctor_repo.find_by_id(req.doctor_id)
        doctor_name = doctor.full_name if doctor else None
        result.append(
            DeactivationRequestAdminResponse(
                id=str(req.id),
                doctor_id=req.doctor_id,
                user_id=req.user_id,
                start_date=req.start_date,
                end_date=req.end_date,
                reason=req.reason,
                status=req.status,
                created_at=req.created_at,
                reviewed_at=req.reviewed_at,
                doctor_name=doctor_name,
            )
        )

    return result


async def approve_deactivation_request(
    request_id: str,
) -> DeactivationRequestAdminResponse:
    """Approve a doctor's deactivation request."""
    

    req = await deactivation_repo.find_by_id(request_id)

    if not req:
        raise DeactivationRequestNotFoundException()

    if req.status != DeactivationRequestStatus.PENDING:
        raise DeactivationRequestAlreadyProcessedException()

    req.status = DeactivationRequestStatus.APPROVED
    req.reviewed_at = datetime.utcnow()
    await deactivation_repo.update(req)

    doctor = await doctor_repo.find_by_id(req.doctor_id)

    if doctor:
        doctor.is_active = False
        doctor.unavailable_from = req.start_date
        doctor.unavailable_to = req.end_date
        await doctor_repo.update(doctor)

        deleted_count = await slot_repo.delete_unbooked_in_date_range(
            doctor_id=req.doctor_id,
            start_date=req.start_date,
            end_date=req.end_date,
        )

        active_appointments = await appointment_repo.find_active_by_doctor_in_date_range(
            doctor_id=req.doctor_id,
            start_date=req.start_date,
            end_date=req.end_date,
        )

        cancelled_count = 0
        for appt in active_appointments:
            appt.status = AppointmentStatus.CANCELLED
            appt.updated_at = datetime.utcnow()
            await appointment_repo.update(appt)

            linked_slot = await slot_repo.find_by_id(appt.slot_id)
            if linked_slot:
                await slot_repo.delete(linked_slot)

            cancelled_count += 1

        logger.info(
            f"Deactivation approved for doctor {req.doctor_id}. "
            f"Deleted {deleted_count} unbooked slots, "
            f"cancelled {cancelled_count} appointments in range "
            f"{req.start_date} to {req.end_date}."
        )

    doctor_name = doctor.full_name if doctor else None

    return DeactivationRequestAdminResponse(
        id=str(req.id),
        doctor_id=req.doctor_id,
        user_id=req.user_id,
        start_date=req.start_date,
        end_date=req.end_date,
        reason=req.reason,
        status=req.status,
        created_at=req.created_at,
        reviewed_at=req.reviewed_at,
        doctor_name=doctor_name,
    )


async def reject_deactivation_request(
    request_id: str,
) -> DeactivationRequestAdminResponse:
    """Reject a doctor's deactivation request. Doctor remains active."""

    req = await deactivation_repo.find_by_id(request_id)

    if not req:
        raise DeactivationRequestNotFoundException()

    if req.status != DeactivationRequestStatus.PENDING:
        raise DeactivationRequestAlreadyProcessedException()

    req.status = DeactivationRequestStatus.REJECTED
    req.reviewed_at = datetime.utcnow()
    await deactivation_repo.update(req)

    doctor = await doctor_repo.find_by_id(req.doctor_id)
    doctor_name = doctor.full_name if doctor else None

    logger.info(
        f"Deactivation rejected for doctor {req.doctor_id}."
    )

    return DeactivationRequestAdminResponse(
        id=str(req.id),
        doctor_id=req.doctor_id,
        user_id=req.user_id,
        start_date=req.start_date,
        end_date=req.end_date,
        reason=req.reason,
        status=req.status,
        created_at=req.created_at,
        reviewed_at=req.reviewed_at,
        doctor_name=doctor_name,
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