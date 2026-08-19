import { useEffect } from 'react';
import { useSocket } from '../context/SocketContext';

export const useSocketEvent = (event, listener) => {
  const { subscribe } = useSocket();

  useEffect(() => subscribe(event, listener), [event, listener, subscribe]);
};
