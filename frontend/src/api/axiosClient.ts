import axios from 'axios';
import { getToken, clearAuth } from '../auth/tokenStorage';

const BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || '';

export const AUTH_CLEARED_EVENT = 'lecturboxd:auth-cleared';

const client = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

client.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response) {
      const { status, data } = err.response;
      if (status === 401) {
        clearAuth();
        if (typeof window !== 'undefined') {
          window.dispatchEvent(new Event(AUTH_CLEARED_EVENT));
        }
      }
      return Promise.reject({
        status,
        data,
        message: data?.message || err.message,
      });
    }
    return Promise.reject({ message: err.message });
  }
);

export default client;
