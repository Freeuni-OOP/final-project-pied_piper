import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getFollowers, getFollowing, getUserProfile, PublicUser, UserProfile } from '../../../api/userApi';
import BackButton from '../../../components/BackButton';

type Mode = 'followers' | 'following';

export default function UserNetworkPage({ mode }: { mode: Mode }) {
  const { userId } = useParams<{ userId: string }>();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [users, setUsers] = useState<PublicUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const [profileData, list] = await Promise.all([
          getUserProfile(userId),
          mode === 'followers' ? getFollowers(userId) : getFollowing(userId),
        ]);
        setProfile(profileData);
        setUsers(list);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load network.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId, mode]);

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <BackButton fallbackTo={userId ? `/profile/${userId}` : '/profile'} />
      <h1 style={{ marginBottom: 8 }}>{mode === 'followers' ? 'Followers' : 'Following'}</h1>
      {profile && <p style={{ color: '#6b7280', marginBottom: 24 }}>{profile.name}</p>}
      {loading && <p>Loading…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && users.length === 0 && (
        <p style={{ color: '#6b7280' }}>
          {mode === 'followers' ? 'No followers yet.' : 'Not following anyone yet.'}
        </p>
      )}
      <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 12 }}>
        {users.map((user) => (
          <li key={user.id} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 16 }}>
            <Link to={`/profile/${user.id}`} style={{ fontWeight: 600, color: '#111827', textDecoration: 'none' }}>
              {user.name}
            </Link>
            <p style={{ margin: '4px 0 0', color: '#6b7280' }}>{user.email}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
