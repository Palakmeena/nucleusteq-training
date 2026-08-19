"""In-memory connection and room management for WebSocket clients."""

from collections import defaultdict

from fastapi import WebSocket
from starlette.websockets import WebSocketState


class ConnectionManager:
    """Track authenticated WebSocket connections and their subscriptions."""

    def __init__(self) -> None:
        self._user_connections: dict[str, set[WebSocket]] = defaultdict(set)
        self._room_connections: dict[str, set[WebSocket]] = defaultdict(set)
        self._connection_rooms: dict[WebSocket, set[str]] = defaultdict(set)

    async def connect(self, websocket: WebSocket, user_id: str, role: str) -> None:
        """Accept a connection and place it in private user and role rooms."""

        await websocket.accept()
        self._user_connections[user_id].add(websocket)
        await self.subscribe(websocket, f"user:{user_id}")
        await self.subscribe(websocket, f"role:{role}")

    async def disconnect(self, websocket: WebSocket, user_id: str) -> None:
        """Remove a disconnected client from every room it joined."""

        self._user_connections[user_id].discard(websocket)
        if not self._user_connections[user_id]:
            self._user_connections.pop(user_id, None)

        for room in self._connection_rooms.pop(websocket, set()):
            self._room_connections[room].discard(websocket)
            if not self._room_connections[room]:
                self._room_connections.pop(room, None)

    async def subscribe(self, websocket: WebSocket, room: str) -> None:
        """Subscribe a connection to a server-defined room."""

        self._room_connections[room].add(websocket)
        self._connection_rooms[websocket].add(room)

    async def send_to_user(self, user_id: str, event: str, data: dict) -> None:
        """Send an event to every active session for a user."""

        await self._send(self._user_connections.get(user_id, set()), event, data)

    async def send_to_room(self, room: str, event: str, data: dict) -> None:
        """Broadcast an event to every active connection in a room."""

        await self._send(self._room_connections.get(room, set()), event, data)

    async def _send(
        self,
        connections: set[WebSocket],
        event: str,
        data: dict,
    ) -> None:
        stale_connections: list[WebSocket] = []

        for websocket in list(connections):
            if websocket.client_state != WebSocketState.CONNECTED:
                stale_connections.append(websocket)
                continue

            try:
                await websocket.send_json({"event": event, "data": data})
            except RuntimeError:
                stale_connections.append(websocket)

        for websocket in stale_connections:
            for user_id, user_connections in list(self._user_connections.items()):
                if websocket in user_connections:
                    await self.disconnect(websocket, user_id)
                    break


connection_manager = ConnectionManager()
