import { Link } from 'react-router-dom';
import AppRoutes from './routes/AppRoutes';
import useAuth from './auth/useAuth';

function App() {
  const auth = useAuth();

  return (
    <div className="app-shell" style={{ fontFamily: 'sans-serif', minHeight: '100vh', background: '#f7f8fb' }}>
      <header style={{ padding: '1rem 2rem', background: '#fff', borderBottom: '1px solid #e5e7eb', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Link to="/" style={{ marginRight: 16, fontWeight: 700, textDecoration: 'none', color: '#111' }}>
            LecturBoxd
          </Link>
          {auth.token && (
            <>
              <Link to="/lectures" style={{ marginRight: 16, textDecoration: 'none', color: '#374151' }}>
                Lectures
              </Link>
              <Link to="/search" style={{ marginRight: 16, textDecoration: 'none', color: '#374151' }}>
                Search
              </Link>
              <Link to="/feed" style={{ marginRight: 16, textDecoration: 'none', color: '#374151' }}>
                Feed
              </Link>
              <Link to="/chat" style={{ marginRight: 16, textDecoration: 'none', color: '#374151' }}>
                Chat
              </Link>
              <Link to="/profile" style={{ textDecoration: 'none', color: '#374151' }}>
                Profile
              </Link>
            </>
          )}
        </div>
        <nav>
          {auth.token ? (
            <button onClick={auth.logout} style={{ padding: '0.5rem 1rem', borderRadius: 6, border: '1px solid #d1d5db', background: '#fff', cursor: 'pointer' }}>
              Logout
            </button>
          ) : (
            <>
              <Link to="/login" style={{ marginRight: 16, textDecoration: 'none', color: '#374151' }}>
                Login
              </Link>
              <Link to="/register" style={{ textDecoration: 'none', color: '#374151' }}>
                Register
              </Link>
            </>
          )}
        </nav>
      </header>
      <main style={{ padding: '2rem' }}>
        <AppRoutes />
      </main>
    </div>
  );
}

export default App;
