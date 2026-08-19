"""Cross-origin resource sharing middleware configuration."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config.settings import settings


def configure_cors_middleware(app: FastAPI) -> None:
    """Register CORS for only the trusted frontend origins."""

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=True,
        allow_methods=["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
        allow_headers=["Authorization", "Content-Type", "Accept", "Origin"],
        expose_headers=["X-Request-ID"],
    )
