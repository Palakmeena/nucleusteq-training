from models.user import User
from typing import Optional


class UserRepository:

    async def find_by_email(self, email: str) -> Optional[User]:
        return await User.find_one({"email": email})

    async def find_by_id(self, user_id: str) -> Optional[User]:
        return await User.get(user_id)

    async def save(self, user: User) -> User:
        await user.insert()
        return user

    async def update(self, user: User) -> User:
        await user.save()
        return user