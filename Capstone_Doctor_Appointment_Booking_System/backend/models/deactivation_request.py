"""Doctor temporary deactivation request model."""

from datetime import datetime
from typing import Optional

from beanie import Document
from pydantic import Field

from enums.doctor_status import DeactivationRequestStatus


class DeactivationRequest(Document):
    """Stored record for a doctor's temporary deactivation request."""

    doctor_id: str
    user_id: str
    start_date: str
    end_date: str
    reason: Optional[str] = None
    status: DeactivationRequestStatus = DeactivationRequestStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    reviewed_at: Optional[datetime] = None

    class Settings:
        """Beanie collection settings."""

        name = "deactivation_requests"
