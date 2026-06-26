from pydantic import BaseModel
from datetime import datetime


class AppointmentResponse(BaseModel):
    id: str
    patient_id: str
    doctor_id: str
    slot_id: str
    appointment_date: str
    start_time: str
    end_time: str
    status: str
    payment_status: str
    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}