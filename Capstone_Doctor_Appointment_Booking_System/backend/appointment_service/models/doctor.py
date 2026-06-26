from beanie import Document
from pydantic import Field
from datetime import datetime


class Doctor(Document):
    user_id: str
    full_name: str
    phone: str
    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str
    is_active: bool = False
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "doctors"