import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getLecture, Lecture } from '../../../api/lectureApi';
import { getRatingSummary } from '../../../api/reviewApi';

export default function LectureDetailPage() {
  const { lectureId } = useParams<{ lectureId: string }>();
  const [lecture, setLecture] = useState<Lecture | null>(null);
  const [ratingSummary, setRatingSummary] = useState<{ average: number; count: number } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const id = Number(lectureId);
    if (!id) {
      setError('Invalid lecture ID.');
      setLoading(false);
      return;
    }

    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const [data, rating] = await Promise.all([getLecture(id), getRatingSummary(id)]);
        setLecture(data);
        setRatingSummary(rating);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load lecture.');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [lectureId]);

  if (loading) return <p>Loading lecture details…</p>;
  if (error) return <div style={{ color: '#b91c1c' }}>{error}</div>;
  if (!lecture) return <p>Lecture not found.</p>;

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <header style={{ marginBottom: 24 }}>
        <h1>{lecture.title}</h1>
        <p>{lecture.description ?? 'No description available.'}</p>
        <div style={{ marginTop: 12 }}>
          <strong>Faculty:</strong> {lecture.faculty ?? 'Unknown'}
        </div>
        {ratingSummary && (
          <div style={{ marginTop: 12 }}>
            <strong>Rating:</strong> {ratingSummary.average.toFixed(1)} / 5 ({ratingSummary.count} reviews)
          </div>
        )}
      </header>
      <Link to={`/lectures/${lecture.id}/reviews`} style={{ color: '#2563eb' }}>
        View and write reviews
      </Link>
    </div>
  );
}
