from pydantic import BaseModel, field_validator
from datetime import datetime


class SlotCreateRequest(BaseModel):
    date: str
    start_time: str
    end_time: str

    @field_validator("date")
    @classmethod
    def validate_date(cls, v):
        try:
            slot_date = datetime.strptime(v, "%Y-%m-%d").date()
        except ValueError:
            raise ValueError("Date must be in YYYY-MM-DD format")
        if slot_date < datetime.utcnow().date():
            raise ValueError("Slot date cannot be in the past")
        return v

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, v):
        try:
            datetime.strptime(v, "%H:%M")
        except ValueError:
            raise ValueError("Time must be in HH:MM format")
        return v


class SlotUpdateRequest(BaseModel):
    date: str = None
    start_time: str = None
    end_time: str = None