import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getReviewsByUser, Review } from '../../../api/reviewApi';
import { getUserProfile, UserProfile } from '../../../api/userApi';
import BackButton from '../../../components/BackButton';

export default function UserReviewsPage() {
  const { userId } = useParams<{ userId: string }>();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
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
          getReviewsByUser(userId),
        ]);
        setProfile(profileData);
        setReviews(page.content ?? []);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load reviews.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <BackButton fallbackTo={userId ? `/profile/${userId}` : '/profile'} />
      <h1 style={{ marginBottom: 8 }}>Reviews</h1>
      {profile && <p style={{ color: '#6b7280', marginBottom: 24 }}>by {profile.name}</p>}
      {loading && <p>Loading…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && reviews.length === 0 && <p style={{ color: '#6b7280' }}>No reviews yet.</p>}
      <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 12 }}>
        {reviews.map((review) => (
          <li key={review.id} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
              <Link to={`/lectures/${review.lecture?.id}`} style={{ fontWeight: 600, color: '#2563eb' }}>
                {review.lecture?.title ?? 'Lecture'}
              </Link>
              <span>{review.rating} ★</span>
            </div>
            <p style={{ margin: 0 }}>{review.comment}</p>
            <small style={{ color: '#6b7280' }}>{new Date(review.createdAt).toLocaleString()}</small>
          </li>
        ))}
      </ul>
    </div>
  );
}
