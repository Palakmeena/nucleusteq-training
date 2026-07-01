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
    await init_beanie(
        database=client[settings.db_name],
        document_models=models,
    )


async def close_db():
    client.close()