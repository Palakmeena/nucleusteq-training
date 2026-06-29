from pydantic import BaseModel
from datetime import datetime


class DoctorResponse(BaseModel):
    id: str
    user_id: str
    full_name: str
    phone: str
    qualification: str
    experience: int
    license_number: str
    specialization: str
    consultation_fee: float
    clinic_address: str
    is_active: bool
    created_at: datetime

    model_config = {"from_attributes": True}


class DoctorListResponse(BaseModel):
    id: str
    full_name: str
    specialization: str
    experience: int
    consultation_fee: float
    clinic_address: str
    is_active: bool

    model_config = {"from_attributes": True}