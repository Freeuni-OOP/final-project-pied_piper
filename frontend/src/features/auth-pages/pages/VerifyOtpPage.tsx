import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as authService from '../../../auth/authService';
import useAuth from '../../../auth/useAuth';
import BackButton from '../../../components/BackButton';

export default function VerifyOtpPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    const pendingEmail = sessionStorage.getItem('pendingVerifyEmail');
    if (!pendingEmail) {
      setError('No pending registration found. Please register first.');
      return;
    }
    setEmail(pendingEmail);
  }, []);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!email) {
      setError('No pending registration found. Please register first.');
      return;
    }
    setError(null);
    setMessage(null);
    setLoading(true);
    try {
      const response = await authService.verifyOtp({ email, otp });
      auth.login(response.token, response.user);
      sessionStorage.removeItem('pendingVerifyEmail');
      sessionStorage.removeItem('pendingVerifyCode');
      setMessage('Verification successful. Welcome!');
      navigate('/');
    } catch (err: any) {
      setError(err?.message ?? err?.data?.message ?? 'Unable to verify OTP.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 520, margin: '0 auto' }}>
      <BackButton to="/register" label="← Back to register" />
      <h2>Verify OTP</h2>
      {email && (
        <p style={{ color: '#6b7280', marginBottom: 16 }}>
          Enter the code sent to <strong>{email}</strong>
        </p>
      )}
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12 }}>
        <label>
          OTP Code
          <input
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            required
            maxLength={6}
            inputMode="numeric"
            autoComplete="one-time-code"
            placeholder="6-digit code"
            style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #d1d5db' }}
          />
        </label>
        {message && <div style={{ color: '#065f46' }}>{message}</div>}
        {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
        <button
          type="submit"
          disabled={loading || !email}
          style={{ padding: '0.9rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer' }}
        >
          {loading ? 'Verifying…' : 'Verify OTP'}
        </button>
      </form>
    </div>
  );
}
