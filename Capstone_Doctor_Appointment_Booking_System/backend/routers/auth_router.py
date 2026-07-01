"""Authentication API routes."""

from fastapi import APIRouter, status

from schemas.request.auth_request import LoginRequest
from schemas.request.auth_request import (
    DoctorRegisterRequest,
    PatientRegisterRequest,
)
from schemas.response.auth_response import (
    LoginResponse,
    UserResponse,
)
from services.auth_service import (
    login_user,
    register_doctor,
    register_patient,
)

router = APIRouter(
    prefix="/api/v1/auth",
    tags=["Authentication"],
)


@router.post(
    "/register/patient",
    response_model=UserResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_patient_endpoint(
    data: PatientRegisterRequest,
):
    """Register a patient account."""

    return await register_patient(data)


@router.post(
    "/register/doctor",
    response_model=UserResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_doctor_endpoint(
    data: DoctorRegisterRequest,
):
    """Register a doctor account."""

    return await register_doctor(data)


@router.post(
    "/login",
    response_model=LoginResponse,
)
async def login(
    data: LoginRequest,
):
    """Authenticate a user and return a token."""

    return await login_user(
        email=data.email,
        password=data.password,
    )