from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient
from config.settings import settings


async def connect_db(models: list):
    client = AsyncIOMotorClient(settings.mongo_uri)
    await init_beanie(
        database=client[settings.db_name],
        document_models=models
    )


async def close_db(client: AsyncIOMotorClient):
    client.close()