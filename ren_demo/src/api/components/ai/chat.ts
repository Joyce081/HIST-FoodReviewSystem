export const listConversation = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/main/ai/list",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

// 获取对话消息列表
export const listConversationMessages = (conversationId: string) => {
	return service({
		method: "get",
		url: "/main/ai/listMessages",
		params: { conversationId }, // 通过 URL 参数传递（如 /post/delete?postId=1）
	}).then((response) => response.data);
};

export const renameConversation = (params?: Record<string, any>) => {
	return service({
		method: "get",
		url: "/main/ai/renameConversation",
		params: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};

export const deleteConversation = (conversationId: number) => {
	return service({
		method: "delete",
		url: "/main/ai/delete",
		params: { conversationId }, // 通过 URL 参数传递（如 /post/delete?postId=1）
	}).then((response) => response.data);
};

export const addConversationMessage = (params?: Record<string, any>) => {
	return service({
		method: "post",
		url: "/main/ai/addConversationMessage",
		data: params || {}, // 无参数时传空对象
	}).then((response) => response.data);
};