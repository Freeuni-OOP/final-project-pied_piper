import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getLecture, Lecture } from '../../../api/lectureApi';
import { getRatingSummary, getReviewsForLecture, Review, RatingSummary } from '../../../api/reviewApi';
import { getMyLectureLog, logLecture, unlogLecture } from '../../../api/lectureLogApi';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function LectureDetailPage() {
  const { lectureId } = useParams<{ lectureId: string }>();
  const auth = useAuth();
  const [lecture, setLecture] = useState<Lecture | null>(null);
  const [ratingSummary, setRatingSummary] = useState<RatingSummary | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [logged, setLogged] = useState(false);
  const [logBusy, setLogBusy] = useState(false);
  const [logError, setLogError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
        const [data, rating, reviewPage] = await Promise.all([
          getLecture(id),
          getRatingSummary(id),
          getReviewsForLecture(id),
        ]);
        setLecture(data);
        setRatingSummary(rating);
        setReviews(reviewPage.content ?? []);

        if (auth.token) {
          const myLog = await getMyLectureLog(id);
          setLogged(!!myLog);
        } else {
          setLogged(false);
        }
      } catch (err: any) {
        setError(err?.message ?? err?.data?.message ?? 'Unable to load lecture.');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [lectureId, auth.token]);

  const toggleLog = async () => {
    if (!lecture || logBusy) return;
    setLogBusy(true);
    setLogError(null);
    try {
      if (logged) {
        await unlogLecture(lecture.id);
        setLogged(false);
      } else {
        await logLecture(lecture.id);
        setLogged(true);
      }
    } catch (err: any) {
      setLogError(err?.message ?? err?.data?.message ?? 'Unable to update lecture log.');
    } finally {
      setLogBusy(false);
    }
  };

  if (loading) return <p>Loading lecture details…</p>;
  if (error) return <div style={{ color: '#b91c1c' }}>{error}</div>;
  if (!lecture) return <p>Lecture not found.</p>;

  const average = ratingSummary?.averageRating ?? 0;
  const total = ratingSummary?.totalReviews ?? 0;

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <BackButton fallbackTo="/lectures" />
      <header style={{ marginBottom: 24 }}>
        <h1 style={{ marginBottom: 8 }}>{lecture.title}</h1>
        <p style={{ color: '#4b5563', marginBottom: 16 }}>
          {lecture.description ?? 'No description available.'}
        </p>
        <div style={{ display: 'grid', gap: 8, color: '#374151' }}>
          {lecture.week != null && (
            <div>
              <strong>Week:</strong> {lecture.week}
            </div>
          )}
          {lecture.lectureNumber != null && (
            <div>
              <strong>Lecture #:</strong> {lecture.lectureNumber}
            </div>
          )}
          {lecture.type && (
            <div>
              <strong>Type:</strong> {lecture.type}
            </div>
          )}
          {lecture.reading && (
            <div>
              <strong>Reading:</strong> {lecture.reading}
            </div>
          )}
          <div>
            <strong>Rating:</strong> {average.toFixed(1)} / 5 ({total} review{total === 1 ? '' : 's'})
          </div>
        </div>
      </header>

      <div style={{ display: 'flex', gap: 12, marginBottom: 12, flexWrap: 'wrap', alignItems: 'center' }}>
        {auth.token ? (
          <Link
            to={`/lectures/${lecture.id}/reviews`}
            style={{
              display: 'inline-block',
              padding: '0.75rem 1.25rem',
              background: '#2563eb',
              color: '#fff',
              borderRadius: 8,
              textDecoration: 'none',
              fontWeight: 600,
            }}
          >
            Write a review
          </Link>
        ) : (
          <Link to="/login" style={{ color: '#2563eb' }}>
            Log in to write a review
          </Link>
        )}
        {auth.token && (
          <button
            type="button"
            onClick={toggleLog}
            disabled={logBusy}
            aria-pressed={logged}
            style={{
              padding: '0.75rem 1.25rem',
              background: logged ? '#2563eb' : '#fff',
              color: logged ? '#fff' : '#2563eb',
              border: '1px solid #2563eb',
              borderRadius: 8,
              fontWeight: 600,
              cursor: logBusy ? 'not-allowed' : 'pointer',
              opacity: logBusy ? 0.7 : 1,
            }}
          >
            {logBusy ? 'Saving…' : logged ? '✓ Logged' : 'Log lecture'}
          </button>
        )}
      </div>
      {logError && (
        <p style={{ color: '#b91c1c', marginBottom: 24 }}>{logError}</p>
      )}

      <section style={{ marginTop: 20 }}>
        <h2 style={{ marginBottom: 16 }}>Reviews</h2>
        {reviews.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No reviews yet. Be the first to review this lecture.</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 16 }}>
            {reviews.map((review) => (
              <li
                key={review.id}
                style={{
                  padding: 16,
                  borderRadius: 10,
                  background: '#f8fafc',
                  border: '1px solid #e5e7eb',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                  <Link to={`/profile/${review.author?.id}`} style={{ fontWeight: 700, color: '#111827', textDecoration: 'none' }}>
                    {review.author?.name ?? 'Anonymous'}
                  </Link>
                  <span>{review.rating} ★</span>
                </div>
                <p style={{ margin: 0 }}>{review.comment}</p>
                <small style={{ display: 'block', marginTop: 10, color: '#6b7280' }}>
                  {new Date(review.createdAt).toLocaleString()}
                </small>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
