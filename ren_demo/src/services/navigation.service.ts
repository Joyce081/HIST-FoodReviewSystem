import router from "@/router";

export class NavigationService {
	// 直接跳转页面（不保留当前路由信息，不能返回上一级）(传参使用query形式，路由无需配置占位符)
	// 使用路径跳转、query传参（无需在路由配置中配置参数占位符）
	replacePageQuery = async (pagePath: string,query?: any) => {
		await router.replace({ path: pagePath,query });
	}

	// 直接跳转页面（保留当前路由信息，可以返回上一级）
	// 使用路径跳转、query传参（无需在路由配置中配置参数占位符）
	pushPageQuery = async (pagePath: string,query?: any) => {
		await router.push({path: pagePath,query});
	}

	// 直接跳转页面（不保留当前路由信息，不能返回上一级）
	// 使用路由名称跳转、params传参（需要在路由配置中配置参数占位符）
	replacePageParams = async (routeName: string,params?: any) => {
		await router.replace({ name: routeName,params });
	}

	// 直接跳转页面（保留当前路由信息，可以返回上一级）
	// 使用路由名称跳转、params传参（需要在路由配置中配置参数占位符）
	pushPageParams = async (routeName: string,params?: any) => {
		await router.push({name: routeName,params});
	}
}

// 导出具名实例
export const navigationService = new NavigationService();
