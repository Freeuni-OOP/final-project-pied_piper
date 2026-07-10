import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  deleteAccount,
  followUser,
  getFollowStatus,
  getUserProfile,
  unfollowUser,
  UserProfile,
} from '../../../api/userApi';
import { startConversation } from '../../../api/chatApi';
import { getToken } from '../../../auth/tokenStorage';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function UserProfilePage() {
  const { userId } = useParams<{ userId: string }>();
  const auth = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [messaging, setMessaging] = useState(false);

  const isOwnProfile = !!auth.userId && auth.userId === userId;

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const profileData = await getUserProfile(userId);
        setProfile(profileData);
        if (auth.token && auth.userId !== userId) {
          try {
            const status = await getFollowStatus(userId);
            setIsFollowing(status.isFollowing);
          } catch {
            setIsFollowing(false);
          }
        }
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load profile.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId, auth.token, auth.userId]);

  const handleToggleFollow = async () => {
    if (!userId || !auth.token || !getToken()) {
      navigate('/login');
      return;
    }
    setActionError(null);
    try {
      const result = isFollowing ? await unfollowUser(userId) : await followUser(userId);
      setIsFollowing(result.isFollowing);
      const refreshed = await getUserProfile(userId);
      setProfile(refreshed);
    } catch (err: any) {
      if (err?.status === 401) {
        setActionError('Please log in again to follow users.');
        navigate('/login');
        return;
      }
      setActionError(err?.message ?? 'Unable to update follow status.');
    }
  };

  const handleMessage = async () => {
    if (!userId || !auth.token || !getToken()) {
      navigate('/login');
      return;
    }
    setMessaging(true);
    setActionError(null);
    try {
      const conversation = await startConversation(userId);
      navigate(`/chat?conversationId=${conversation.id}`);
    } catch (err: any) {
      if (err?.status === 401) {
        setActionError('Please log in again to send messages.');
        navigate('/login');
        return;
      }
      setActionError(err?.message ?? 'Unable to start conversation.');
    } finally {
      setMessaging(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('Delete your account permanently? This cannot be undone.')) return;
    setDeleting(true);
    setActionError(null);
    try {
      await deleteAccount();
      auth.logout();
      navigate('/');
    } catch (err: any) {
      setActionError(err?.message ?? 'Unable to delete account.');
      setDeleting(false);
    }
  };

  if (!userId) return <p>Invalid profile URL.</p>;

  return (
    <div style={{ maxWidth: 900, margin: '0 auto' }}>
      <BackButton to="/" />
      {loading && <p>Loading profile…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && profile && (
        <>
          <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 12, padding: 24, marginBottom: 24 }}>
            <h1 style={{ marginBottom: 8 }}>{profile.name}</h1>
            <p style={{ color: '#6b7280', marginBottom: 20 }}>{profile.email}</p>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: 12, marginBottom: 20 }}>
              <Link to={`/profile/${userId}/reviews`} style={statCardStyle}>
                <strong style={{ fontSize: 22 }}>{profile.reviewCount ?? 0}</strong>
                <span>Reviews</span>
              </Link>
              <Link to={`/profile/${userId}/logs`} style={statCardStyle}>
                <strong style={{ fontSize: 22 }}>{profile.lectureLogCount ?? 0}</strong>
                <span>Lectures logged</span>
              </Link>
              <Link to={`/profile/${userId}/followers`} style={statCardStyle}>
                <strong style={{ fontSize: 22 }}>{profile.followerCount ?? 0}</strong>
                <span>Followers</span>
              </Link>
              <Link to={`/profile/${userId}/following`} style={statCardStyle}>
                <strong style={{ fontSize: 22 }}>{profile.followingCount ?? 0}</strong>
                <span>Following</span>
              </Link>
            </div>

            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              {!isOwnProfile && auth.token && (
                <>
                  <button
                    type="button"
                    onClick={handleToggleFollow}
                    style={{
                      padding: '0.75rem 1.2rem',
                      background: isFollowing ? '#ef4444' : '#2563eb',
                      color: '#fff',
                      border: 'none',
                      borderRadius: 8,
                      cursor: 'pointer',
                    }}
                  >
                    {isFollowing ? 'Unfollow' : 'Follow'}
                  </button>
                  <button
                    type="button"
                    onClick={handleMessage}
                    disabled={messaging}
                    style={{
                      padding: '0.75rem 1.2rem',
                      background: '#111827',
                      color: '#fff',
                      border: 'none',
                      borderRadius: 8,
                      cursor: 'pointer',
                    }}
                  >
                    {messaging ? 'Opening…' : 'Message'}
                  </button>
                </>
              )}
              {isOwnProfile && (
                <button
                  type="button"
                  onClick={handleDeleteAccount}
                  disabled={deleting}
                  style={{
                    padding: '0.75rem 1.2rem',
                    background: '#fff',
                    color: '#b91c1c',
                    border: '1px solid #fecaca',
                    borderRadius: 8,
                    cursor: 'pointer',
                  }}
                >
                  {deleting ? 'Deleting…' : 'Delete account'}
                </button>
              )}
            </div>
            {actionError && <div style={{ color: '#b91c1c', marginTop: 12 }}>{actionError}</div>}
          </div>
        </>
      )}
    </div>
  );
}

const statCardStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: 4,
  alignItems: 'center',
  padding: 16,
  background: '#f8fafc',
  border: '1px solid #e5e7eb',
  borderRadius: 10,
  textDecoration: 'none',
  color: '#111827',
};
