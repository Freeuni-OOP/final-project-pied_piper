import axios from 'axios';
import { getToken, clearToken } from '../auth/tokenStorage';

const BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || '';

const client = axios.create({
	baseURL: BASE_URL,
	headers: { 'Content-Type': 'application/json' },
	timeout: 15000,
});

client.interceptors.request.use((config) => {
	const token = getToken();
	if (token && config.headers) {
		config.headers.Authorization = `Bearer ${token}`;
	}
	return config;
});

client.interceptors.response.use(
	(res) => res,
	(err) => {
		// normalize error shape
		if (err.response) {
			const { status, data } = err.response;
			// auto-logout on 401
			if (status === 401) {
				try {
					clearToken();
				} catch (e) {}
			}
			return Promise.reject({ status, data, message: data?.message || err.message });
		}
		return Promise.reject({ message: err.message });
	}
);

export default client;
