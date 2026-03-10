//获取代码生成列表
export const listPageGen = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/gen/list/page",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//获取代码生成详情
export const getGenTable = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/gen/detail",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//获取代码生成列表
export const getGenListForDataBase = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/gen/list/dataBase/page",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//创建表
export const createTable = (params?: any) => {
	return service({
		method: "post",
		url: "/gen/create",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//添加代码生成
export const addGen = (params?: any) => {
	return service({
		method: "post",
		url: "/gen/add",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//修改代码生成
export const updateGenTable = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/gen/modify",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//删除代码生成
export const deleteGen = (tableIds: any) => {
	return service({
		method: "delete",
		url: "/gen/delete",
		params:  tableIds , // 通过 URL 参数传递（如 /generator/delete?tableId=1）
	}).then((response) => response.data);
};

//重新同步表结构
export const refreshGen = (tableId: any) => {
	return service({
		method: "post",
		url: "/gen/refresh",
		//由于只有一个参数，后台不想要单独为这一个参数再创建一个实体类，所以使用params方式传递
		//POST同样支持params方式传输
		//POST请求键值对形式传输，一种是直接使用params传输，一种是使用data传输，但是使用data传输时，需要在请求头中添加'Content-Type': 'application/x-www-form-urlencoded'
		params: tableId,
	}).then((response) => response.data);
};

//生成代码
export const genCode = (params?: any) => {
	return service({
		method: "get",
		url: "/gen/genCode",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};
