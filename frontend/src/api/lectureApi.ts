import client from './axiosClient';

export interface Lecture {
	id: number;
	title: string;
	description?: string;
	faculty?: string;
}

export async function getLectures(): Promise<Lecture[]> {
	const res = await client.get('/api/lectures');
	return res.data;
}

export async function getLecture(id: number): Promise<Lecture> {
	const res = await client.get(`/api/lectures/${id}`);
	return res.data;
}
