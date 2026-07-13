"""Deactivation request data access helpers."""

from typing import Optional

from models.deactivation_request import DeactivationRequest
from enums.doctor_status import DeactivationRequestStatus


class DeactivationRequestRepository:
    """Repository methods for deactivation request documents."""

    async def save(self, request: DeactivationRequest) -> DeactivationRequest:
        """Persist a new deactivation request."""
        await request.insert()
        return request

    async def find_by_id(self, request_id: str) -> Optional[DeactivationRequest]:
        """Find a deactivation request by its id."""
        return await DeactivationRequest.get(request_id)

    async def find_all_by_doctor(self, doctor_id: str) -> list[DeactivationRequest]:
        """Retrieve all deactivation requests for a given doctor."""
        return await DeactivationRequest.find(
            {"doctor_id": doctor_id}
        ).to_list()

    async def find_pending_by_doctor(self, doctor_id: str) -> Optional[DeactivationRequest]:
        """Return the first pending request for a doctor, if any."""
        return await DeactivationRequest.find_one(
            {
                "doctor_id": doctor_id,
                "status": DeactivationRequestStatus.PENDING,
            }
        )

    async def find_all_pending(self) -> list[DeactivationRequest]:
        """Return all pending deactivation requests (for admin)."""
        return await DeactivationRequest.find(
            {"status": DeactivationRequestStatus.PENDING}
        ).to_list()

    async def find_all(self) -> list[DeactivationRequest]:
        """Return all deactivation requests (for admin)."""
        return await DeactivationRequest.find_all().to_list()

    async def update(self, request: DeactivationRequest) -> DeactivationRequest:
        """Persist changes to an existing deactivation request."""
        await request.save()
        return request
