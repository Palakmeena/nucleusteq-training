"""Admin dashboard response schemas."""

from pydantic import BaseModel


class DashboardResponse(BaseModel):
    """Summary counts for the admin dashboard."""

    total_doctors: int
    active_doctors: int
    total_appointments: int
    completed_appointments: int
    cancelled_appointments: int