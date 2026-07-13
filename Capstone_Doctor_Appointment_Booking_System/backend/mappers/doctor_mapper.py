"""Mapper for doctor responses."""

from models.doctor import Doctor
from schemas.response.doctor_response import (
    DoctorListResponse,
    DoctorResponse,
)


class DoctorMapper:
    """Convert Doctor documents into response schemas."""

    @staticmethod
    def to_response(
        doctor: Doctor,
    ) -> DoctorResponse:
        return DoctorResponse(
            id=str(doctor.id),
            user_id=doctor.user_id,
            full_name=doctor.full_name,
            phone=doctor.phone,
            qualification=doctor.qualification,
            experience=doctor.experience,
            license_number=doctor.license_number,
            specialization=doctor.specialization,
            consultation_fee=doctor.consultation_fee,
            clinic_address=doctor.clinic_address,
            is_active=doctor.is_active,
            status=doctor.status,
            unavailable_from=doctor.unavailable_from,
            unavailable_to=doctor.unavailable_to,
            created_at=doctor.created_at,
        )

    @staticmethod
    def to_list_response(
        doctor: Doctor,
    ) -> DoctorListResponse:
        return DoctorListResponse(
            id=str(doctor.id),
            full_name=doctor.full_name,
            specialization=doctor.specialization,
            experience=doctor.experience,
            consultation_fee=doctor.consultation_fee,
            clinic_address=doctor.clinic_address,
            is_active=doctor.is_active,
            status=doctor.status,
        )