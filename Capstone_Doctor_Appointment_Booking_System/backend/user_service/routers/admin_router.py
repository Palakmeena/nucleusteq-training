from fastapi import APIRouter, Depends
from services.admin_service import get_all_users, activate_user, deactivate_user
from middleware.auth_middleware import require_role

router = APIRouter(prefix="/api/v1/admin", tags=["Admin"])


@router.get("/users")
async def all_users(
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await get_all_users()


@router.patch("/users/{user_id}/activate")
async def activate(
    user_id: str,
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await activate_user(user_id)


@router.patch("/users/{user_id}/deactivate")
async def deactivate(
    user_id: str,
    current_user: dict = Depends(require_role("ADMIN"))
):
    return await deactivate_user(user_id)