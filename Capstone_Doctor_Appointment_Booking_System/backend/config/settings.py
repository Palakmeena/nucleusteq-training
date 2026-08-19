"""Application settings loaded from environment variables."""

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """Typed configuration for database, JWT, and server settings."""

    # Database
    mongo_uri: str
    db_name: str

    # JWT
    jwt_secret: str
    jwt_algorithm: str = "HS256"
    jwt_expiry_minutes: int = 30

    # Application
    app_host: str = "127.0.0.1"
    app_port: int = 8000

    # Cross-origin requests are needed only when the frontend runs separately.
    cors_allowed_origins: str = (
        "http://localhost:5173,http://127.0.0.1:5173"
    )

    model_config = {
        "env_file": ".env"
    }

    @property
    def cors_origins(self) -> list[str]:
        """Return the configured CORS origins as a normalized list."""

        return [
            origin.strip()
            for origin in self.cors_allowed_origins.split(",")
            if origin.strip()
        ]


settings = Settings()
