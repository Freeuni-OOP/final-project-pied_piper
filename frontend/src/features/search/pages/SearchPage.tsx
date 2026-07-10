import { FormEvent, useState } from 'react';
import { Link } from 'react-router-dom';
import { searchLectures, Lecture } from '../../../api/lectureApi';
import { searchUsers, PublicUser } from '../../../api/userApi';
import BackButton from '../../../components/BackButton';

type SearchMode = 'lecture' | 'user';

export default function SearchPage() {
  const [mode, setMode] = useState<SearchMode>('lecture');
  const [query, setQuery] = useState('');
  const [lectures, setLectures] = useState<Lecture[]>([]);
  const [users, setUsers] = useState<PublicUser[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (event: FormEvent) => {
    event.preventDefault();
    const q = query.trim();
    if (!q) return;

    setLoading(true);
    setError(null);
    setSearched(true);
    try {
      if (mode === 'lecture') {
        const page = await searchLectures(q);
        setLectures(page.content ?? []);
        setUsers([]);
      } else {
        const page = await searchUsers(q);
        setUsers(page.content ?? []);
        setLectures([]);
      }
    } catch (err: any) {
      setError(err?.message ?? err?.data?.message ?? 'Search failed.');
      setLectures([]);
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <BackButton to="/" />
      <h1 style={{ marginBottom: 16 }}>Search</h1>

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <button
          type="button"
          onClick={() => setMode('lecture')}
          style={{
            padding: '0.6rem 1rem',
            borderRadius: 8,
            border: mode === 'lecture' ? '2px solid #2563eb' : '1px solid #d1d5db',
            background: mode === 'lecture' ? '#eff6ff' : '#fff',
            cursor: 'pointer',
            fontWeight: mode === 'lecture' ? 700 : 400,
          }}
        >
          Lectures
        </button>
        <button
          type="button"
          onClick={() => setMode('user')}
          style={{
            padding: '0.6rem 1rem',
            borderRadius: 8,
            border: mode === 'user' ? '2px solid #2563eb' : '1px solid #d1d5db',
            background: mode === 'user' ? '#eff6ff' : '#fff',
            cursor: 'pointer',
            fontWeight: mode === 'user' ? 700 : 400,
          }}
        >
          Users
        </button>
      </div>

      <form onSubmit={handleSearch} style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={mode === 'lecture' ? 'Search lectures…' : 'Search users by name or email…'}
          style={{ flex: 1, padding: 10, borderRadius: 8, border: '1px solid #d1d5db' }}
        />
        <button
          type="submit"
          disabled={loading || !query.trim()}
          style={{
            padding: '0.7rem 1.2rem',
            background: '#2563eb',
            color: '#fff',
            border: 'none',
            borderRadius: 8,
            cursor: 'pointer',
          }}
        >
          {loading ? 'Searching…' : 'Search'}
        </button>
      </form>

      {error && <div style={{ color: '#b91c1c', marginBottom: 16 }}>{error}</div>}

      {mode === 'lecture' && (
        <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 12 }}>
          {lectures.map((lecture) => (
            <li key={lecture.id} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 16 }}>
              <Link to={`/lectures/${lecture.id}`} style={{ color: '#111827', textDecoration: 'none', fontWeight: 600 }}>
                {lecture.title}
              </Link>
              {lecture.description && (
                <p style={{ margin: '8px 0 0', color: '#6b7280' }}>{lecture.description}</p>
              )}
            </li>
          ))}
          {searched && !loading && lectures.length === 0 && <p style={{ color: '#6b7280' }}>No lectures found.</p>}
        </ul>
      )}

      {mode === 'user' && (
        <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'grid', gap: 12 }}>
          {users.map((user) => (
            <li key={user.id} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 16 }}>
              <Link to={`/profile/${user.id}`} style={{ color: '#111827', textDecoration: 'none', fontWeight: 600 }}>
                {user.name}
              </Link>
              <p style={{ margin: '4px 0 0', color: '#6b7280' }}>{user.email}</p>
            </li>
          ))}
          {searched && !loading && users.length === 0 && <p style={{ color: '#6b7280' }}>No users found.</p>}
        </ul>
      )}
    </div>
  );
}
