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

    async def search(
        self,
        name: str | None = None,
        specialization: str | None = None,
    ):
        query = {"is_active": True}

        if specialization:
            query["specialization"] = specialization

        if name:
            query["full_name"] = {
                "$regex": name,
                "$options": "i",
            }

        return await Doctor.find(query).to_list()

    async def save(self, doctor: Doctor) -> Doctor:
        await doctor.insert()
        return doctor

    async def update(self, doctor: Doctor) -> Doctor:
        await doctor.save()
        return doctor

    async def delete(self, doctor: Doctor) -> None:
        await doctor.delete()