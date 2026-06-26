from fastapi import HTTPException, status
from repositories.doctor_repository import DoctorRepository
from schemas.response.doctor_response import DoctorResponse
from constants.messages import DoctorMessages
from utils.logger import get_logger

logger = get_logger(__name__)
doctor_repo = DoctorRepository()


async def get_all_doctors():
    doctors = await doctor_repo.find_all()
    return [
        DoctorResponse(
            id=str(d.id),
            user_id=d.user_id,
            full_name=d.full_name,
            qualification=d.qualification,
            specialization=d.specialization,
            experience=d.experience,
            license_number=d.license_number,
            consultation_fee=d.consultation_fee,
            clinic_address=d.clinic_address,
            is_active=d.is_active,
            created_at=d.created_at
        ) for d in doctors
    ]


async def activate_doctor(doctor_id: str) -> DoctorResponse:
    doctor = await doctor_repo.find_by_id(doctor_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )

    doctor.is_active = True
    await doctor_repo.update(doctor)
    logger.info(f"Doctor activated: {doctor_id}")

    return DoctorResponse(
        id=str(doctor.id),
        user_id=doctor.user_id,
        full_name=doctor.full_name,
        qualification=doctor.qualification,
        specialization=doctor.specialization,
        experience=doctor.experience,
        license_number=doctor.license_number,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        is_active=doctor.is_active,
        created_at=doctor.created_at
    )


async def deactivate_doctor(doctor_id: str) -> DoctorResponse:
    doctor = await doctor_repo.find_by_id(doctor_id)
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DoctorMessages.DOCTOR_NOT_FOUND
        )

    doctor.is_active = False
    await doctor_repo.update(doctor)
    logger.info(f"Doctor deactivated: {doctor_id}")

    return DoctorResponse(
        id=str(doctor.id),
        user_id=doctor.user_id,
        full_name=doctor.full_name,
        qualification=doctor.qualification,
        specialization=doctor.specialization,
        experience=doctor.experience,
        license_number=doctor.license_number,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        is_active=doctor.is_active,
        created_at=doctor.created_at
    )


async def get_dashboard_stats() -> dict:
    from models.appointment import Appointment, AppointmentStatus
    from models.doctor import Doctor

    total_doctors = await Doctor.count()
    total_appointments = await Appointment.count()
    completed = await Appointment.find(
        {"status": AppointmentStatus.COMPLETED}
    ).count()
    cancelled = await Appointment.find(
        {"status": AppointmentStatus.CANCELLED}
    ).count()
    active_doctors = await Doctor.find(
        {"is_active": True}
    ).count()

    return {
        "total_doctors": total_doctors,
        "active_doctors": active_doctors,
        "total_appointments": total_appointments,
        "completed_appointments": completed,
        "cancelled_appointments": cancelled
    }