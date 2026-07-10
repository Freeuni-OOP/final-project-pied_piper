// HTTP client functions for lecture browse, search, and detail endpoints.
import client from './axiosClient';

export interface Lecture {
	id: number;
	title: string;
	description?: string;
	faculty?: string;
	week?: number;
	lectureNumber?: number;
	type?: string;
}

export interface Faculty {
	id: number;
	name: string;
}

export interface Semester {
	id: number;
	name: string;
}

export interface Subject {
	id: number;
	name: string;
	faculty?: string;
}

export interface SubjectSyllabus {
	id: number;
	name: string;
	lectures: Lecture[];
}

// Syllabus browsing endpoints
export async function listFaculties(): Promise<Faculty[]> {
	const res = await client.get('/api/syllabus/faculties');
	return res.data;
}

export async function listSemesters(facultyId: number): Promise<Semester[]> {
	const res = await client.get(`/api/syllabus/faculties/${facultyId}/semesters`);
	return res.data;
}

export async function listSubjects(semesterId: number): Promise<Subject[]> {
	const res = await client.get(`/api/syllabus/semesters/${semesterId}/subjects`);
	return res.data;
}

export async function getSubjectSyllabus(subjectId: number): Promise<SubjectSyllabus> {
	const res = await client.get(`/api/syllabus/subjects/${subjectId}`);
	return res.data;
}

// Individual lecture endpoint (if still used)
export async function getLecture(id: number): Promise<Lecture> {
	const res = await client.get(`/api/lectures/${id}`);
	return res.data;
}


