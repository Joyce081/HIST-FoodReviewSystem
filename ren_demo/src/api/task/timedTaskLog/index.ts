//获取定时任务列表
export const listPageTimedTaskLog = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/timedTask/log/list/page",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

//删除定时任务
export const deleteTimedTaskLog = (taskLogId: number) => {
	return service({
		method: "delete",
		url: "/timedTask/log/delete",
		params: { taskLogId }, // 通过 URL 参数传递（如 /timedTaskLog/delete?taskLogId=1）
	}).then((response) => response.data);
};
