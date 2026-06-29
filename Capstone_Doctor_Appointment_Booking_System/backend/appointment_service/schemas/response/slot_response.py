from pydantic import BaseModel
from datetime import datetime


class SlotResponse(BaseModel):
    id: str
    doctor_id: str
    date: str
    start_time: str
    end_time: str
    is_booked: bool
    created_at: datetime

    model_config = {"from_attributes": True}