import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from database.connection import connect_db
from models.user import User
from models.patient import Patient
from config.settings import settings
from routers.auth_router import router as auth_router
from routers.admin_router import router as admin_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_db([User, Patient])
    print("✅ Connected to MongoDB — User Service")
    yield
    print("❌ Disconnected from MongoDB — User Service")


app = FastAPI(
    title="User Service",
    description="Handles authentication and user management",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(auth_router)
app.include_router(admin_router)


@app.get("/health")
async def health_check():
    return {"status": "User Service is running", "port": 8001}


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=settings.user_service_port,
        reload=True
    )