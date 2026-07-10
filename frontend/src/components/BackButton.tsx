import { useNavigate } from 'react-router-dom';

interface BackButtonProps {
  to?: string;
  label?: string;
}

export default function BackButton({ to, label = '← Back' }: BackButtonProps) {
  const navigate = useNavigate();

  return (
    <button
      type="button"
      onClick={() => (to ? navigate(to) : navigate(-1))}
      style={{
        marginBottom: 16,
        padding: '0.45rem 0.9rem',
        borderRadius: 8,
        border: '1px solid #d1d5db',
        background: '#fff',
        color: '#374151',
        cursor: 'pointer',
      }}
    >
      {label}
    </button>
  );
}
