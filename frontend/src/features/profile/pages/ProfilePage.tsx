import { Navigate } from 'react-router-dom';
import useAuth from '../../../auth/useAuth';

export default function ProfilePage() {
  const auth = useAuth();
  if (!auth.userId) return <p>Please login to view your profile.</p>;
  return <Navigate to={`/profile/${auth.userId}`} replace />;
}
