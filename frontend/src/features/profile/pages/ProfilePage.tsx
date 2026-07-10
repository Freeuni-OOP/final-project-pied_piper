<<<<<<< Updated upstream
// Authenticated user's own profile page with stats, logs, reviews, and settings link.
=======
import { useEffect, useState } from 'react';
import useAuth from '../../../auth/useAuth';
import { getFollowers, getFollowing, PublicUser, getUserProfile, UserProfile } from '../../../api/userApi';
import { getUserLectureLogs } from '../../../api/lectureLogApi';
import { LectureLog, PageResponse } from '../../../types/lecture';

export default function ProfilePage() {
  const auth = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [followers, setFollowers] = useState<PublicUser[]>([]);
  const [following, setFollowing] = useState<PublicUser[]>([]);
  const [lectureLogs, setLectureLogs] = useState<LectureLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const user = auth.user;
    if (!user) return;
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const [profileData, followersData, followingData, logsResponse] = await Promise.all([
          getUserProfile(user.id),
          getFollowers(user.id),
          getFollowing(user.id),
          getUserLectureLogs(user.id),
        ]);
        setProfile(profileData);
        setFollowers(followersData);
        setFollowing(followingData);
        setLectureLogs(logsResponse.content);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load profile data.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [auth.user]);

  if (!auth.user) return <p className="p-6">Please login to view your profile.</p>;

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-lg border border-gray-200 p-8 mb-6">
        <h1 className="text-3xl font-bold mb-2 text-gray-900">{auth.user.name ?? auth.user.email}</h1>
        <p className="text-gray-600 mb-6">{auth.user.email}</p>

        {profile && (
          <div className="grid grid-cols-3 gap-4 mb-6">
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <p className="text-2xl font-bold text-blue-600">{profile.reviewCount || 0}</p>
              <p className="text-sm text-gray-600 mt-1">Reviews</p>
            </div>
            <div className="text-center p-4 bg-green-50 rounded-lg">
              <p className="text-2xl font-bold text-green-600">{profile.lectureLogsCount || 0}</p>
              <p className="text-sm text-gray-600 mt-1">Lectures Logged</p>
            </div>
            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <p className="text-2xl font-bold text-purple-600">{followers.length + (following.length || 0)}</p>
              <p className="text-sm text-gray-600 mt-1">Network</p>
            </div>
          </div>
        )}
      </div>

      {loading && <p className="text-gray-500">Loading profile data…</p>}
      {error && <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-6">{error}</div>}

      {!loading && !error && (
        <div className="space-y-6">
          {/* Followers/Following */}
          <div className="grid grid-cols-2 gap-6">
            <div className="bg-white rounded-lg border border-gray-200 p-6">
              <h3 className="text-lg font-semibold mb-4 text-gray-900">Followers ({followers.length})</h3>
              {followers.length === 0 ? (
                <p className="text-gray-500">No followers yet.</p>
              ) : (
                <ul className="space-y-2">
                  {followers.map((f) => (
                    <li key={f.id} className="text-gray-700">{f.name}</li>
                  ))}
                </ul>
              )}
            </div>
            <div className="bg-white rounded-lg border border-gray-200 p-6">
              <h3 className="text-lg font-semibold mb-4 text-gray-900">Following ({following.length})</h3>
              {following.length === 0 ? (
                <p className="text-gray-500">Not following anyone yet.</p>
              ) : (
                <ul className="space-y-2">
                  {following.map((f) => (
                    <li key={f.id} className="text-gray-700">{f.name}</li>
                  ))}
                </ul>
              )}
            </div>
          </div>

          {/* Lecture Logs */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h3 className="text-lg font-semibold mb-4 text-gray-900">Recent Lecture Logs</h3>
            {lectureLogs.length === 0 ? (
              <p className="text-gray-500">No lectures logged yet.</p>
            ) : (
              <ul className="space-y-3">
                {lectureLogs.map((log) => (
                  <li key={log.id} className="p-3 bg-gray-50 rounded-lg border border-gray-200">
                    <p className="font-medium text-gray-900">{log.lectureName}</p>
                    <p className="text-sm text-gray-600 mt-1">
                      {new Date(log.loggedAt).toLocaleDateString()}
                    </p>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
>>>>>>> Stashed changes
