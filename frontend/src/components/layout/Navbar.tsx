// Top navigation bar with links to browse, feed, chat, profile, and auth actions.

import { Link, useNavigate } from 'react-router-dom';
import useAuth from '../../auth/useAuth';

export default function Navbar() {
  const auth = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    auth.logout();
    navigate('/');
  };

  return (
    <nav className="bg-white shadow-md border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <div className="flex items-center gap-6">
          <Link to="/" className="text-2xl font-bold text-blue-600">
            LecturBoxd
          </Link>
          <div className="flex gap-4">
            <Link to="/lectures" className="text-gray-700 hover:text-blue-600 transition-colors">
              Browse Lectures
            </Link>
            {auth.isAuthenticated && (
              <>
                <Link to="/feed" className="text-gray-700 hover:text-blue-600 transition-colors">
                  Feed
                </Link>
                <Link to="/chat" className="text-gray-700 hover:text-blue-600 transition-colors">
                  Messages
                </Link>
              </>
            )}
          </div>
        </div>

        <div className="flex items-center gap-4">
          {auth.isAuthenticated ? (
            <>
              <Link
                to="/profile"
                className="px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
              >
                {auth.user?.name || 'Profile'}
              </Link>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

