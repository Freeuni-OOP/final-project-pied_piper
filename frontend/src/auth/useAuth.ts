// Custom hook for consuming authentication context and guarding against usage outside AuthProvider.
import { useContext } from 'react';
import { AuthContext } from './AuthContext';

export default function useAuth() {
	const ctx = useContext(AuthContext);
	if (!ctx) throw new Error('useAuth must be used within AuthProvider');

	return {
		...ctx,
		userId: ctx.user?.id,
		isAuthenticated: !!ctx.token,
	};
}

