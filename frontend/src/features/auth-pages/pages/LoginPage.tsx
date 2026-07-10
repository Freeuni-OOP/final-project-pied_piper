import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import * as authService from '../../../auth/authService';
import useAuth from '../../../auth/useAuth';

export default function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await authService.login({ email, password });
      auth.login(response.token, response.user);
      navigate('/');
    } catch (err: any) {
      setError(err?.message ?? 'Unable to login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 480, margin: '0 auto' }}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12 }}>
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        <label>
          Password
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
        <button type="submit" disabled={loading} style={{ padding: '0.8rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}>
          {loading ? 'Logging in…' : 'Login'}
        </button>
      </form>
      <div style={{ marginTop: 16 }}>
        <Link to="/register">Register</Link>
        <span style={{ margin: '0 8px' }}>/</span>
        <Link to="/verify">Verify OTP</Link>
      </div>
      {auth.token && <p style={{ marginTop: 16 }}>Already logged in as {auth.user?.email}</p>}
    </div>
  );
}
