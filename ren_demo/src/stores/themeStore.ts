// 定义允许的侧边栏主题类型
type SideTheme = "theme_blue" | "theme_black";

// 创建名为 'theme' 的 Pinia store
export const useThemeStore = defineStore("theme", () => {
	// --- 常量定义 ---
  // 所有可用的侧边栏主题选项
  const SIDE_THEME_OPTIONS: SideTheme[] = ["theme_blue", "theme_black"];
	// 默认主题
  const DEFAULT_THEME = "theme_black";
	// 默认是否显示动态标题
  const DEFAULT_DYNAMIC_TITLE = true;
	// 默认是否显示标签视图
  const DEFAULT_TAGS_VIEW = true;
  // 默认是否显示帮助按钮
  const DEFAULT_SHOW_HELP_BUTTON = true;
	// 默认是否显示设置面板
  const DEFAULT_SHOW_SETTINGS = false;

  // --- 辅助函数 ---
  // 验证并获取有效的侧边栏主题
  const getValidSideTheme = (value: any): SideTheme => {
    return SIDE_THEME_OPTIONS.includes(value) ? value : DEFAULT_THEME;
  };
	// 持久化，保存当前设置到 localStorage
  const saveSettings = () => {
    const settings = {
      theme: theme.value,
      isDynamicTitle: isDynamicTitle.value,
      tagsView: tagsView.value,
			showHelpButton: showHelpButton.value,
    };
    localStorage.setItem("layout-setting", JSON.stringify(settings));
  };

	// 从 localStorage 加载保存的布局设置
  // 如果不存在则返回 null（使用空字符串避免 JSON.parse 出错）
  const savedSettings = JSON.parse(localStorage.getItem("layout-setting") || "null");

  // --- 主题相关状态（使用 ref 创建响应式变量）---
  // 主色调（默认值：Element Plus 的蓝色）
	// ||（逻辑或）运算符：如果左侧的操作数为假值（falsy），则返回右侧的操作数。假值包括：false、0、''、null、undefined、NaN。
  // 侧边栏主题（默认暗色主题）
  const theme = ref<SideTheme>(getValidSideTheme(savedSettings?.theme));
  // 是否显示动态标题（默认 true）
	// ??（逻辑 null 合并运算符）：如果左侧的操作数为 null 或 undefined，则返回右侧的操作数。
  const isDynamicTitle = ref(savedSettings?.isDynamicTitle ?? DEFAULT_DYNAMIC_TITLE);
  // 是否显示标签视图（类似浏览器标签页，默认 false）
  const tagsView = ref(savedSettings?.tagsView ?? DEFAULT_TAGS_VIEW);
	// 是否显示帮助按钮（默认显示）
  const showHelpButton = ref(savedSettings?.showHelpButton ?? DEFAULT_SHOW_HELP_BUTTON);
	// 是否显示设置面板（默认不显示）（当前字段无需持久化，默认不显示）
  const showSettings = ref(DEFAULT_SHOW_SETTINGS);

	// --- 工具函数 ---
  // 修改设置项的统一方法
  const changeSetting = (key: string, value: any) => {
    // 创建一个包含所有状态引用的对象
    const stateMap: Record<string, any> = {
      theme,
      isDynamicTitle,
      showSettings,
      tagsView,
			showHelpButton,
    };

    // 检查 key 是否存在于状态映射中
    if (key in stateMap) {
      stateMap[key].value = value;  // 更新对应的响应式值

      // 排除临时状态 showSettings 的持久化
      if (key !== "showSettings") {
        saveSettings();  // 持久化到 localStorage
      }
    }
  };
	// 获取所有可用的侧边栏主题选项
  const getAvailableSideThemes = (): SideTheme[] => {
    return [...SIDE_THEME_OPTIONS];
  };

  // 暴露状态和方法给组件使用
  return {
		// 状态
    theme,
    isDynamicTitle,
    showSettings,
    tagsView,
		showHelpButton,


		// 方法
    changeSetting,
		getAvailableSideThemes
  };
});
