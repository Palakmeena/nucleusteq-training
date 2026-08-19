"""JWT authentication dependency for WebSocket connections."""

from fastapi import WebSocket, WebSocketException, status

from exceptions.auth_exceptions import InvalidTokenException
from utils.jwt_utils import decode_access_token


async def get_websocket_current_user(websocket: WebSocket) -> dict:
    """Validate the JWT supplied during the WebSocket handshake."""

    token = websocket.query_params.get("token")
    if not token:
        raise WebSocketException(code=status.WS_1008_POLICY_VIOLATION)

    try:
        return decode_access_token(token)
    except InvalidTokenException:
        raise WebSocketException(code=status.WS_1008_POLICY_VIOLATION)
