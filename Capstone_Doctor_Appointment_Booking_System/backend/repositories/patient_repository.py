"""Patient data access helpers."""

from typing import Optional

from models.patient import Patient


class PatientRepository:

    """Repository methods for patient documents."""

    async def find_by_id(self, patient_id: str) -> Optional[Patient]:
        return await Patient.get(patient_id)

    async def find_by_user_id(self, user_id: str) -> Optional[Patient]:
        return await Patient.find_one({"user_id": user_id})

    async def find_all(self):
        return await Patient.find_all().to_list()

    async def save(self, patient: Patient) -> Patient:
        await patient.insert()
        return patient

    async def update(self, patient: Patient) -> Patient:
        await patient.save()
        return patient

    async def delete(self, patient: Patient) -> None:
        await patient.delete()