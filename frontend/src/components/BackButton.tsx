import { useLocation, useNavigate } from 'react-router-dom';

interface BackButtonProps {
  /** Used only when there is no previous in-app page to return to. */
  fallbackTo?: string;
  label?: string;
}

export default function BackButton({ fallbackTo = '/', label = '← Back' }: BackButtonProps) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <button
      type="button"
      onClick={() => {
        // React Router sets key to "default" on the first entry in the stack.
        if (location.key !== 'default') {
          navigate(-1);
        } else {
          navigate(fallbackTo);
        }
      }}
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
