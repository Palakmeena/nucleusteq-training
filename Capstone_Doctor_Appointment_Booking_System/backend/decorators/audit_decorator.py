"""Audit logging for important business actions."""

from functools import wraps

from utils.logger import get_logger

logger = get_logger(__name__)


def audit_action(action: str):
    """Log the outcome of a critical asynchronous business action."""

    def decorator(func):
        @wraps(func)
        async def wrapper(*args, **kwargs):
            try:
                result = await func(*args, **kwargs)
                logger.info("AUDIT | action=%s | status=SUCCESS", action)
                return result
            except Exception:
                logger.warning("AUDIT | action=%s | status=FAILED", action)
                raise

        return wrapper

    return decorator
