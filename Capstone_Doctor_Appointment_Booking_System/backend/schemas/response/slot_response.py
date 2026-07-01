from datetime import datetime

from pydantic import BaseModel


class SlotResponse(BaseModel):
    id: str
    doctor_id: str
    date: str
    start_time: str
    end_time: str
    is_booked: bool
    created_at: datetime

    model_config = {
        "from_attributes": True
    }