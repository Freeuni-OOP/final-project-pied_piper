// HTTP client functions for user profiles, follow/unfollow, and follower listings.
import client from './axiosClient';

export interface PublicUser {
	id: string;
	name: string;
	email: string;
}

export interface UserProfile {
	id: string;
	name: string;
	email: string;
	reviewCount?: number;
	lectureLogsCount?: number;
	followersCount?: number;
	followingCount?: number;
}

export interface FollowStatus {
	userId: string;
	isFollowing: boolean;
}

export async function getUserProfile(userId: string): Promise<UserProfile> {
	const res = await client.get(`/api/users/${userId}`);
	return res.data;
}

export async function followUser(userId: string): Promise<FollowStatus> {
	const res = await client.post(`/api/users/${userId}/follow`);
	return res.data;
}

export async function unfollowUser(userId: string): Promise<FollowStatus> {
	const res = await client.delete(`/api/users/${userId}/follow`);
	return res.data;
}

export async function getFollowStatus(userId: string): Promise<FollowStatus> {
	const res = await client.get(`/api/users/${userId}/follow-status`);
	return res.data;
}

export async function getFollowers(userId: string): Promise<PublicUser[]> {
	const res = await client.get(`/api/users/${userId}/followers`);
	return res.data;
}

export async function getFollowing(userId: string): Promise<PublicUser[]> {
	const res = await client.get(`/api/users/${userId}/following`);
	return res.data;
}


