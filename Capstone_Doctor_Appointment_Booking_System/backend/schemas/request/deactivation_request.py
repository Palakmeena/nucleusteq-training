"""Deactivation request schemas."""

from typing import Optional

from pydantic import BaseModel, field_validator


class DeactivationRequestCreate(BaseModel):
    """Payload submitted by a doctor to request temporary deactivation."""

    start_date: str
    end_date: str
    reason: Optional[str] = None

    @field_validator("start_date", "end_date")
    @classmethod
    def validate_date_format(cls, value: str) -> str:
        """Ensure dates are in YYYY-MM-DD format."""
        import re
        if not re.fullmatch(r"\d{4}-\d{2}-\d{2}", value):
            raise ValueError("Date must be in YYYY-MM-DD format")
        return value
