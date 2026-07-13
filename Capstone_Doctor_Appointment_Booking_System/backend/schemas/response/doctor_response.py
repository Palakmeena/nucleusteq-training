"""Doctor response schemas."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from enums.doctor_status import DoctorStatus


class DoctorBaseResponse(BaseModel):
    """Shared doctor response fields."""

    id: str
    full_name: str
    specialization: str
    experience: int
    consultation_fee: float
    clinic_address: str
    is_active: bool
    status: DoctorStatus

    model_config = {
        "from_attributes": True
    }


class DoctorListResponse(DoctorBaseResponse):
    """Response payload for doctor search results."""

    pass


class DoctorResponse(DoctorBaseResponse):
    """Response payload for a doctor profile."""

    user_id: str
    phone: str
    qualification: str
    license_number: str
    created_at: datetime
    unavailable_from: Optional[str] = None
    unavailable_to: Optional[str] = None