"""WebSocket event and room names used by the real-time layer."""

SOCKET_EVENT_CONNECTION_READY = "connection.ready"
SOCKET_EVENT_CONNECTION_ERROR = "connection.error"
SOCKET_EVENT_SUBSCRIBE_DOCTOR = "subscribe.doctor"

SOCKET_EVENT_APPOINTMENT_CREATED = "appointment.created"
SOCKET_EVENT_APPOINTMENT_UPDATED = "appointment.updated"
SOCKET_EVENT_APPOINTMENT_CANCELLED = "appointment.cancelled"

SOCKET_EVENT_SLOT_CREATED = "slot.created"
SOCKET_EVENT_SLOT_UPDATED = "slot.updated"
SOCKET_EVENT_SLOT_DELETED = "slot.deleted"

SOCKET_ADMIN_ROOM = "role:ADMIN"
