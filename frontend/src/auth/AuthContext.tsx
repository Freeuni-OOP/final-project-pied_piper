import React from 'react';

export interface AuthUser {
	id: string;
	name?: string;
	email?: string;
}

export interface AuthContextValue {
	user: AuthUser | null;
	token: string | null;
	login: (token: string, user: AuthUser) => void;
	logout: () => void;
}

export const AuthContext = React.createContext<AuthContextValue | undefined>(undefined);
