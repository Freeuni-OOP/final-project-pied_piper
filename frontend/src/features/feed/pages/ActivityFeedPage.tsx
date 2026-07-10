import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getFeed } from '../../../api/feedApi';
import { FeedItem } from '../../../types/feed';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function ActivityFeedPage() {
  const auth = useAuth();
  const [feedItems, setFeedItems] = useState<FeedItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    const loadFeed = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await getFeed(page);
        const items = response.content ?? [];
        setFeedItems((prev) => (page === 0 ? items : [...prev, ...items]));
        setHasMore(page < (response.totalPages ?? 1) - 1);
      } catch (err: any) {
        setError(err?.message ?? 'Failed to load feed');
      } finally {
        setLoading(false);
      }
    };

    if (auth.isAuthenticated) {
      loadFeed();
    }
  }, [page, auth.isAuthenticated]);

  if (!auth.isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto p-6">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center">
          <p className="text-blue-900">Please log in to view your activity feed.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <BackButton to="/" />
      <h1 className="text-3xl font-bold mb-6 text-gray-900">Activity Feed</h1>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 text-red-700">
          {error}
        </div>
      )}

      {loading && feedItems.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500">Loading feed…</p>
        </div>
      )}

      {feedItems.length === 0 && !loading && (
        <div className="text-center py-12">
          <p className="text-gray-500">
            Your feed is empty. Log a lecture, write a review, or follow classmates to see activity here.
          </p>
        </div>
      )}

      <div className="space-y-4">
        {feedItems.map((item) => (
          <div
            key={item.id}
            className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow"
          >
            <div className="flex justify-between items-start mb-3">
              <div>
                <Link
                  to={`/profile/${item.actorId}`}
                  className="font-semibold text-gray-900 hover:text-blue-600"
                >
                  {item.actorName}
                  {item.actorId === auth.userId ? ' (you)' : ''}
                </Link>
                <p className="text-sm text-gray-500">
                  {new Date(item.createdAt).toLocaleDateString()}
                </p>
              </div>
              <span className="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-medium rounded-full">
                {item.type === 'REVIEW_CREATED' ? 'Review' : 'Lecture Log'}
              </span>
            </div>
            <p className="text-gray-700 mb-2">
              <Link to={`/lectures/${item.lectureId}`} className="font-semibold text-blue-600">
                {item.lectureTitle}
              </Link>
            </p>
          </div>
        ))}
      </div>

      {hasMore && (
        <div className="mt-8 text-center">
          <button
            onClick={() => setPage(page + 1)}
            disabled={loading}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            {loading ? 'Loading…' : 'Load More'}
          </button>
        </div>
      )}
    </div>
  );
}
