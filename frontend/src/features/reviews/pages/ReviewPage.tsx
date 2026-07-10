import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { createReview, getRatingSummary, getReviewsForLecture, Review, RatingSummary } from '../../../api/reviewApi';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function ReviewPage() {
  const { lectureId } = useParams<{ lectureId: string }>();
  const auth = useAuth();
  const [reviews, setReviews] = useState<Review[]>([]);
  const [ratingSummary, setRatingSummary] = useState<RatingSummary | null>(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const id = Number(lectureId);
    if (!Number.isFinite(id) || id <= 0) {
      setError('Invalid lecture ID.');
      setLoading(false);
      return;
    }

    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const [page, summary] = await Promise.all([
          getReviewsForLecture(id),
          getRatingSummary(id),
        ]);
        setReviews(page.content ?? []);
        setRatingSummary(summary);
      } catch (err: any) {
        setError(err?.message ?? err?.data?.message ?? 'Unable to load reviews.');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [lectureId]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!auth.token) {
      setSubmitError('Please login to post a review.');
      return;
    }
    const id = Number(lectureId);
    setSubmitError(null);
    setSubmitting(true);
    try {
      const created = await createReview(id, { rating, comment });
      setReviews((prev) => [created, ...prev]);
      setComment('');
      setRating(5);
      setRatingSummary(await getRatingSummary(id));
    } catch (err: any) {
      setSubmitError(err?.message ?? err?.data?.message ?? 'Unable to submit review.');
    } finally {
      setSubmitting(false);
    }
  };

  const average = ratingSummary?.averageRating ?? 0;
  const total = ratingSummary?.totalReviews ?? 0;

  return (
    <div style={{ maxWidth: 900, margin: '0 auto' }}>
      <BackButton fallbackTo={lectureId ? `/lectures/${lectureId}` : '/lectures'} />
      <h2>Write a review</h2>
      {loading && <p>Loading reviews…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && (
        <>
          <section style={{ marginBottom: 24, padding: 16, background: '#fff', borderRadius: 12, border: '1px solid #e5e7eb' }}>
            {!auth.token ? (
              <p>
                Please <Link to="/login">log in</Link> to submit your review.
              </p>
            ) : (
              <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12 }}>
                <label>
                  Rating
                  <select value={rating} onChange={(e) => setRating(Number(e.target.value))} style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }}>
                    {[5, 4, 3, 2, 1].map((value) => (
                      <option key={value} value={value}>
                        {value} star{value > 1 ? 's' : ''}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  Review
                  <textarea value={comment} onChange={(e) => setComment(e.target.value)} rows={5} required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
                </label>
                {submitError && <div style={{ color: '#b91c1c' }}>{submitError}</div>}
                <button type="submit" disabled={submitting} style={{ padding: '0.9rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}>
                  {submitting ? 'Submitting…' : 'Submit Review'}
                </button>
              </form>
            )}
          </section>

          <section style={{ background: '#fff', borderRadius: 12, padding: 16, border: '1px solid #e5e7eb' }}>
            <h3>Community reviews</h3>
            <p>
              Average rating: {average.toFixed(1)} / 5 ({total} review{total === 1 ? '' : 's'})
            </p>
            {reviews.length === 0 ? (
              <p>No reviews yet.</p>
            ) : (
              <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 16 }}>
                {reviews.map((review) => (
                  <li key={review.id} style={{ padding: 16, borderRadius: 10, background: '#f8fafc', border: '1px solid #e5e7eb' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                      <strong>{review.author?.name ?? 'Anonymous'}</strong>
                      <span>{review.rating} ★</span>
                    </div>
                    <p style={{ margin: 0 }}>{review.comment}</p>
                    <small style={{ display: 'block', marginTop: 10, color: '#6b7280' }}>{new Date(review.createdAt).toLocaleString()}</small>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </>
      )}
    </div>
  );
}
