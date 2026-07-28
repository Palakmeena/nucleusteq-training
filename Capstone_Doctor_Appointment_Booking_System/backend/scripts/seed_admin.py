"""Seed script for the default admin account."""

import asyncio

from database.connection import connect_db
from enums.user_role import UserRole
from models.patient import Patient
from models.user import User
from utils.password_utils import hash_password
from utils.logger import get_logger



async def seed_admin():
    """Create the default admin account if it does not already exist."""

    await connect_db(
        [
            User,
            Patient,
        ]
    )

    existing = await User.find_one(
        User.email == "admin@docbook.com"
    )

    if existing:
        print("Admin already exists.")
        return

    admin = User(
        full_name="Admin",
        email="admin@docbook.com",
        password_hash=hash_password("Admin@123"),
        phone="9999999999",
        role=UserRole.ADMIN,
        is_active=True,
    )

    await admin.insert()


    logger = get_logger(__name__)

    logger.info("Admin created successfully.")
    logger.info("Email: admin@docbook.com")
    logger.info("Password: Admin@123")


if __name__ == "__main__":
    asyncio.run(seed_admin())