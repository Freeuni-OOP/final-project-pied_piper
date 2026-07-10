import { useEffect, useState, CSSProperties } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  listFaculties,
  listSemesters,
  listSubjects,
  getSubjectLectures,
  Faculty,
  Semester,
  Subject,
  SubjectLectures,
} from '../../../api/lectureApi';
import BackButton from '../../../components/BackButton';

const cardStyle: CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: 4,
  alignItems: 'center',
  justifyContent: 'center',
  padding: 16,
  background: '#f8fafc',
  border: '1px solid #e5e7eb',
  borderRadius: 10,
  color: '#111827',
  cursor: 'pointer',
  textAlign: 'center',
  width: '100%',
  font: 'inherit',
};

const selectedCardStyle: CSSProperties = {
  ...cardStyle,
  background: '#eff6ff',
  border: '2px solid #2563eb',
  fontWeight: 600,
};

const secondaryButtonStyle: CSSProperties = {
  padding: '0.75rem 1.2rem',
  background: '#fff',
  color: '#2563eb',
  border: '1px solid #2563eb',
  borderRadius: 8,
  cursor: 'pointer',
  fontWeight: 600,
  textAlign: 'left',
  width: '100%',
};

export default function LectureBrowsePage() {
  const navigate = useNavigate();
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [selectedFaculty, setSelectedFaculty] = useState<number | null>(null);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [selectedSemester, setSelectedSemester] = useState<number | null>(null);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [selectedSubjectData, setSelectedSubjectData] = useState<SubjectLectures | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const data = await listFaculties();
        setFaculties(data);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load faculties.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  useEffect(() => {
    if (!selectedFaculty) return;

    const load = async () => {
      setError(null);
      try {
        const data = await listSemesters(selectedFaculty);
        setSemesters(data);
        setSelectedSemester(null);
        setSubjects([]);
        setSelectedSubjectData(null);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load semesters.');
      }
    };
    load();
  }, [selectedFaculty]);

  useEffect(() => {
    if (!selectedSemester) return;

    const load = async () => {
      setError(null);
      try {
        const data = await listSubjects(selectedSemester);
        setSubjects(data);
        setSelectedSubjectData(null);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load subjects.');
      }
    };
    load();
  }, [selectedSemester]);

  useEffect(() => {
    if (!selectedSubjectData?.id) return;

    const load = async () => {
      setError(null);
      try {
        const data = await getSubjectLectures(selectedSubjectData.id);
        setSelectedSubjectData(data);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load lectures.');
      }
    };
    load();
  }, [selectedSubjectData?.id]);

  const handleSubjectClick = (subject: Subject) => {
    setSelectedSubjectData({ ...subject, lectures: [] } as SubjectLectures);
  };

  return (
    <div style={{ maxWidth: 960, margin: '0 auto' }}>
      <BackButton />

      <div
        style={{
          background: '#fff',
          border: '1px solid #e5e7eb',
          borderRadius: 12,
          padding: 24,
          marginBottom: 24,
        }}
      >
        <h1 style={{ marginBottom: 8 }}>Browse Lectures</h1>
        <p style={{ color: '#6b7280', marginBottom: 0 }}>
          Choose a faculty, semester, and subject to open lectures.
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

      {loading && faculties.length === 0 && <p style={{ color: '#6b7280' }}>Loading faculties…</p>}

      <div
        style={{
          background: '#fff',
          border: '1px solid #e5e7eb',
          borderRadius: 12,
          padding: 24,
          marginBottom: 24,
        }}
      >
        <h2 style={{ marginBottom: 16, fontSize: 18 }}>Faculties</h2>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
            gap: 12,
          }}
        >
          {faculties.map((faculty) => (
            <button
              key={faculty.id}
              type="button"
              onClick={() => setSelectedFaculty(faculty.id)}
              style={selectedFaculty === faculty.id ? selectedCardStyle : cardStyle}
            >
              <strong style={{ fontSize: 15 }}>{faculty.name}</strong>
            </button>
          ))}
        </div>
      </div>

      {selectedFaculty && (
        <div
          style={{
            background: '#fff',
            border: '1px solid #e5e7eb',
            borderRadius: 12,
            padding: 24,
            marginBottom: 24,
          }}
        >
          <h2 style={{ marginBottom: 16, fontSize: 18 }}>Semesters</h2>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))',
              gap: 12,
            }}
          >
            {semesters.map((semester) => (
              <button
                key={semester.id}
                type="button"
                onClick={() => setSelectedSemester(semester.id)}
                style={selectedSemester === semester.id ? selectedCardStyle : cardStyle}
              >
                <strong style={{ fontSize: 15 }}>{semester.number || semester.name}</strong>
              </button>
            ))}
          </div>
        </div>
      )}

      {selectedSemester && (
        <div
          style={{
            background: '#fff',
            border: '1px solid #e5e7eb',
            borderRadius: 12,
            padding: 24,
            marginBottom: 24,
          }}
        >
          <h2 style={{ marginBottom: 16, fontSize: 18 }}>Subjects</h2>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
              gap: 12,
            }}
          >
            {subjects.map((subject) => (
              <button
                key={subject.id}
                type="button"
                onClick={() => handleSubjectClick(subject)}
                style={selectedSubjectData?.id === subject.id ? selectedCardStyle : cardStyle}
              >
                <strong style={{ fontSize: 15 }}>{subject.name}</strong>
              </button>
            ))}
          </div>
        </div>
      )}

      {selectedSubjectData && (
        <div
          style={{
            background: '#fff',
            border: '1px solid #e5e7eb',
            borderRadius: 12,
            padding: 24,
            marginBottom: 24,
          }}
        >
          <h2 style={{ marginBottom: 16, fontSize: 18 }}>
            {selectedSubjectData.name} — Lectures
          </h2>

          {(!selectedSubjectData.lectures || selectedSubjectData.lectures.length === 0) && (
            <p style={{ color: '#6b7280' }}>Loading lectures…</p>
          )}

          <div style={{ display: 'grid', gap: 12 }}>
            {selectedSubjectData.lectures?.map((lecture) => (
              <button
                key={lecture.id}
                type="button"
                onClick={() => navigate(`/lectures/${lecture.id}`)}
                style={secondaryButtonStyle}
              >
                <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                  <span style={{ color: '#111827', fontWeight: 700 }}>
                    {lecture.title}
                  </span>
                  <span style={{ color: '#6b7280', fontSize: 13, fontWeight: 500 }}>
                    Week {lecture.week}
                    {lecture.lectureNumber != null ? ` · Lecture ${lecture.lectureNumber}` : ''}
                    {lecture.type ? ` · ${lecture.type}` : ''}
                  </span>
                  {lecture.description && (
                    <span style={{ color: '#6b7280', fontSize: 13, fontWeight: 400 }}>
                      {lecture.description}
                    </span>
                  )}
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
