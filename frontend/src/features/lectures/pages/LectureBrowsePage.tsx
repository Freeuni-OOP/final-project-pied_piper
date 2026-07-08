import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { getLectures, Lecture } from '../../../api/lectureApi';

export default function LectureBrowsePage() {
  const [lectures, setLectures] = useState<Lecture[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const data = await getLectures();
        setLectures(data);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load lectures.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const filtered = useMemo(
    () => lectures.filter((lecture) => lecture.title.toLowerCase().includes(query.toLowerCase())),
    [lectures, query]
  );

  return (
    <div style={{ maxWidth: 960, margin: '0 auto' }}>
      <h2>Lecture Syllabus</h2>
      <div style={{ marginBottom: 16 }}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search lectures"
          style={{ width: '100%', padding: 10, borderRadius: 8, border: '1px solid #d1d5db' }}
        />
      </div>
      {loading && <p>Loading lectures…</p>}
      {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
      {!loading && !error && filtered.length === 0 && <p>No lectures found.</p>}
      <div style={{ display: 'grid', gap: 12 }}>
        {filtered.map((lecture) => (
          <Link key={lecture.id} to={`/lectures/${lecture.id}`} style={{ padding: 16, borderRadius: 12, background: '#fff', border: '1px solid #e5e7eb', textDecoration: 'none', color: '#111827' }}>
            <h3 style={{ margin: 0 }}>{lecture.title}</h3>
            <p style={{ margin: '8px 0 0', color: '#6b7280' }}>{lecture.description ?? 'No description available.'}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
