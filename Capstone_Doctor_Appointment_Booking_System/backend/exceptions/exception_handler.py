"""Global exception handlers."""

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from constants.appointment_constants import (
    ALREADY_CANCELLED,
    APPOINTMENT_NOT_FOUND,
    CANNOT_CANCEL,
    INVALID_STATUS_UPDATE,
)
from constants.auth_constants import (
    ACCOUNT_INACTIVE,
    EMAIL_ALREADY_EXISTS,
    FORBIDDEN,
    INVALID_CREDENTIALS,
    INVALID_TOKEN,
    UNAUTHORIZED,
    USER_NOT_FOUND,
)
from constants.doctor_constants import (
    DOCTOR_INACTIVE,
    DOCTOR_NOT_FOUND,
    PROFILE_ALREADY_EXISTS,
)
from constants.slot_constants import (
    CANNOT_DELETE_BOOKED,
    INVALID_SLOT_TIME,
    SLOT_ALREADY_BOOKED,
    SLOT_NOT_FOUND,
    SLOT_OVERLAP,
)

from exceptions.appointment_exceptions import (
    AppointmentAlreadyCancelledException,
    AppointmentCancellationException,
    AppointmentNotFoundException,
    InvalidAppointmentStatusException,
)
from exceptions.auth_exceptions import (
    EmailAlreadyExistsException,
    ForbiddenException,
    InactiveAccountException,
    InvalidCredentialsException,
    InvalidTokenException,
    UnauthorizedException,
    UserNotFoundException,
)
from exceptions.doctor_exceptions import (
    DoctorInactiveException,
    DoctorNotFoundException,
    DoctorProfileAlreadyExistsException,
)
from exceptions.slot_exceptions import (
    InvalidSlotTimeException,
    SlotAlreadyBookedException,
    SlotCannotBeDeletedException,
    SlotNotFoundException,
    SlotOverlapException,
)


def register_exception_handlers(app: FastAPI) -> None:
    """Register all application exception handlers."""

    # ==========================
    # Authentication Exceptions
    # ==========================

    @app.exception_handler(InvalidCredentialsException)
    async def invalid_credentials_handler(
        request: Request,
        exc: InvalidCredentialsException,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED,
            content={
                "detail": INVALID_CREDENTIALS,
            },
        )

    @app.exception_handler(UnauthorizedException)
    async def unauthorized_handler(
        request: Request,
        exc: UnauthorizedException,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED,
            content={
                "detail": UNAUTHORIZED,
            },
        )

    @app.exception_handler(ForbiddenException)
    async def forbidden_handler(
        request: Request,
        exc: ForbiddenException,
    ):
        return JSONResponse(
            status_code=status.HTTP_403_FORBIDDEN,
            content={
                "detail": FORBIDDEN,
            },
        )

    @app.exception_handler(InvalidTokenException)
    async def invalid_token_handler(
        request: Request,
        exc: InvalidTokenException,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED,
            content={
                "detail": INVALID_TOKEN,
            },
        )

    @app.exception_handler(InactiveAccountException)
    async def inactive_account_handler(
        request: Request,
        exc: InactiveAccountException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": ACCOUNT_INACTIVE,
            },
        )

    @app.exception_handler(EmailAlreadyExistsException)
    async def email_exists_handler(
        request: Request,
        exc: EmailAlreadyExistsException,
    ):
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT,
            content={
                "detail": EMAIL_ALREADY_EXISTS,
            },
        )

    @app.exception_handler(UserNotFoundException)
    async def user_not_found_handler(
        request: Request,
        exc: UserNotFoundException,
    ):
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "detail": USER_NOT_FOUND,
            },
        )

    # ==========================
    # Doctor Exceptions
    # ==========================

    @app.exception_handler(DoctorNotFoundException)
    async def doctor_not_found_handler(
        request: Request,
        exc: DoctorNotFoundException,
    ):
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "detail": DOCTOR_NOT_FOUND,
            },
        )

    @app.exception_handler(DoctorInactiveException)
    async def doctor_inactive_handler(
        request: Request,
        exc: DoctorInactiveException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": DOCTOR_INACTIVE,
            },
        )

    @app.exception_handler(DoctorProfileAlreadyExistsException)
    async def doctor_profile_exists_handler(
        request: Request,
        exc: DoctorProfileAlreadyExistsException,
    ):
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT,
            content={
                "detail": PROFILE_ALREADY_EXISTS,
            },
        )

    # ==========================
    # Slot Exceptions
    # ==========================

    @app.exception_handler(SlotNotFoundException)
    async def slot_not_found_handler(
        request: Request,
        exc: SlotNotFoundException,
    ):
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "detail": SLOT_NOT_FOUND,
            },
        )

    @app.exception_handler(SlotAlreadyBookedException)
    async def slot_booked_handler(
        request: Request,
        exc: SlotAlreadyBookedException,
    ):
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT,
            content={
                "detail": SLOT_ALREADY_BOOKED,
            },
        )

    @app.exception_handler(SlotOverlapException)
    async def slot_overlap_handler(
        request: Request,
        exc: SlotOverlapException,
    ):
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT,
            content={
                "detail": SLOT_OVERLAP,
            },
        )

    @app.exception_handler(InvalidSlotTimeException)
    async def invalid_slot_time_handler(
        request: Request,
        exc: InvalidSlotTimeException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": INVALID_SLOT_TIME,
            },
        )

    @app.exception_handler(SlotCannotBeDeletedException)
    async def slot_cannot_be_deleted_handler(
        request: Request,
        exc: SlotCannotBeDeletedException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": CANNOT_DELETE_BOOKED,
            },
        )


    # ==========================
    # Appointment Exceptions
    # ==========================

    @app.exception_handler(AppointmentNotFoundException)
    async def appointment_not_found_handler(
        request: Request,
        exc: AppointmentNotFoundException,
    ):
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "detail": APPOINTMENT_NOT_FOUND,
            },
        )

    @app.exception_handler(AppointmentAlreadyCancelledException)
    async def appointment_cancelled_handler(
        request: Request,
        exc: AppointmentAlreadyCancelledException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": ALREADY_CANCELLED,
            },
        )

    @app.exception_handler(AppointmentCancellationException)
    async def appointment_cancellation_handler(
        request: Request,
        exc: AppointmentCancellationException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": CANNOT_CANCEL,
            },
        )

    @app.exception_handler(InvalidAppointmentStatusException)
    async def invalid_status_handler(
        request: Request,
        exc: InvalidAppointmentStatusException,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "detail": INVALID_STATUS_UPDATE,
            },
        )