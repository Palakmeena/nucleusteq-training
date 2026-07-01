"""Patient profile model."""

from beanie import Document
from pydantic import Field
from datetime import datetime


class Patient(Document):
    """Stored patient profile record."""

    user_id: str
    gender: str
    date_of_birth: str
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        """Beanie collection settings."""

        name = "patients"