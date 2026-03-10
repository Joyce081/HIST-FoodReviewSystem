export interface User {
	userId: number;
	username: string;
	nickname: string;
	password: string;
	isDel: number;
	deptId: number;
	userType: string;
	email: string;
	phonenumber: string;
	sex: number;
	avatar: string;
	loginIp: string;
	loginDate: number;
	createBy: string;
	createTime: number;
	updateBy: string;
	updateTime: number;
	remark: string;
	dept: any;
}
