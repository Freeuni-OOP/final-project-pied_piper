import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as authService from '../../../auth/authService';
import useAuth from '../../../auth/useAuth';

export default function VerifyOtpPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setMessage(null);
    setLoading(true);
    try {
      const response = await authService.verifyOtp({ email, otp });
      auth.login(response.token, response.user);
      setMessage('Verification successful. Welcome!');
      navigate('/');
    } catch (err: any) {
      setError(err?.message ?? 'Unable to verify OTP.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 520, margin: '0 auto' }}>
      <h2>Verify OTP</h2>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12 }}>
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        <label>
          OTP Code
          <input value={otp} onChange={(e) => setOtp(e.target.value)} required style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }} />
        </label>
        {message && <div style={{ color: '#065f46' }}>{message}</div>}
        {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
        <button type="submit" disabled={loading} style={{ padding: '0.9rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}>
          {loading ? 'Verifying…' : 'Verify OTP'}
        </button>
      </form>
      <p style={{ marginTop: 16 }}>This request uses the agreed auth API contract and saves your token when verification succeeds.</p>
    </div>
  );
}
