//获取字典类型列表（分页）
export const listPageDictType = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/dictType/list/page",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//获取字典类型列表（不分页）
export const listNotPageDictType = () => {
	return service({
		method: "get",
		url: `/dictType/list`,
	}).then((response) => response.data);
};

//添加字典类型
export const addDictType = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/dictType/add",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//修改字典类型
export const modifyDictType = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/dictType/modify",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//删除字典类型
export const deleteDictType = (dictTypeId: number) => {
	return service({
		method: "delete",
		url: "/dictType/delete",
		params: { dictTypeId }, // 通过 URL 参数传递（如 /dictType/delete?dictTypeId=1）
	}).then((response) => response.data);
};
