import asyncio

from database.connection import connect_db
from models.patient import Patient
from models.user import Role, User
from utils.password_utils import hash_password


async def seed_admin():
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
        full_name="Super Admin",
        email="admin@docbook.com",
        password_hash=hash_password("Admin@123"),
        phone="9999999999",
        role=Role.ADMIN,
        is_active=True,
    )

    await admin.insert()

    print("Admin created successfully.")
    print("Email: admin@docbook.com")
    print("Password: Admin@123")


if __name__ == "__main__":
    asyncio.run(seed_admin())