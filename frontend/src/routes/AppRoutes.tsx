import { Navigate, Route, Routes } from 'react-router-dom';
import HomePage from '../features/home/pages/HomePage';
import LoginPage from '../features/auth-pages/pages/LoginPage';
import RegisterPage from '../features/auth-pages/pages/RegisterPage';
import VerifyOtpPage from '../features/auth-pages/pages/VerifyOtpPage';
import LectureBrowsePage from '../features/lectures/pages/LectureBrowsePage';
import LectureDetailPage from '../features/lectures/pages/LectureDetailPage';
import ReviewPage from '../features/reviews/pages/ReviewPage';
import LogLecturePage from '../features/reviews/pages/LogLecturePage';
import ProfilePage from '../features/profile/pages/ProfilePage';
import UserProfilePage from '../features/profile/pages/UserProfilePage';
import UserReviewsPage from '../features/profile/pages/UserReviewsPage';
import UserLogsPage from '../features/profile/pages/UserLogsPage';
import UserNetworkPage from '../features/profile/pages/UserNetworkPage';
import ChatPage from '../features/chat/pages/ChatPage';
import ActivityFeedPage from '../features/feed/pages/ActivityFeedPage';
import SearchPage from '../features/search/pages/SearchPage';
import ProtectedRoute from '../auth/ProtectedRoute';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify" element={<VerifyOtpPage />} />
      <Route path="/search" element={<SearchPage />} />
      <Route path="/lectures" element={<LectureBrowsePage />} />
      <Route path="/lectures/:lectureId" element={<LectureDetailPage />} />
      <Route path="/lectures/:lectureId/reviews" element={<ReviewPage />} />
      <Route
        path="/lectures/:lectureId/log"
        element={
          <ProtectedRoute>
            <LogLecturePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />
      <Route path="/profile/:userId" element={<UserProfilePage />} />
      <Route path="/profile/:userId/reviews" element={<UserReviewsPage />} />
      <Route path="/profile/:userId/logs" element={<UserLogsPage />} />
      <Route path="/profile/:userId/followers" element={<UserNetworkPage mode="followers" />} />
      <Route path="/profile/:userId/following" element={<UserNetworkPage mode="following" />} />
      <Route
        path="/chat"
        element={
          <ProtectedRoute>
            <ChatPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/feed"
        element={
          <ProtectedRoute>
            <ActivityFeedPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
