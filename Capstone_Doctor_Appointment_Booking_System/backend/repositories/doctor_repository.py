"""Doctor data access helpers."""

from typing import Optional

from models.doctor import Doctor


class DoctorRepository:
    """Repository methods for doctor documents."""

    async def find_by_id(self, doctor_id: str) -> Optional[Doctor]:
        return await Doctor.get(doctor_id)

    async def find_by_user_id(self, user_id: str) -> Optional[Doctor]:
        return await Doctor.find_one({"user_id": user_id})

    async def find_by_license(self, license_number: str) -> Optional[Doctor]:
        return await Doctor.find_one({"license_number": license_number})

    async def find_all(self):
        return await Doctor.find_all().to_list()

    async def count(self) -> int:
        """Return the total number of doctors."""
        return await Doctor.find_all().count()

    async def count_active(self) -> int:
        """Return the total number of active doctors."""
        return await Doctor.find(
            {"is_active": True}
        ).count()

    async def search(
        self,
        name: str | None = None,
        specialization: str | None = None,
        location: str | None = None,
        min_experience: int | None = None,
        max_fee: float | None = None,
    ):
        """Search active doctors using optional filters."""

        query = {"is_active": True}

        if name:
            query["full_name"] = {
                "$regex": name,
                "$options": "i",
            }

        if specialization:
            query["specialization"] = {
                "$regex": specialization,
                "$options": "i",
            }

        if location:
            query["clinic_address"] = {
                "$regex": location,
                "$options": "i",
            }

        if min_experience is not None:
            query["experience"] = {
                "$gte": min_experience,
            }

        if max_fee is not None:
            query["consultation_fee"] = {
                "$lte": max_fee,
            }

        return await Doctor.find(
            query
        ).to_list()

    async def save(self, doctor: Doctor) -> Doctor:
        await doctor.insert()
        return doctor

    async def update(self, doctor: Doctor) -> Doctor:
        await doctor.save()
        return doctor

    async def delete(self, doctor: Doctor) -> None:
        await doctor.delete()