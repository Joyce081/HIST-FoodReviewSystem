<template>
	<div class="container">
		<el-container>
			<!-- 侧边栏 -->
			<el-aside class="aside" :width="isCollapse ? '64px' : '245px'">
				<div class="aside-header"></div>
				<el-menu
					:router="false"
					background-color="transparent"
					:collapse="isCollapse"
					class="custom-menu"
					:unique-opened="true"
					@select="tagAdd"
					ref="menuRef"
				>
					<!-- 遍历菜单项 -->
					<SidebarItem
						v-for="item in menuItems"
						:key="item.index"
						:item="item"
					></SidebarItem>
				</el-menu>
				<img
					class="sidebar-bg"
					:src="themeVariables.sidebarBottomPic || '/src/assets/images/sidebar/bg.png'"
					v-show="themeVariables.isShowSidebarBottomPic || true"
					:class="{'sidebar-bg-visible': !isCollapse && themeSetting.theme.value == 'theme_blue'}"
				/>
			</el-aside>

			<el-container>
				<!-- 头部区域 -->
				<el-header class="container-header">
					<div class="header-one">
						<div class="one-left">
							<div class="iconClass-collapse">
								<el-button link @click="collapseFun">
									<el-icon :size="30" v-if="!isCollapse"><i-ep-fold /></el-icon>
									<el-icon :size="30" v-if="isCollapse"><i-ep-expand /></el-icon>
								</el-button>
							</div>
						</div>
						<div class="one-right">
							<el-dropdown @command="userInfoCommand" popper-class="custom-dropdown-menu">
								<!-- 触发元素 -->
								<el-icon :size="25" class="iconClass-setting">
									<i-ep-setting />
								</el-icon>
								<!-- 下拉菜单（Vue 3 插槽语法） -->
								<template #dropdown>
									<el-dropdown-menu>
										<el-dropdown-item command="userProfile">个人中心</el-dropdown-item>
										<el-dropdown-item command="themeSetting">主题设置</el-dropdown-item>
										<el-dropdown-item command="loginOut">退出登录</el-dropdown-item>
									</el-dropdown-menu>
								</template>
							</el-dropdown>
						</div>
					</div>
					<div class="header-two" v-show="themeSetting.tagsView.value">
						<el-tag
							:closable="item.closable"
							:type="item.type"
							v-for="(item, index) in tagArr"
							:key="item.tagId"
							@click="tagClick(index)"
							@close="tagClose(index)"
							>{{ item.tagName }}</el-tag
						>
					</div>
				</el-header>

				<!-- 内容区域 -->
				<el-main class="container-main">
					<RouterView></RouterView>
				</el-main>

				<!-- 主题设置 -->
				<el-drawer
					v-model="themeSetting.showSettings.value"
					title="主题设置"
					direction="rtl"
					size="300px"
					class="theme-setting-drawer"
				>
					<div class="setting-container">
						<!-- 主题选择 -->
						<div class="setting-title">主题风格</div>
						<div
							v-for="theme in themeStore.getAvailableSideThemes()"
							:key="theme"
							class="theme-option"
							:class="{ 'theme-active': themeSetting.theme.value === theme }"
							@click="themeStore.changeSetting('theme', theme)"
						>
							<div class="theme-name">
								{{ theme === 'theme_blue' ? '科技蓝白' : '酷雅暗黑' }}
							</div>
						</div>

						<!-- 动态标题开关 -->
						<div class="setting-item">
							<span class="setting-label">动态标题</span>
							<el-switch
								v-model="themeSetting.isDynamicTitle.value"
								@change="value => themeStore.changeSetting('isDynamicTitle', value)"
							/>
						</div>

						<!-- 标签视图开关 -->
						<div class="setting-item">
							<span class="setting-label">标签视图</span>
							<el-switch
								v-model="themeSetting.tagsView.value"
								@change="value => themeStore.changeSetting('tagsView', value)"
							/>
						</div>

						<!-- 帮助按钮开关 -->
						<div class="setting-item">
							<span class="setting-label">帮助按钮</span>
							<el-switch
								v-model="themeSetting.showHelpButton.value"
								@change="value => themeStore.changeSetting('showHelpButton', value)"
							/>
						</div>
					</div>
				</el-drawer>

				<!-- AI相关 -->
				<img src="@/assets/images/ai/help.png" class="ai-help" @click="openHelpDialog" v-show="themeSetting.showHelpButton.value && dialogFormHelp === false"/>
				<el-dialog title="帮助" v-model="dialogFormHelp" :width="dialogWidth">
					<Chat></Chat>
				</el-dialog>
			</el-container>
		</el-container>
	</div>
</template>

<script setup lang="ts" name="home">
import { useAuthStore } from "@/stores/authStore";
import router from "@/router";
import type { Menu } from "@/types/Menu";
// 该组件是递归组件，所以无法自动导入，需要手动导入
import SidebarItem from "@/components/SidebarItem/Index.vue";
import eventBus from '@/events/eventBus';
import { useTheme } from '@/composables/useTheme';
import '@/assets/css/layout/mainLayout.scss';

