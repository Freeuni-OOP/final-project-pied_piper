// Page for logging attendance of a lecture with date watched and optional quick rating.

import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { logLecture } from '../../../api/lectureLogApi';
import useAuth from '../../../auth/useAuth';

export default function LogLecturePage() {
  const { lectureId } = useParams<{ lectureId: string }>();
  const navigate = useNavigate();
  const auth = useAuth();
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!lectureId || !auth.isAuthenticated) return;

    setError(null);
    setLoading(true);
    try {
      await logLecture(Number(lectureId), notes);
      // Navigate back to lecture detail or profile
      navigate(`/lectures/${lectureId}`);
    } catch (err: any) {
      setError(err?.message ?? 'Failed to log lecture');
    } finally {
      setLoading(false);
    }
  };

  if (!auth.isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto p-6">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center">
          <p className="text-blue-900">Please log in to log a lecture.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="bg-white rounded-lg border border-gray-200 p-8">
        <h1 className="text-2xl font-bold mb-6 text-gray-900">Log Lecture</h1>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Notes (optional)
            </label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={4}
              placeholder="Add any notes about this lecture..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="flex gap-3">
            <button
              type="submit"
              disabled={loading}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              {loading ? 'Logging…' : 'Log Lecture'}
            </button>
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-6 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

