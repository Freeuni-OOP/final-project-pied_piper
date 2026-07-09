import { useEffect, useState } from 'react';
import useAuth from '../../../auth/useAuth';
import { getFollowers, getFollowing, PublicUser } from '../../../api/userApi';

export default function ProfilePage() {
  const auth = useAuth();
  const [followers, setFollowers] = useState<PublicUser[]>([]);
  const [following, setFollowing] = useState<PublicUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const user = auth.user;
    if (!user) return;
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const [followersData, followingData] = await Promise.all([
          getFollowers(user.id),
          getFollowing(user.id),
        ]);
        setFollowers(followersData);
        setFollowing(followingData);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load follow data.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [auth.user]);

  if (!auth.user) return <p>Please login to view your profile.</p>;

  return (
    <div style={{ maxWidth: 900, margin: '0 auto' }}>
      <h2>{auth.user.name ?? auth.user.email}</h2>
      <p>{auth.user.email}</p>
      {loading && <p>Loading profile data…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && (
        <div style={{ display: 'grid', gap: 24 }}>
          <section style={{ background: '#fff', borderRadius: 12, padding: 16, border: '1px solid #e5e7eb' }}>
            <h3>Followers</h3>
            {followers.length === 0 ? <p>No followers yet.</p> : <p>{followers.length} follower(s)</p>}
          </section>
          <section style={{ background: '#fff', borderRadius: 12, padding: 16, border: '1px solid #e5e7eb' }}>
            <h3>Following</h3>
            {following.length === 0 ? <p>Not following anyone yet.</p> : <p>{following.length} following</p>}
          </section>
        </div>
      )}
    </div>
  );
}
