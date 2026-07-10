import { useEffect, useState, CSSProperties } from 'react';
import { useNavigate } from 'react-router-dom';
import { getFeed } from '../../../api/feedApi';
import { getReviewsByUser } from '../../../api/reviewApi';
import { FeedItem } from '../../../types/feed';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

const panelStyle: CSSProperties = {
  background: '#fff',
  border: '1px solid #e5e7eb',
  borderRadius: 12,
  padding: 24,
  marginBottom: 16,
};

const primaryButtonStyle: CSSProperties = {
  padding: '0.75rem 1.2rem',
  background: '#2563eb',
  color: '#fff',
  border: 'none',
  borderRadius: 8,
  cursor: 'pointer',
  fontWeight: 600,
};

const secondaryButtonStyle: CSSProperties = {
  padding: '0.55rem 0.9rem',
  background: '#fff',
  color: '#2563eb',
  border: '1px solid #2563eb',
  borderRadius: 8,
  cursor: 'pointer',
  fontWeight: 600,
};

const cardButtonStyle: CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: 4,
  alignItems: 'flex-start',
  padding: 16,
  background: '#f8fafc',
  border: '1px solid #e5e7eb',
  borderRadius: 10,
  color: '#111827',
  cursor: 'pointer',
  textAlign: 'left',
  width: '100%',
  font: 'inherit',
};

export default function ActivityFeedPage() {
  const auth = useAuth();
  const navigate = useNavigate();
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
        let items = response.content ?? [];

        // If feed API omitted review fields, fill them from each author's reviews.
        const needsReviewText = items.some(
          (item) =>
            item.type === 'REVIEW_CREATED' &&
            (item.comment == null || item.comment === '' || item.rating == null)
        );
        if (needsReviewText) {
          const actorIds = [...new Set(items.filter((i) => i.type === 'REVIEW_CREATED').map((i) => i.actorId))];
          const reviewMaps = await Promise.all(
            actorIds.map(async (actorId) => {
              try {
                const pageData = await getReviewsByUser(actorId, 0, 100);
                return { actorId, reviews: pageData.content ?? [] };
              } catch {
                return { actorId, reviews: [] as Awaited<ReturnType<typeof getReviewsByUser>>['content'] };
              }
            })
          );
          const byActor = new Map(reviewMaps.map((entry) => [entry.actorId, entry.reviews]));
          items = items.map((item) => {
            if (item.type !== 'REVIEW_CREATED') return item;
            if (item.comment && item.rating != null) return item;
            const reviews = byActor.get(item.actorId) ?? [];
            const match =
              (item.reviewId != null && reviews.find((r) => r.id === item.reviewId)) ||
              reviews.find((r) => r.lecture?.id === item.lectureId);
            if (!match) return item;
            return {
              ...item,
              rating: item.rating ?? match.rating,
              comment: item.comment || match.comment,
              reviewId: item.reviewId ?? match.id,
            };
          });
        }

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
      <div style={{ maxWidth: 720, margin: '0 auto' }}>
        <BackButton />
        <div style={panelStyle}>
          <p style={{ color: '#374151', margin: 0 }}>Please log in to view your activity feed.</p>
          <button
            type="button"
            onClick={() => navigate('/login')}
            style={{ ...primaryButtonStyle, marginTop: 16 }}
          >
            Log in
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <BackButton />

      <div style={panelStyle}>
        <h1 style={{ marginBottom: 8 }}>Activity Feed</h1>
        <p style={{ color: '#6b7280', margin: 0 }}>
          Your activity and updates from people you follow.
        </p>
      </div>

      {error && (
        <div
          style={{
            background: '#fef2f2',
            border: '1px solid #fecaca',
            color: '#b91c1c',
            borderRadius: 8,
            padding: 12,
            marginBottom: 16,
          }}
        >
          {error}
        </div>
      )}

      {loading && feedItems.length === 0 && (
        <div style={panelStyle}>
          <p style={{ color: '#6b7280', margin: 0 }}>Loading feed…</p>
        </div>
      )}

      {feedItems.length === 0 && !loading && (
        <div style={panelStyle}>
          <p style={{ color: '#6b7280', margin: 0 }}>
            Your feed is empty. Log a lecture, write a review, or follow classmates to see activity here.
          </p>
        </div>
      )}

      <div style={{ display: 'grid', gap: 12 }}>
        {feedItems.map((item) => (
          <div key={item.id} style={{ ...panelStyle, marginBottom: 0, padding: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 12 }}>
              <div>
                <button
                  type="button"
                  onClick={() => navigate(`/profile/${item.actorId}`)}
                  style={{
                    ...secondaryButtonStyle,
                    padding: '0.35rem 0.75rem',
                    marginBottom: 6,
                  }}
                >
                  {item.actorName}
                  {item.actorId === auth.userId ? ' (you)' : ''}
                </button>
                <p style={{ margin: 0, color: '#6b7280', fontSize: 13 }}>
                  {new Date(item.createdAt).toLocaleDateString()}
                </p>
              </div>
              <span
                style={{
                  alignSelf: 'flex-start',
                  padding: '6px 10px',
                  background: '#eff6ff',
                  color: '#1d4ed8',
                  borderRadius: 8,
                  fontSize: 12,
                  fontWeight: 600,
                }}
              >
                {item.type === 'REVIEW_CREATED' ? 'Review' : 'Lecture Log'}
              </span>
            </div>

            <button
              type="button"
              onClick={() => navigate(`/lectures/${item.lectureId}`)}
              style={cardButtonStyle}
            >
              <strong style={{ fontSize: 15 }}>{item.lectureTitle}</strong>
              <span style={{ color: '#6b7280', fontSize: 13 }}>Open lecture</span>
            </button>

            {item.type === 'REVIEW_CREATED' && (
              <div
                style={{
                  marginTop: 12,
                  padding: 12,
                  background: '#f8fafc',
                  border: '1px solid #e5e7eb',
                  borderRadius: 10,
                }}
              >
                {item.rating != null && (
                  <p style={{ margin: '0 0 8px', fontWeight: 700, color: '#111827' }}>
                    {item.actorName}&apos;s rating: {item.rating} ★
                  </p>
                )}
                {item.comment?.trim() ? (
                  <p style={{ margin: 0, color: '#374151', lineHeight: 1.5, whiteSpace: 'pre-wrap' }}>
                    {item.comment}
                  </p>
                ) : (
                  <p style={{ margin: 0, color: '#9ca3af', fontStyle: 'italic' }}>
                    No written review
                  </p>
                )}
              </div>
            )}
          </div>
        ))}
      </div>

      {hasMore && (
        <div style={{ marginTop: 20, textAlign: 'center' }}>
          <button
            type="button"
            onClick={() => setPage(page + 1)}
            disabled={loading}
            style={{
              ...primaryButtonStyle,
              opacity: loading ? 0.6 : 1,
              cursor: loading ? 'not-allowed' : 'pointer',
            }}
          >
            {loading ? 'Loading…' : 'Load more'}
          </button>
        </div>
      )}
    </div>
  );
}
