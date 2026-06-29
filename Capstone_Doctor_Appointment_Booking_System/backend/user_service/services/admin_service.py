from fastapi import HTTPException, status
from models.user import User, Role
from repositories.user_repository import UserRepository
from utils.logger import get_logger

logger = get_logger(__name__)
user_repo = UserRepository()


async def get_all_users():
    return await User.find_all().to_list()


async def activate_user(user_id: str):
    user = await user_repo.find_by_id(user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    user.is_active = True
    await user_repo.update(user)
    logger.info(f"User activated: {user_id}")
    return {"message": "User activated successfully"}


async def deactivate_user(user_id: str):
    user = await user_repo.find_by_id(user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    user.is_active = False
    await user_repo.update(user)
    logger.info(f"User deactivated: {user_id}")
    return {"message": "User deactivated successfully"}