"""Dependencies that expose request-scoped context to route handlers."""

from fastapi import Request


def get_request_id(request: Request) -> str:
    """Return the correlation ID created by request logging middleware."""

    return request.state.request_id
