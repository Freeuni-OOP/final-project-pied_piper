import { Navigate, Route, Routes } from 'react-router-dom';
import type { ReactNode } from 'react';
import HomePage from '../features/home/pages/HomePage';
import LoginPage from '../features/auth-pages/pages/LoginPage';
import RegisterPage from '../features/auth-pages/pages/RegisterPage';
import VerifyOtpPage from '../features/auth-pages/pages/VerifyOtpPage';
import LectureBrowsePage from '../features/lectures/pages/LectureBrowsePage';
import LectureDetailPage from '../features/lectures/pages/LectureDetailPage';
import ReviewPage from '../features/reviews/pages/ReviewPage';
import ProfilePage from '../features/profile/pages/ProfilePage';
import UserProfilePage from '../features/profile/pages/UserProfilePage';
import UserReviewsPage from '../features/profile/pages/UserReviewsPage';
import UserLogsPage from '../features/profile/pages/UserLogsPage';
import UserNetworkPage from '../features/profile/pages/UserNetworkPage';
import ChatPage from '../features/chat/pages/ChatPage';
import ActivityFeedPage from '../features/feed/pages/ActivityFeedPage';
import SearchPage from '../features/search/pages/SearchPage';
import ProtectedRoute from '../auth/ProtectedRoute';

function Protect({ children }: { children: ReactNode }) {
  return <ProtectedRoute>{children}</ProtectedRoute>;
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify" element={<VerifyOtpPage />} />

      <Route path="/search" element={<Protect><SearchPage /></Protect>} />
      <Route path="/lectures" element={<Protect><LectureBrowsePage /></Protect>} />
      <Route path="/lectures/:lectureId" element={<Protect><LectureDetailPage /></Protect>} />
      <Route path="/lectures/:lectureId/reviews" element={<Protect><ReviewPage /></Protect>} />
      <Route path="/lectures/:lectureId/log" element={<Navigate to=".." relative="path" replace />} />

      <Route path="/profile" element={<Protect><ProfilePage /></Protect>} />
      <Route path="/profile/:userId" element={<Protect><UserProfilePage /></Protect>} />
      <Route path="/profile/:userId/reviews" element={<Protect><UserReviewsPage /></Protect>} />
      <Route path="/profile/:userId/logs" element={<Protect><UserLogsPage /></Protect>} />
      <Route path="/profile/:userId/followers" element={<Protect><UserNetworkPage mode="followers" /></Protect>} />
      <Route path="/profile/:userId/following" element={<Protect><UserNetworkPage mode="following" /></Protect>} />

      <Route path="/chat" element={<Protect><ChatPage /></Protect>} />
      <Route path="/feed" element={<Protect><ActivityFeedPage /></Protect>} />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
