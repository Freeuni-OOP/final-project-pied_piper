import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getFollowers, getFollowing, getFollowStatus, followUser, unfollowUser, PublicUser } from '../../../api/userApi';
import useAuth from '../../../auth/useAuth';

export default function UserProfilePage() {
  const { userId } = useParams<{ userId: string }>();
  const auth = useAuth();
  const [followers, setFollowers] = useState<PublicUser[]>([]);
  const [following, setFollowing] = useState<PublicUser[]>([]);
  const [followStatus, setFollowStatus] = useState<{ userId: string; isFollowing: boolean } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const [followersData, followingData, status] = await Promise.all([
          getFollowers(userId),
          getFollowing(userId),
          getFollowStatus(userId),
        ]);
        setFollowers(followersData);
        setFollowing(followingData);
        setFollowStatus(status);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load profile.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  const handleToggleFollow = async () => {
    if (!userId || !followStatus) return;
    try {
      const result = followStatus.isFollowing ? await unfollowUser(userId) : await followUser(userId);
      setFollowStatus(result);
    } catch (err: any) {
      setError(err?.message ?? 'Unable to update follow status.');
    }
  };

  if (!userId) return <p>Invalid profile URL.</p>;

  return (
    <div style={{ maxWidth: 900, margin: '0 auto' }}>
      <h2>User profile</h2>
      {loading && <p>Loading profile…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && (
        <>
          <div style={{ marginBottom: 24 }}>
            <button onClick={handleToggleFollow} style={{ padding: '0.9rem', background: followStatus?.isFollowing ? '#ef4444' : '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}>
              {followStatus?.isFollowing ? 'Unfollow' : 'Follow'}
            </button>
          </div>
          <div style={{ display: 'grid', gap: 24 }}>
            <section style={{ background: '#fff', borderRadius: 12, padding: 16, border: '1px solid #e5e7eb' }}>
              <h3>Followers</h3>
              {followers.length === 0 ? <p>No followers yet.</p> : <p>{followers.length} followers</p>}
            </section>
            <section style={{ background: '#fff', borderRadius: 12, padding: 16, border: '1px solid #e5e7eb' }}>
              <h3>Following</h3>
              {following.length === 0 ? <p>Not following anyone.</p> : <p>{following.length} following</p>}
            </section>
          </div>
        </>
      )}
    </div>
  );
}