/*============================通用参数开始============================*/
// auth相关pinia
const authStore = useAuthStore();
//路由对象，用于获取当前路由信息（参数/查询字符串）等
const route = useRoute();
// 菜单栏
const menuRef = ref();
// 当前主题、主题变量
const { themeStore, themeSetting, themeVariables } = useTheme();
/*============================通用参数结束============================*/

/*============================菜单栏相关开始============================*/
// 菜单是否折叠
const isCollapse = ref(false);
//修改菜单栏是否折叠
const collapseFun = () => {
	isCollapse.value = !isCollapse.value;
};
const menuItems = ref<Menu[]>([]);
//选中菜单
const menuActive = (path: string) => {
	menuRef.value.updateActiveIndex(path);
}
/*============================菜单栏相关结束============================*/

/*============================标签页开始============================*/
// 定义合法类型
const {tagArr} = storeToRefs(authStore);
//标签页添加方法
const tagAdd = (index: string) => {
	// 获取当前菜单项配置
	const findMenuItem = (items: any[]): any => {
		for (const item of items) {
			if (item.index === index) return item;
			if (item.children) {
				const found = findMenuItem(item.children);
				if (found) return found;
			}
		}
	};
	//当前选中的菜单项
	const currentItem = findMenuItem(menuItems.value) || { index, name: "未知菜单" };

	//打开标签页
	tabComposable.openPageQuery(currentItem.index, currentItem.name,route.query);
};

//标签页点击方法
const tagClick = (index: number) => {
	tabComposable.tagActive(tagArr.value[index].tagId);
};

//标签页关闭方法
const tagClose = async (index: number) => {
	tabComposable.closePage({
		closePath: tagArr.value[index].tagId,
	});
};
/*============================标签页结束============================*/

/*============================个人中心相关开始============================*/
const userInfoCommand = (command: string) => {
	switch (command) {
		case "loginOut":
			authStore.logout();
			break;
		case "themeSetting":
			themeStore.changeSetting('showSettings', true);
			break;
		case "userProfile":
			//打开标签页
			tabComposable.openPageQuery("/system/user/profile", "个人中心");
			break;
	}
};
/*============================个人中心相关结束============================*/

/*============================监视器相关开始============================*/
// 监听 tagArr 变化并持久化
watch(
	() => tagArr,
	(newVal) => {
		localStorage.setItem("tagArr", JSON.stringify(newVal.value));
	},
	{ deep: true } // 深度监听数组内部对象变化
);
// 判断是否开启ElementPlus的暗黑模式
watch(() => themeVariables.value.isOpenElementPlusDarkMode, (newVal) => {
	if (newVal) {
		// 开启暗黑模式
		document.documentElement.classList.add('dark')
	} else {
		// 关闭暗黑模式
		document.documentElement.classList.remove('dark')
	}
}, { immediate: true });
//监听是否显示标题的变化
watch(() => themeSetting.isDynamicTitle.value, (newVal) => {
	// 动态标题变化时，更新文档标题
	document.title = newVal ? (route.meta?.title as string) + ' - 任后台管理系统' : '任后台管理系统';
}, { immediate: true });
/*============================监视器相关结束============================*/

/*============================帮助相关开始============================*/
const dialogFormHelp = ref(false);
//打开弹框
const openHelpDialog = () => {
	dialogFormHelp.value = true;
};
// 创建响应式数据来存储窗口尺寸
const windowWidth = ref(window.innerWidth)
// 用于更新窗口宽度的函数
const updateWindowWidth = () => {
  windowWidth.value = window.innerWidth
}
// 定义计算属性，计算对话框宽度（浏览器宽度 - 300px）
const dialogWidth = computed(() => {
  return `${windowWidth.value - 200}px`
})
/*============================帮助相关结束============================*/

/*============================生命周期钩子开始============================*/
onMounted(async () => {
	// 先等待路由加载完成
	await router.isReady();
	console.info("获取当前路由表信息", router.getRoutes());
	const { menus } = storeToRefs(authStore);
	menuItems.value = [{ index: "/index", name: "首页", icon: "i-ep-house" }];
	menuItems.value.push(...menus.value);
	// 从 localStorage 加载标签页数据
	const savedTags = localStorage.getItem("tagArr");
	if (savedTags) {
		tagArr.value = JSON.parse(savedTags);
	}
	//动态选中菜单
	menuActive(route.meta.menuShow?.toString() || "");
	// 监听窗口 resize 事件
	window.addEventListener('resize', updateWindowWidth);
});
/*============================生命周期钩子结束============================*/

/*============================事件总线开始============================*/
// 监听设置活动菜单项事件
eventBus.on('menu-set-active', ({ path }) => {
	//确保DOM挂载完成后再执行
	nextTick(() => {
    menuActive(path);
  });
});
/*============================事件总线结束============================*/
</script>
