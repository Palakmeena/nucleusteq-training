"""Deactivation request response schemas."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from enums.doctor_status import DeactivationRequestStatus


class DeactivationRequestResponse(BaseModel):
    """Response payload for a deactivation request."""

    id: str
    doctor_id: str
    user_id: str
    start_date: str
    end_date: str
    reason: Optional[str] = None
    status: DeactivationRequestStatus
    created_at: datetime
    reviewed_at: Optional[datetime] = None

    model_config = {
        "from_attributes": True
    }


class DeactivationRequestAdminResponse(DeactivationRequestResponse):
    """Response payload for admin view — includes doctor name."""

    doctor_name: Optional[str] = None
