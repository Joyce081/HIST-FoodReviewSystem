import { useAuthStore } from "@/stores/authStore";
import eventBus from '@/events/eventBus';

export const tabComposable = {

	// 打开新标签页
	// 使用路径跳转、query传参（无需在路由配置中配置参数占位符）
	openPageQuery(pagePath: string,pageName: string,query?: any){
		// auth相关pinia
		const authStore = useAuthStore();

		//先添加标签
		authStore.addTag({
			tagId: pagePath,
			tagName: pageName,
			type: "primary",
			closable: true,
		});

		// 统一更新所有标签状态
		authStore.updateTagStates(pagePath);
		//跳转页面
		navigationService.pushPageQuery(pagePath,query);
	},

	// 打开新标签页
	// 使用路径跳转、query传参（无需在路由配置中配置参数占位符）
	openPageParams(pagePath: string,pageName: string,routeName: string,params?: any) {
		// auth相关pinia
		const authStore = useAuthStore();
		//先添加标签
		authStore.addTag({
			tagId: pagePath,
			tagName: pageName,
			type: "primary",
			closable: true,
		});

		// 统一更新所有标签状态
		authStore.updateTagStates(pagePath);
		//跳转页面
		navigationService.pushPageParams(routeName,params);
	},

	async closePage(options: ClosePageOptions = {}) {
		const authStore = useAuthStore();
    const { tagArr } = storeToRefs(authStore);
		const { targetPath, closePath, query } = options;
		// 确定要关闭的标签索引
		let closeIndex;

		// 1. 如果有指定要关闭的路径，则使用它
		if (closePath) {
			closeIndex = tagArr.value.findIndex(item => item.tagId === closePath);
		}
		// 2. 否则关闭当前激活标签
		else {
			closeIndex = tagArr.value.findIndex(item => item.type === "primary");
		}

		if (closeIndex === -1) {
			console.warn("未找到活动标签");
			return;
		}
		// 获取要关闭的标签
		const closingTag = tagArr.value[closeIndex];
		// 移除关闭标签（从数组中删除该元素）
		tagArr.value.splice(closeIndex, 1);

		// 关闭后自动激活相邻标签
		if (closingTag.type === "primary" && tagArr.value.length > 0) {
			let toPath : string = "";
			if(targetPath == null || targetPath == ""){
				const newIndex = Math.min(closeIndex, tagArr.value.length - 1);
				toPath = tagArr.value[newIndex].tagId;
			}else{
				toPath = targetPath;
			}
			await this.tagActive(toPath,query);
		}
		return closingTag; // 可选：返回被关闭的标签信息
	},

	//选中标签页
	async tagActive (path: string,query?: any){
		const authStore = useAuthStore();

		await navigationService.pushPageQuery(path,query);
		// 动态选中菜单
		await this.menuActive(path);
		authStore.updateTagStates(path);
	},

	// 选中菜单
	menuActive(path: string) {
		eventBus.emit('menu-set-active', { path: path });
	}
}

// 导出类型定义
export interface ClosePageOptions {
  targetPath?: string;
  closePath?: string;
  query?: any;
}
