from datetime import datetime

from pydantic import BaseModel, field_validator


class SlotCreateRequest(BaseModel):
    date: str
    start_time: str
    end_time: str

    @field_validator("date")
    @classmethod
    def validate_date(cls, value: str) -> str:
        try:
            slot_date = datetime.strptime(value, "%Y-%m-%d").date()
        except ValueError:
            raise ValueError("Date must be in YYYY-MM-DD format")

        if slot_date < datetime.utcnow().date():
            raise ValueError("Slot date cannot be in the past")

        return value

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, value: str) -> str:
        try:
            datetime.strptime(value, "%H:%M")
        except ValueError:
            raise ValueError("Time must be in HH:MM format")

        return value


class SlotUpdateRequest(BaseModel):
    date: str | None = None
    start_time: str | None = None
    end_time: str | None = None

    @field_validator("date")
    @classmethod
    def validate_date(cls, value):
        if value is None:
            return value

        try:
            slot_date = datetime.strptime(value, "%Y-%m-%d").date()
        except ValueError:
            raise ValueError("Date must be in YYYY-MM-DD format")

        if slot_date < datetime.utcnow().date():
            raise ValueError("Slot date cannot be in the past")

        return value

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, value):
        if value is None:
            return value

        try:
            datetime.strptime(value, "%H:%M")
        except ValueError:
            raise ValueError("Time must be in HH:MM format")

        return value