"""Doctor slot model."""

from beanie import Document
from pydantic import Field
from datetime import datetime


class Slot(Document):
    """Stored availability slot record."""

    doctor_id: str
    date: str
    start_time: str
    end_time: str
    is_booked: bool = False
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        """Beanie collection settings."""

        name = "slots"