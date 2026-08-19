"""Authenticated WebSocket endpoint for real-time application updates."""

from fastapi import APIRouter, Depends, WebSocket, WebSocketDisconnect

from constants.socket_constants import (
    SOCKET_EVENT_CONNECTION_ERROR,
    SOCKET_EVENT_CONNECTION_READY,
    SOCKET_EVENT_SUBSCRIBE_DOCTOR,
)
from dependencies.websocket_auth_dependency import get_websocket_current_user
from sockets.connection_manager import connection_manager


router = APIRouter(tags=["Real-time"])


@router.websocket("/ws")
async def websocket_endpoint(
    websocket: WebSocket,
    current_user: dict = Depends(get_websocket_current_user),
) -> None:
    """Connect an authenticated user and process room subscriptions."""

    user_id = current_user["sub"]
    role = current_user["role"]
    await connection_manager.connect(websocket, user_id, role)

    try:
        await websocket.send_json(
            {
                "event": SOCKET_EVENT_CONNECTION_READY,
                "data": {"user_id": user_id, "role": role},
            }
        )

        while True:
            message = await websocket.receive_json()
            event = message.get("event")
            data = message.get("data") or {}

            if event == SOCKET_EVENT_SUBSCRIBE_DOCTOR and data.get("doctor_id"):
                await connection_manager.subscribe(
                    websocket,
                    f"doctor:{data['doctor_id']}",
                )
            else:
                await websocket.send_json(
                    {
                        "event": SOCKET_EVENT_CONNECTION_ERROR,
                        "data": {"message": "Unsupported socket event."},
                    }
                )
    except WebSocketDisconnect:
        await connection_manager.disconnect(websocket, user_id)
