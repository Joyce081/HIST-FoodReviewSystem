import type { LoginUser } from "@/types/LoginUser";
import type { LoginResponse } from "@/types/LoginResponse";
import type { DynamicRoute } from "@/types/DynamicRoute";
import type { Menu } from "@/types/Menu";
import router from "@/router";

export const useAuthStore = defineStore("auth", () => {
	// 状态声明
	// 短期token
	const accessToken = ref("");
	// 是否登录
	const isLogin = ref(false);
	// 登录用户信息
	const loginUser = ref<LoginUser | null>(null);
	// 是否超级管理员
	const isAdmin = ref<boolean>(false);
	// 用户角色
	const roleNames = ref<string[]>([]);
	// 用户角色Id
	const roleIds = ref<number[]>([]);
	// 用户岗位
	const postNames = ref<string[]>([]);
	// 用户岗位Id
	const postIds = ref<number[]>([]);
	// 按钮权限
	const permissions = ref<string[]>([]);
	// 侧边栏菜单
	const menus = ref<Menu[]>([]);
	// 路由配置
	const dynamicRoutes = ref<DynamicRoute[]>([]);

	//获取accessToken（该方法主要给Swagger使用）
	const getAccessToken = async (): Promise<string> => {
		//判断accessToken是否存在
		if (accessToken.value) {
			//调用后台获取accessToken的接口（后台判断是否需要刷新，如果需要刷新，则返回新的accessToken，如果不需要刷新，则返回原accessToken），如果获取失败（表示原accessToken失效），则调用刷新token的接口
			await service({
				method: "post",
				url: "/auth/getAccessToken",
				headers: {
					"x-access-token": accessToken.value,
				},
			}).then(async (response) => {
				if(response.data.code == 200){
					accessToken.value = response.data.accessToken;
				}else{
					await refreshToken();
				}
			});
		}else{
			//accessToken不存在，直接调用刷新token的接口
			await refreshToken();
		}
		//返回accessToken
		return accessToken.value;
	};

	//刷新token
	//const refreshToken = async (): Promise<boolean> =>{}其实就等于const refreshToken = async () =>{}
	//只要是async方法，返回一定是一个Promise<?>类型，显示声明Promise<boolean>只是为了保证代码规范性，防止内部返回错类型，在功能上并没有差别
	//<?>表示任意类型，该方法内部最后的返回值类型一定要是?
	//再简单一点，如果这个方法我们外部用得到他的返回值，我们就明确声明Promise<?>防止内部返回值写错
	//如果外部用不到返回值，或者这个方法本身就没有返回值，完全可以不用声明返回值类型
	const refreshToken = async (): Promise<boolean> => {
		//从localStorage中获取到refreshToken
		const refreshToken = localStorage.getItem("refreshToken");
		//如果没有获取到，则报错
		if (!refreshToken) {
			clearAuth();
			throw new Error("无有效 RefreshToken");
		}
		try {
			//向refreshToken发送请求，要求返回值是RefreshTokenResponse类型
			const response = await service.post(
				"/auth/refreshToken",
				{},
				{ headers: { "X-Refresh-Token": refreshToken } }
			);
			//从响应头中获取到新的accessToken
			const newAccessToken = response.data.accessToken;
			//如果不存在，则抛错
			if (!newAccessToken) throw new Error("无效的令牌响应");

			//如果存在，则将新的accessToken存入Pinia
			accessToken.value = newAccessToken;
			//如果refreshToken存在，将新的refreshToken存入localStorage
			if (response.data.refreshToken) {
				localStorage.setItem("refreshToken", response.data.refreshToken);
			}
			//在async方法内部，只要执行return，就等于调用了Promise的resolve(true)方法
			return true;
		} catch {
			clearAuth();
			//在async方法内部，只要执行throw，就等于调用了Promise的reject(err)方法
			throw new Error("令牌刷新失败");
		}
	};

	// 登录方法
	const login = async (username: string, password: string): Promise<LoginResponse> => {
		//向/login接口发送请求，要求返回体是LoginResponse类型
		const response = await service.post<LoginResponse>("/auth/login", { username, password });
		//如果请求成功
		if (response.data.code === 200) {
			//将accessToken存入Pinia
			accessToken.value = response.data.accessToken;
			//如果refreshToken存在，将refreshToken存入localStorage
			if (response.data.refreshToken) {
				localStorage.setItem("refreshToken", response.data.refreshToken);
			}
			//是否已经登陆设置为是
			isLogin.value = true;
		}
		return response.data;
	};

	// 自动登录方法
	const autoLogin = async () => {
		const response = await service.post("/auth/auto/login");
		//如果请求成功
		if (response.data.code === 200) {
			//是否已经登陆设置为是
			isLogin.value = true;
		}
		return response.data;
	};

	//获取用户信息接口
	const getUserInfo = async (): Promise<void> => {
		try {
			const res = await service.get("/user/info");
			//该接口包含自动登录功能，所以是否已经登陆设置为是
			isLogin.value = true;
			//以下设置用户相关信息
			loginUser.value = res.data.loginUser;
			isAdmin.value = res.data.isAdmin;
			roleNames.value = res.data.roleNames;
			roleIds.value = res.data.roleIds;
			postNames.value = res.data.postNames;
			postIds.value = res.data.postIds;
			permissions.value = res.data.permissions;
			menus.value = res.data.menus;
			dynamicRoutes.value = res.data.dynamicRoutes;
		} catch (err: unknown) {
			clearAuth();
			throw err; // 抛出错误让路由守卫处理
		}
	};

	//用户登出
	const logout = async (): Promise<void> => {
		await service.post("/auth/logout");
		clearAuth();
	};

	//清除登录信息
	const clearAuth = (): void => {
		loginUser.value = null;
		isAdmin.value = false;
		roleNames.value = [];
		roleIds.value = [];
		postNames.value = [];
		postIds.value = [];
		menus.value = [];
		permissions.value = [];
		dynamicRoutes.value = [];
		accessToken.value = "";
		isLogin.value = false;
		tagArr.value = [{
			tagId: "/index",
			tagName: "首页",
			type: "primary",
			closable: false
		}];
		localStorage.removeItem("refreshToken");
		localStorage.removeItem("tagArr");
		router.replace("/login");
	};

	/** 标签相关 */
	// 定义合法类型
	type TagType = "primary" | "success" | "warning" | "danger" | "info";
	interface TagItem {
		tagId: string;
		tagName: string;
		type: TagType;
		closable: boolean;
	}
	const tagArr = ref<TagItem[]>([
		{ tagId: "/index", tagName: "首页", type: "primary", closable: false },
	]);
	// 标签操作方法
	const addTag = (newTag: TagItem) => {
		//判断当前需要添加的标签是否已经存在
		const existingTag = tagArr.value.find(t => t.tagId === newTag.tagId);
		//不存在，则添加
		if (!existingTag) {
			tagArr.value.push(newTag);
		}
	}
	// 修改标签状态
	const updateTagStates = (activeTagId: string) => {
		tagArr.value.forEach((tag) => {
			tag.type = tag.tagId === activeTagId ? "primary" : "info";
			tag.closable = tag.tagId !== "/index"; // 首页始终不可关闭
		});
	};

	/** 返回状态管理参数以及方法 */
	return {
		// 状态
		accessToken,
		isLogin,
		loginUser,
		isAdmin,
		roleNames,
		roleIds,
		postNames,
		postIds,
		menus,
		permissions,
		dynamicRoutes,
		tagArr,

		// 方法
		login,
		refreshToken,
		getUserInfo,
		logout,
		clearAuth,
		autoLogin,
		getAccessToken,
		addTag,
		updateTagStates,
	};
});
