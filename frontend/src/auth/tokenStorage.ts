export interface StoredAuthUser {
  id: string;
  name?: string;
  email?: string;
}

const ACCESS_KEY = 'lecturboxd_access_token';
const USER_KEY = 'lecturboxd_user';

export function getToken(): string | null {
  try {
    return localStorage.getItem(ACCESS_KEY);
  } catch {
    return null;
  }
}

export function setToken(token: string) {
  try {
    localStorage.setItem(ACCESS_KEY, token);
  } catch {
    // ignore storage failures
  }
}

export function getUser(): StoredAuthUser | null {
  try {
    const data = localStorage.getItem(USER_KEY);
    return data ? JSON.parse(data) : null;
  } catch {
    return null;
  }
}

export function setUser(user: StoredAuthUser) {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  } catch {
    // ignore storage failures
  }
}

export function clearToken() {
  try {
    localStorage.removeItem(ACCESS_KEY);
  } catch {
    // ignore storage failures
  }
}

export function clearUser() {
  try {
    localStorage.removeItem(USER_KEY);
  } catch {
    // ignore storage failures
  }
}

export function clearAuth() {
  clearToken();
  clearUser();
}
