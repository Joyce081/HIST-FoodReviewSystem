//获取菜单列表
export const listTreeMenu = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/menu/tree",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//添加菜单
export const addMenu = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/menu/add",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//修改菜单
export const modifyMenu = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/menu/modify",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//删除菜单
export const deleteMenu = (menuId: number) => {
	return service({
		method: "delete",
		url: "/menu/delete",
		params: { menuId }, // 通过 URL 参数传递（如 /menu/delete?menuId=1）
	}).then((response) => response.data);
};

//获取排除本菜单的菜单树列表（下拉列表使用）
export const getDoesNotContainSelfMenuTreeSelect = (menuId: number = -1) => {
	return service({
		method: "get",
		url: `/menu/tree/select/doesNotContainSelfMenu/${menuId}`,
	}).then((response) => response.data);
};

//获取角色菜单树列表（下拉列表使用）
export const listTreeSelectRoleMenu = (roleId: number = -1) => {
	return service({
		method: "get",
		url: `/menu/tree/select/role/${roleId}`,
	}).then((response) => response.data);
};

//获取菜单树列表（下拉列表使用）
export const listTreeSelectMenu = () => {
	return service({
		method: "get",
		url: `/menu/tree/select`,
	}).then((response) => response.data);
};
