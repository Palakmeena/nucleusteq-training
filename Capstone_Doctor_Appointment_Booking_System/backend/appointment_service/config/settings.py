from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    mongo_uri: str
    db_name: str
    jwt_secret: str
    jwt_expiry_minutes: int = 30
    appointment_service_port: int = 8002

    model_config = {"env_file": ".env"}


settings = Settings()