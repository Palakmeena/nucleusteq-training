import pytest
from httpx import AsyncClient, ASGITransport
from asgi_lifespan import LifespanManager
from main import app


@pytest.fixture(scope="function")
async def client():
    async with LifespanManager(app) as manager:
        async with AsyncClient(
            transport=ASGITransport(app=manager.app),
            base_url="http://test"
        ) as ac:
            yield ac