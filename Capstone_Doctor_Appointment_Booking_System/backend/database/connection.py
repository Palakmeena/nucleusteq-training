"""MongoDB connection helpers for Beanie initialization."""

from typing import Sequence

from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient

from config.settings import settings


client = AsyncIOMotorClient(
    settings.mongo_uri
)


async def connect_db(
    models: Sequence[type],
):
    """Initialize Beanie with the configured database and models."""

    await init_beanie(
        database=client[settings.db_name],
        document_models=models,
    )


async def close_db():
    """Close the MongoDB client."""

    client.close()