//修改密码
export const updatePassword = (data: {
	oldPassword: string;
	newPassword: string;
	confirmPassword: string;
}) => {
	return service({
		method: "put",
		url: "/user/profile/updatePwd",
		data,
	}).then((response) => response.data);
};

//修改用户信息
export const updateUserProfile = (data: {
	nickname: string;
	phonenumber: string;
	email: string;
	sex: number;
}) => {
	return service({
		method: "put",
		url: "/user/profile/update",
		data,
	}).then((response) => response.data);
};

//上传头像
export const uploadAvatar = (data: FormData) => {
	return service({
		method: "post",
		url: "/localStorage/upload",
		data,
	}).then((response) => response.data);
};

//更新用户头像
export const updateUserAvatar = (avatarUrl: string) => {
	return service({
		method: "put",
		url: "/user/profile/updateAvatar",
		data: { avatar: avatarUrl },
	}).then((response) => response.data);
};
