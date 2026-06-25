from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient
from config.settings import settings


MONGO_URI = settings.mongo_uri
DB_NAME = settings.db_name


async def connect_db(models: list):
    client = AsyncIOMotorClient(MONGO_URI)
    await init_beanie(
        database=client[DB_NAME],
        document_models=models
    )


async def close_db(client: AsyncIOMotorClient):
    client.close()