import type { User } from "./User";

export interface LoginUser {
	userId: number;
	expireTime: number;
	refreshTokenExpireTime: number;
	loginTime: number;
	ipaddr: string;
	loginLocation: string;
	browser: string;
	os: string;
	permissions: string[];
	user: User;
}
