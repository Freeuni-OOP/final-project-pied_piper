import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from './useAuth';

export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const auth = useAuth();
  if (!auth.token) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
