import React, { useEffect, useMemo, useState } from 'react';
import { AuthContext, AuthUser } from './AuthContext';
import { getToken, getUser, setToken, setUser, clearAuth } from './tokenStorage';
import { AUTH_CLEARED_EVENT } from '../api/axiosClient';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setTokenState] = useState<string | null>(null);
  const [user, setUserState] = useState<AuthUser | null>(null);

  useEffect(() => {
    const existingToken = getToken();
    const existingUser = getUser();
    if (existingToken) setTokenState(existingToken);
    if (existingUser) setUserState(existingUser);

    const syncLogout = () => {
      setTokenState(null);
      setUserState(null);
    };
    window.addEventListener(AUTH_CLEARED_EVENT, syncLogout);
    return () => window.removeEventListener(AUTH_CLEARED_EVENT, syncLogout);
  }, []);

  const login = (newToken: string, newUser: AuthUser) => {
    setToken(newToken);
    setTokenState(newToken);
    setUser(newUser);
    setUserState(newUser);
  };

  const logout = () => {
    clearAuth();
    setTokenState(null);
    setUserState(null);
  };

  const value = useMemo(
    () => ({ user, token, login, logout }),
    [user, token]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
