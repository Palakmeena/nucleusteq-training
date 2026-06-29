from fastapi import APIRouter
from schemas.request.register_request import PatientRegisterRequest, DoctorRegisterRequest
from schemas.response.user_response import UserResponse, LoginResponse
from services.auth_service import register_patient, register_doctor, login_user
from pydantic import BaseModel

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])


class LoginRequest(BaseModel):
    email: str
    password: str


@router.post("/register/patient", response_model=UserResponse, status_code=201)
async def patient_register(data: PatientRegisterRequest):
    return await register_patient(data)


@router.post("/register/doctor", response_model=UserResponse, status_code=201)
async def doctor_register(data: DoctorRegisterRequest):
    return await register_doctor(data)


@router.post("/login", response_model=LoginResponse)
async def login(data: LoginRequest):
    return await login_user(data.email, data.password)