import { Link } from 'react-router-dom';
import useAuth from '../../../auth/useAuth';

export default function HomePage() {
  const auth = useAuth();

  return (
    <section style={{ maxWidth: 900, margin: '0 auto' }}>
      <div style={{ padding: '2rem', background: '#fff', borderRadius: 12, boxShadow: '0 10px 30px rgba(15,23,42,.08)' }}>
        <h1 style={{ marginBottom: 16, fontSize: '2.5rem', color: '#111827' }}>LecturBoxd</h1>
        <p style={{ marginBottom: 24, color: '#4b5563', lineHeight: 1.8 }}>
          Track your lectures, review courses, and connect with classmates using the agreed backend contract.
          The app is ready for auth, syllabus browsing, reviews, and follow collaboration.
        </p>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <Link to="/lectures" style={{ padding: '0.9rem 1.4rem', background: '#2563eb', color: '#fff', borderRadius: 8, textDecoration: 'none' }}>
            Browse Syllabus
          </Link>
          {auth.token ? (
            <Link to={`/profile/${auth.user?.id}`} style={{ padding: '0.9rem 1.4rem', background: '#10b981', color: '#fff', borderRadius: 8, textDecoration: 'none' }}>
              My Profile
            </Link>
          ) : (
            <Link to="/login" style={{ padding: '0.9rem 1.4rem', background: '#111827', color: '#fff', borderRadius: 8, textDecoration: 'none' }}>
              Login / Join
            </Link>
          )}
        </div>
      </div>
    </section>
  );
}
