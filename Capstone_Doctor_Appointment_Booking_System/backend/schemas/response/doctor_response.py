from datetime import datetime

from pydantic import BaseModel


class DoctorBaseResponse(BaseModel):
    id: str
    full_name: str
    specialization: str
    experience: int
    consultation_fee: float
    clinic_address: str
    is_active: bool

    model_config = {
        "from_attributes": True
    }


class DoctorListResponse(DoctorBaseResponse):
    pass


class DoctorResponse(DoctorBaseResponse):
    user_id: str
    phone: str
    qualification: str
    license_number: str
    created_at: datetime