import client from './axiosClient';

export interface UserResponse {
  id: string;
  name: string;
  email: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface VerifyRequest {
  email: string;
  otp: string;
}

export interface RegisterResponse {
  message: string;
  email: string;
  expiresAt?: string;
  devCode?: string;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export async function register(data: RegisterRequest): Promise<RegisterResponse> {
  const res = await client.post('/api/auth/register', data);
  return res.data;
}

export async function verifyOtp(data: VerifyRequest): Promise<AuthResponse> {
  // Backend expects field name "code", not "otp"
  const res = await client.post('/api/auth/verify', {
    email: data.email,
    code: data.otp,
  });
  return res.data;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const res = await client.post('/api/auth/login', data);
  return res.data;
}

export async function devDeleteUser(email: string) {
  const res = await client.post('/api/auth/dev/delete-user', { email });
  return res.data;
}
