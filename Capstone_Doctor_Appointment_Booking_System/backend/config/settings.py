"""Application settings loaded from environment variables."""

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """Typed configuration for database, JWT, and server settings."""

    # Database
    mongo_uri: str
    db_name: str

    # JWT
    jwt_secret: str
    jwt_expiry_minutes: int = 30

    # Application
    app_host: str = "127.0.0.1"
    app_port: int = 8000

    model_config = {
        "env_file": ".env"
    }


settings = Settings()