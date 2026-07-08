import { Navigate, Route, Routes } from 'react-router-dom';
import HomePage from '../features/home/pages/HomePage';
import LoginPage from '../features/auth-pages/pages/LoginPage';
import RegisterPage from '../features/auth-pages/pages/RegisterPage';
import VerifyOtpPage from '../features/auth-pages/pages/VerifyOtpPage';
import LectureBrowsePage from '../features/lectures/pages/LectureBrowsePage';
import LectureDetailPage from '../features/lectures/pages/LectureDetailPage';
import ReviewPage from '../features/reviews/pages/ReviewPage';
import ProfilePage from '../features/profile/pages/ProfilePage';
import UserProfilePage from '../features/profile/pages/UserProfilePage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify" element={<VerifyOtpPage />} />
      <Route path="/lectures" element={<LectureBrowsePage />} />
      <Route path="/lectures/:lectureId" element={<LectureDetailPage />} />
      <Route path="/lectures/:lectureId/reviews" element={<ReviewPage />} />
      <Route path="/profile" element={<ProfilePage />} />
      <Route path="/profile/:userId" element={<UserProfilePage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
