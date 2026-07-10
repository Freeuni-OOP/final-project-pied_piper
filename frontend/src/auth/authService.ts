import * as api from '../api/authApi';
import { setToken, setUser, clearAuth } from './tokenStorage';

export async function register(data: api.RegisterRequest) {
  return api.register(data);
}

export async function verifyOtp(data: api.VerifyRequest) {
  const res = await api.verifyOtp(data);
  if (res?.token) {
    setToken(res.token);
  }
  if (res?.user) {
    setUser(res.user);
  }
  return res;
}

export async function login(data: api.LoginRequest) {
  const res = await api.login(data);
  if (res?.token) {
    setToken(res.token);
  }
  if (res?.user) {
    setUser(res.user);
  }
  return res;
}

export function logout() {
  clearAuth();
}
