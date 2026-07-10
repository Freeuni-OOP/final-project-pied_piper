import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getUserLectureLogs } from '../../../api/lectureLogApi';
import { getUserProfile, UserProfile } from '../../../api/userApi';
import { LectureLog } from '../../../types/lecture';
import BackButton from '../../../components/BackButton';

export default function UserLogsPage() {
  const { userId } = useParams<{ userId: string }>();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [logs, setLogs] = useState<LectureLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const [profileData, page] = await Promise.all([
          getUserProfile(userId),
          getUserLectureLogs(userId),
        ]);
        setProfile(profileData);
        setLogs(page.content ?? []);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load lecture logs.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <BackButton fallbackTo={userId ? `/profile/${userId}` : '/profile'} />
      <h1 style={{ marginBottom: 8 }}>Lectures logged</h1>
      {profile && <p style={{ color: '#6b7280', marginBottom: 24 }}>by {profile.name}</p>}
      {loading && <p>Loading…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && logs.length === 0 && <p style={{ color: '#6b7280' }}>No lectures logged yet.</p>}
      <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 12 }}>
        {logs.map((log) => (
          <li key={log.id} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 16 }}>
            <Link to={`/lectures/${log.lectureId}`} style={{ fontWeight: 600, color: '#2563eb' }}>
              {log.lectureTitle}
            </Link>
            <p style={{ margin: '8px 0 0', color: '#6b7280' }}>
              {log.watchedAt
                ? new Date(log.watchedAt).toLocaleDateString()
                : new Date(log.createdAt).toLocaleDateString()}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
}
