"""Slot request schemas."""

from datetime import datetime

from pydantic import BaseModel, field_validator

from constants.validation_constants import (
    INVALID_DATE_FORMAT,
    INVALID_TIME_FORMAT,
    SLOT_DATE_IN_PAST,
    SLOT_TIME_IN_PAST,
)


class SlotCreateRequest(BaseModel):
    """Payload for creating a slot."""

    date: str
    start_time: str
    end_time: str

    @field_validator("date")
    @classmethod
    def validate_date(cls, value: str) -> str:
        """Ensure the slot date uses the expected format and is not in the past."""

        try:
            slot_date = datetime.strptime(
                value,
                "%Y-%m-%d",
            ).date()

        except ValueError:
            raise ValueError(INVALID_DATE_FORMAT)

        if slot_date < datetime.utcnow().date():
            raise ValueError(SLOT_DATE_IN_PAST)

        return value

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, value: str, info) -> str:
        """Ensure slot times use the expected 24-hour format and are not in the past for today."""

        try:
            parsed_time = datetime.strptime(
                value,
                "%H:%M",
            )

        except ValueError:
            raise ValueError(INVALID_TIME_FORMAT)

        if info.data.get("date"):
            slot_date = datetime.strptime(
                info.data["date"],
                "%Y-%m-%d",
            ).date()

            today = datetime.utcnow().date()

            if slot_date == today:
                now = datetime.utcnow()
                slot_datetime = datetime.combine(
                    today,
                    parsed_time.time(),
                )

                if slot_datetime < now:
                    raise ValueError(SLOT_TIME_IN_PAST)

        return value


class SlotUpdateRequest(BaseModel):
    """Payload for updating a slot."""

    date: str | None = None
    start_time: str | None = None
    end_time: str | None = None

    @field_validator("date")
    @classmethod
    def validate_date(cls, value):
        """Ensure the updated date stays valid when provided."""

        if value is None:
            return value

        try:
            slot_date = datetime.strptime(
                value,
                "%Y-%m-%d",
            ).date()

        except ValueError:
            raise ValueError(INVALID_DATE_FORMAT)

        if slot_date < datetime.utcnow().date():
            raise ValueError(SLOT_DATE_IN_PAST)

        return value

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, value, info):
        """Ensure the updated times stay valid when provided and not in the past for today."""

        if value is None:
            return value

        try:
            parsed_time = datetime.strptime(
                value,
                "%H:%M",
            )

        except ValueError:
            raise ValueError(INVALID_TIME_FORMAT)

        if info.data.get("date"):
            slot_date = datetime.strptime(
                info.data["date"],
                "%Y-%m-%d",
            ).date()

            today = datetime.utcnow().date()

            if slot_date == today:
                now = datetime.utcnow()
                slot_datetime = datetime.combine(
                    today,
                    parsed_time.time(),
                )

                if slot_datetime < now:
                    raise ValueError(SLOT_TIME_IN_PAST)

        return value