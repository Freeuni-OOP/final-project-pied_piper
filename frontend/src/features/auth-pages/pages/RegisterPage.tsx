import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as authService from '../../../auth/authService';
import BackButton from '../../../components/BackButton';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setMessage(null);
    setLoading(true);
    try {
      const response = await authService.register({ name, email, password });
      sessionStorage.setItem('pendingVerifyEmail', email);
      sessionStorage.removeItem('pendingVerifyCode');
      setMessage(response?.message ?? 'Check your university email for the OTP code.');
      setTimeout(() => navigate('/verify'), 800);
    } catch (err: any) {
      setError(err?.message ?? err?.data?.message ?? 'Unable to register.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 520, margin: '0 auto' }}>
      <BackButton to="/" />
      <h2>Create Account</h2>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12 }}>
        <label>
          Full Name
          <input value={name} onChange={(e) => setName(e.target.value)} required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        <label>
          University Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required placeholder="you@freeuni.edu.ge" style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        <label>
          Password
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        {message && <div style={{ color: '#065f46' }}>{message}</div>}
        {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
        <button type="submit" disabled={loading} style={{ padding: '0.9rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}>
          {loading ? 'Submitting…' : 'Register'}
        </button>
      </form>
    </div>
  );
}
