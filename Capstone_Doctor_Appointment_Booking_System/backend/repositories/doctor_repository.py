"""Doctor data access helpers."""

from datetime import date
from typing import Optional

from models.doctor import Doctor
from enums.doctor_status import DoctorStatus


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

    async def find_by_status(self, status: DoctorStatus):
        """Return all doctors with the given registration status."""
        return await Doctor.find({"status": status}).to_list()

    async def find_by_ids(self, doctor_ids: list[str]):
        from beanie import PydanticObjectId
        obj_ids = [PydanticObjectId(did) for did in set(doctor_ids) if did]
        if not obj_ids:
            return []
        return await Doctor.find({"_id": {"$in": obj_ids}}).to_list()

    async def count(self) -> int:
        """Return the total number of doctors."""
        return await Doctor.find_all().count()

    async def count_active(self) -> int:
        """Return the total number of approved doctors."""
        return await Doctor.find(
            {"status": DoctorStatus.APPROVED}
        ).count()

    async def search(
        self,
        name: str | None = None,
        specialization: str | None = None,
        location: str | None = None,
        min_experience: int | None = None,
        max_fee: float | None = None,
    ):
        """Search approved and available doctors using optional filters."""
        
        today = date.today().isoformat()  

       
        query = {
            "status": DoctorStatus.APPROVED,
            "$or": [
                {"is_active": True},
                {
                    "is_active": False,
                    "unavailable_to": {"$lt": today},
                },
            ],
        }

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

        doctors = await Doctor.find(query).to_list()

        
        for doctor in doctors:
            if (
                not doctor.is_active
                and doctor.unavailable_to is not None
                and doctor.unavailable_to < today
            ):
                doctor.is_active = True
                doctor.unavailable_from = None
                doctor.unavailable_to = None
                await doctor.save()

        return doctors

    async def save(self, doctor: Doctor) -> Doctor:
        await doctor.insert()
        return doctor

    async def update(self, doctor: Doctor) -> Doctor:
        await doctor.save()
        return doctor

    async def delete(self, doctor: Doctor) -> None:
        await doctor.delete()