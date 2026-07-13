"""Doctor profile model."""

from datetime import datetime
from typing import Optional

from beanie import Document
from pydantic import Field

from enums.doctor_status import DoctorStatus


class Doctor(Document):
    """Stored doctor profile record."""

    user_id: str
    full_name: str
    phone: str
    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str

    status: DoctorStatus = DoctorStatus.PENDING

    is_active: bool = False

    unavailable_from: Optional[str] = None
    unavailable_to: Optional[str] = None

    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        """Beanie collection settings."""

        name = "doctors"