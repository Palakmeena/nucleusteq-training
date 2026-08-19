import { createContext, useCallback, useContext, useEffect, useRef } from 'react';
import { useAuth } from './AuthContext';
import { SOCKET_EVENTS } from '../constants/socketEvents';
import { createWebSocketUrl } from '../services/websocketService';

const SocketContext = createContext(null);

export const useSocket = () => {
  const context = useContext(SocketContext);
  if (!context) {
    throw new Error('useSocket must be used within a SocketProvider');
  }
  return context;
};

export const SocketProvider = ({ children }) => {
  const { user } = useAuth();
  const socketRef = useRef(null);
  const listenersRef = useRef(new Map());
  const doctorSubscriptionsRef = useRef(new Set());
  const reconnectTimerRef = useRef(null);

  const dispatchEvent = useCallback((message) => {
    const listeners = listenersRef.current.get(message.event);
    listeners?.forEach((listener) => listener(message.data));
  }, []);

  const subscribe = useCallback((event, listener) => {
    if (!listenersRef.current.has(event)) {
      listenersRef.current.set(event, new Set());
    }
    listenersRef.current.get(event).add(listener);

    return () => {
      const listeners = listenersRef.current.get(event);
      listeners?.delete(listener);
      if (listeners?.size === 0) listenersRef.current.delete(event);
    };
  }, []);

  const sendEvent = useCallback((event, data = {}) => {
    if (socketRef.current?.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify({ event, data }));
    }
  }, []);

  const subscribeToDoctor = useCallback((doctorId) => {
    const normalizedDoctorId = String(doctorId);
    doctorSubscriptionsRef.current.add(normalizedDoctorId);
    sendEvent(SOCKET_EVENTS.SUBSCRIBE_DOCTOR, { doctor_id: normalizedDoctorId });

    return () => doctorSubscriptionsRef.current.delete(normalizedDoctorId);
  }, [sendEvent]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!user || !token) return undefined;

    let active = true;

    const connect = () => {
      const socket = new WebSocket(createWebSocketUrl(token));
      socketRef.current = socket;

      socket.onopen = () => {
        doctorSubscriptionsRef.current.forEach((doctorId) => {
          socket.send(JSON.stringify({
            event: SOCKET_EVENTS.SUBSCRIBE_DOCTOR,
            data: { doctor_id: doctorId },
          }));
        });
      };

      socket.onmessage = (message) => {
        try {
          dispatchEvent(JSON.parse(message.data));
        } catch {
          // Ignore malformed real-time messages.
        }
      };

      socket.onclose = () => {
        if (active) {
          reconnectTimerRef.current = window.setTimeout(connect, 3000);
        }
      };
    };

    connect();

    return () => {
      active = false;
      window.clearTimeout(reconnectTimerRef.current);
      socketRef.current?.close();
      socketRef.current = null;
    };
  }, [user, dispatchEvent]);

  return (
    <SocketContext.Provider value={{ subscribe, sendEvent, subscribeToDoctor }}>
      {children}
    </SocketContext.Provider>
  );
};
