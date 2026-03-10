import { computed, watchEffect } from 'vue'
import { useThemeStore } from '@/stores/themeStore'
import type { ButtonType } from 'element-plus'

export function useTheme() {
  const themeStore = useThemeStore()
  const themeSetting = storeToRefs(themeStore)

  const themeConfig = {
    // 科技蓝白
    theme_blue: {
      /*====================================================CSS 变量使用 -- 前缀，方便CSS直接绑定=================================================*/
			//=====================布局主页====================
      // 侧边栏最底部背景色
			'--aside-bg-color': '#011F4A',
      // 侧边栏菜单字体颜色
			'--menu-color': '#ffffff',
      // 侧边栏菜单激活背景色
			'--menu-active-bg-color': 'linear-gradient(-90deg, #06396e, #022953)',
      // 侧边栏菜单激活字体颜色
			'--menu-active-color': '#1890ff',
      // 侧边栏菜单悬停背景色
			'--menu-hover-bg-color': 'rgba(24, 144, 255, 0.08)',
      // 侧边栏菜单激活时，添加的一个前置区域背景色
			'--menu-active-before-bg-color': 'rgba(24, 144, 255, 0.2)',
      // 一号头部栏背景色
			'--header-one-bg-color': '#ffffff',
			// 一号头部栏下边框颜色
			'--header-one-border-color': '#e8e8e8',
      // 一号头部栏阴影
			'--header-one-box-shadow-color': 'rgba(0, 0, 0, 0.05)',
      // 一号头部栏内的侧边栏折叠图标背景色
			'--icon-collapse-bg-color': '#f5f7fa',
      // 一号头部栏内的设置图标颜色
			'--icon-setting-color': '#606266',
      // 一号头部栏内的设置图标悬停颜色
			'--icon-setting-hover-color': '#409eff',
      // 二号头部栏背景色
			'--header-two-bg-color': '#f8f9fa',
			// 二号头部栏下边框颜色
			'--header-two-border-color': '#e8e8e8',
      // 二号头部栏标签已激活背景色
			'--tag-primary-bg-color': '#ecf5ff',
      // 二号头部栏标签已激活字体颜色
			'--tag-primary-color': '#409eff',
      // 二号头部栏标签未激活背景色
			'--tag-other-bg-color': '#f4f4f5',
      // 二号头部栏标签未激活字体颜色
			'--tag-other-color': '#909399',
			// 主题设置字体颜色
			'--theme-setting-color': '#606266',
			// 主题设置标题下边框颜色
			'--theme-setting-title-border-color': '#ebeef5',
			// 主题设置选择主题hover背景色
			'--theme-setting-option-hover-bg-color': '#f5f7fa',
			// 主题设置选中主题字体标签颜色
			'--theme-setting-active-color': '#409eff',
			//=====================全局css修改====================
      // 搜索表单背景色
			'--search-form-bg-color': '#edf4fa',
      // 搜索表单边框颜色
			'--search-form-border-color': '#e6e6e6',
      // 数据表格表头背景色
			'--data-table-header-bg-color': '#edf4fa',
      // 数据表格表头字体颜色
			'--data-table-header-color': '#515a6e',
			// DropDown下拉框背景色
			'--dropdown-menu-bg-color': '#ffffff',
			// DropDown下拉框边框颜色
			'--dropdown-menu-border-color': '#ffffff',
			// DropDown下拉框选项字体颜色
			'--dropdown-menu-item-color': '#2C2C30',
			// DropDown下拉框选项悬停背景色
			'--dropdown-menu-item-hover-bg-color': '#F3F3F3',
			//=====================缓存监控页====================
      // 缓存监控页面标题颜色
			'--cache-index-card-title-color': '#333',
      // 缓存监控页面卡片阴影颜色
			'--cache-index-box-shadow-color': 'rgba(0, 0, 0, 0.1)',
      // 缓存监控页面基本信息边框颜色
			'--cache-index-grid-row-border-bottom': '#ebeef5',
      // 缓存监控页面基本信息标签颜色
			'--cache-index-label-color': '#606266',
			//=====================缓存列表页====================
			// 缓存列表页面主字体颜色
			'--cache-list-main-text-color': '#303133',
			// 缓存列表页面次字体颜色
			'--cache-list-secondary-text-color': '#606266',
			// 缓存列表页面主边框颜色
			'--cache-list-main-border-color': '#ebeef5',
      // 缓存列表页面背景色
			'--cache-list-bg-color': '#edf4fa',
      // 缓存列表页面标题背景
			'--cache-list-page-header-bg-color': 'linear-gradient(to right, #f0f7ff, #ffffff)',
      // 缓存列表页面标题阴影
			'--cache-list-panel-container-box-shadow-color': 'rgba(0, 0, 0, 0.08)',
      // 缓存列表页面内容背景色
			'--cache-list-content-box-bg-color': '#f8f9fa',
			//=====================服务监控页====================
      // 服务监控页服务器项标题背景色
			'--server-item-title-bg-color': '#edf4fa',
			//=====================自定义选项卡组件====================
      // 自定义选项卡父级背景色
			'--custom-tabs-parent-tab-bg-color': '#edf4fa',
      // 自定义选项卡父级边框颜色
			'--custom-tabs-parent-tab-border': '#e6e6e6',
      // 自定义选项卡子级背景色
			'--custom-tabs-sub-tab-bg-color': '#dae6f0',
      // 自定义选项卡子级字体颜色
			'--custom-tabs-sub-tab-color': '#333333',
      // 自定义选项卡子级悬停阴影颜色
			'--custom-tabs-sub-tab-hover-box-shadow-color': 'rgba(0, 0, 0, 0.1)',
      // 自定义选项卡子级激活背景色
			'--custom-tabs-sub-tab-active-bg-color': '#ffffff',
      // 自定义选项卡子级激活字体颜色
			'--custom-tabs-sub-tab-active-color': '#1890ff',
			//=====================cron表达式====================
			// cron表达式弹窗边框颜色
			'--cron-popup-border-color': '#EEEEEE',
			// cron表达式弹窗背景颜色
			'--cron-popup-bg-color': '#FFFFFF',
			//=====================用户个人中心====================
			// 用户个人中心一级背景色
			'--user-profile-first-bg-color': '#f5f7fa',
			// 用户个人中心二级背景色
			'--user-profile-second-bg-color': '#ffffff',
			// 用户个人中心三级背景色
			'--user-profile-third-bg-color': '#fafafa',
			// 用户个人中心一级字体颜色
			'--user-profile-first-text-color': '#303133',
			// 用户个人中心二级字体颜色
			'--user-profile-second-text-color': '#606266',
			// 用户个人中心三级字体颜色
			'--user-profile-third-text-color': '#409EFF',
			// 用户个人中心一级边框颜色
			'--user-profile-first-border-color': '#ebeef5',
			//=====================上传图片示例页面====================
			// 上传图片示例页面一级背景色
			'--image-index-first-bg-color': '#ffffff',
			// 上传图片示例页面二级背景色
			'--image-index-second-bg-color': '#f8f9fa',
			// 上传图片示例页面三级背景色
			'--image-index-third-bg-color': '#ffffff',
			// 上传图片示例页面四级背景色
			'--image-index-fourth-bg-color': '#f5f7fa',
			// 上传图片示例页面一级字体色
			'--image-index-first-text-color': '#333333',
			// 上传图片示例页面二级字体色
			'--image-index-second-text-color': '#333333',
			// 上传图片示例页面三级字体色
			'--image-index-third-text-color': '#2c3e50',
			// 上传图片示例页面四级字体色
			'--image-index-fourth-text-color': '#409eff',
			// 上传图片示例页面五级字体色
			'--image-index-fifth-text-color': '#409eff',
			// 上传图片示例页面六级字体色
			'--image-index-sixth-text-color': '#f56c6c',
			// 上传图片示例页面七级字体色
			'--image-index-seventh-text-color': '#606266',
			// 上传图片示例页面一级边框颜色
			'--image-index-first-border-color': '#eeeeee',
			//=====================elementPlus变量替换====================
			//===============root============

			//============el-switch==========
			// 主题设置开关打开颜色
			'--el-switch-on-color-cover': '#409eff',
			// 主题设置开关关闭颜色
			'--el-switch-off-color-cover': '#e8e8e8',
			/*====================================================JS 变量使用驼峰命名=================================================*/
			//=====================通用列表页====================
      // 搜索按钮类名
			searchBtnClass: 'light-blue-btn',
      // 重置按钮类名
			resetBtnClass: 'pan-blue-btn',
      // 添加按钮ElementPlus按钮类型
			addBtnElType: 'primary' as ButtonType,
      // 编辑按钮ElementPlus按钮类型
			editBtnElType: 'success' as ButtonType,
      // 删除按钮ElementPlus按钮类型
			deleteBtnElType: 'danger' as ButtonType,
      // 重置密码按钮ElementPlus按钮类型
			resetPasswordBtnElType: 'warning' as ButtonType,
			// 字列表按钮ElementPlus按钮类型
			subListBtnElType: 'info' as ButtonType,
      // 数据表格内ElementPlus标签类型
			tableElTagType: 'info' as any,
			//=====================在线用户页====================
      // 强制退出按钮ElementPlus按钮类型
			compulsoryWithdrawalBtnElType: 'warning' as ButtonType,
			//=====================代码生成页====================
      // 刷新生成按钮ElementPlus按钮类型
			refreshGenBtnElType: 'info' as ButtonType,
      // 生成代码按钮ElementPlus按钮类型
			genCodeBtnElType: 'primary' as ButtonType,
			//=====================布局主页====================
      // 是否显示侧边栏底部图片
			isShowSidebarBottomPic: true,
      // 侧边栏底部图片地址
			sidebarBottomPic: '/src/assets/images/sidebar/bg.png',
			// 当前主题是基于ElementPlus的亮色主题经过修改后得到，所以这里需要首先关闭ElementPlus的暗色模式
			isOpenElementPlusDarkMode: false,
    },
    // 酷雅暗黑
    theme_black: {
			/*====================================================CSS 变量使用 -- 前缀，方便CSS直接绑定=================================================*/
			//=====================布局主页====================
      // 侧边栏最底部背景色
			'--aside-bg-color': '#18181C',
      // 侧边栏菜单字体颜色
			'--menu-color': '#E1E1E1',
      // 侧边栏菜单激活背景色
			'--menu-active-bg-color': 'rgba(99, 226, 180, 0.15)',
      // 侧边栏菜单激活字体颜色
			'--menu-active-color': '#63e2b7',
      // 侧边栏菜单悬停背景色
			'--menu-hover-bg-color': 'rgba(205, 209, 211, 0.1)',
      // 侧边栏菜单激活时，添加的一个前置区域背景色
			'--menu-active-before-bg-color': 'rgba(99, 226, 180, 0.8)',
      // 一号头部栏背景色
			'--header-one-bg-color': '#18181C',
			// 一号头部栏下边框颜色
			'--header-one-border-color': '#2b2b2f',
      // 一号头部栏阴影
			'--header-one-box-shadow-color': 'rgba(23, 23, 28, 0.5)',
      // 一号头部栏内的侧边栏折叠图标背景色
			'--icon-collapse-bg-color': 'rgba(205, 209, 211, 0.1)',
      // 一号头部栏内的设置图标颜色
			'--icon-setting-color': '#E1E1E1',
      // 一号头部栏内的设置图标悬停颜色
			'--icon-setting-hover-color': 'rgba(99, 226, 180, 1)',
      // 二号头部栏背景色
			'--header-two-bg-color': '#18181C',
			// 二号头部栏下边框颜色
			'--header-two-border-color': '#2b2b2f',
      // 二号头部栏标签已激活背景色
			'--tag-primary-bg-color': 'rgba(99, 226, 180, 0.15)',
      // 二号头部栏标签已激活字体颜色
			'--tag-primary-color': 'rgba(99, 226, 180, 1)',
      // 二号头部栏标签未激活背景色
			'--tag-other-bg-color': 'rgba(205, 209, 211, 0.1)',
      // 二号头部栏标签未激活字体颜色
			'--tag-other-color': '#E1E1E1',
			// 主题设置字体颜色
			'--theme-setting-color': '#E1E1E1',
			// 主题设置标题下边框颜色
			'--theme-setting-title-border-color': '#2b2b2f',
			// 主题设置选择主题hover背景色
			'--theme-setting-option-hover-bg-color': 'rgba(205, 209, 211, 0.1)',
			// 主题设置选中主题字体标签颜色
			'--theme-setting-active-color': 'rgba(99, 226, 180, 1)',
			//=====================全局css修改====================
      // 搜索表单背景色
			'--search-form-bg-color': '#26262A',
      // 搜索表单边框颜色
			'--search-form-border-color': '#3d3d3d',
      // 数据表格表头背景色
			'--data-table-header-bg-color': '#26262A',
      // 数据表格表头字体颜色
			'--data-table-header-color': '#e0e0e0',
			// DropDown下拉框背景色
			'--dropdown-menu-bg-color': '#48484e',
			// DropDown下拉框边框颜色
			'--dropdown-menu-border-color': '#48484e',
			// DropDown下拉框选项字体颜色
			'--dropdown-menu-item-color': '#E1E1E1',
			// DropDown下拉框选项悬停背景色
			'--dropdown-menu-item-hover-bg-color': '#58585D',
			//=====================缓存监控页====================
      // 缓存监控页面标题颜色
			'--cache-index-card-title-color': '#e0e0e0',
      // 缓存监控页面卡片阴影颜色
			'--cache-index-box-shadow-color': 'rgba(0, 0, 0, 0.2)',
      // 缓存监控页面基本信息边框颜色
			'--cache-index-grid-row-border-bottom': '#3d3d3d',
      // 缓存监控页面基本信息标签颜色
			'--cache-index-label-color': '#a0a0a0',
			//=====================缓存列表页====================
			// 缓存列表页面主字体颜色
			'--cache-list-main-text-color': '#e0e0e0',
			// 缓存列表页面次字体颜色
			'--cache-list-secondary-text-color': '#a0a0a0',
			// 缓存列表页面主边框颜色
			'--cache-list-main-border-color': '#3d3d3d',
      // 缓存列表页面背景色
			'--cache-list-bg-color': '#2d2d2d',
      // 缓存列表页面标题背景
			'--cache-list-page-header-bg-color': 'linear-gradient(to right, #1a1a1a, #2d2d2d)',
      // 缓存列表页面标题阴影
			'--cache-list-panel-container-box-shadow-color': 'rgba(0, 0, 0, 0.2)',
      // 缓存列表页面内容背景色
			'--cache-list-content-box-bg-color': '#262626',
      //=====================服务监控页====================
      // 服务监控页服务器项标题背景色
			'--server-item-title-bg-color': '#1a1a1a',
			//=====================自定义选项卡组件====================
      // 自定义选项卡父级背景色
			'--custom-tabs-parent-tab-bg-color': '#26262A',
      // 自定义选项卡父级边框颜色
			'--custom-tabs-parent-tab-border': '#3d3d3d',
      // 自定义选项卡子级背景色
			'--custom-tabs-sub-tab-bg-color': 'rgba(205, 209, 211, 0.1)',
      // 自定义选项卡子级字体颜色
			'--custom-tabs-sub-tab-color': '#E1E1E1',
      // 自定义选项卡子级悬停阴影颜色
			'--custom-tabs-sub-tab-hover-box-shadow-color': 'rgba(0, 0, 0, 0.1)',
      // 自定义选项卡子级激活背景色
			'--custom-tabs-sub-tab-active-bg-color': 'rgba(99, 226, 180, 0.15)',
      // 自定义选项卡子级激活字体颜色
			'--custom-tabs-sub-tab-active-color': 'rgba(99, 226, 180, 1)',
			//=====================cron表达式====================
			// cron表达式弹窗边框颜色
			'--cron-popup-border-color': '#414244',
			// cron表达式弹窗背景颜色
			'--cron-popup-bg-color': '#1B1C1D',
			//=====================用户个人中心====================
			// 用户个人中心一级背景色
			'--user-profile-first-bg-color': '#141414',
			// 用户个人中心二级背景色
			'--user-profile-second-bg-color': '#1f1f1f',
			// 用户个人中心三级背景色
			'--user-profile-third-bg-color': '#2a2a2a',
			// 用户个人中心一级字体颜色
			'--user-profile-first-text-color': '#e8e8e8',
			// 用户个人中心二级字体颜色
			'--user-profile-second-text-color': '#8c8c8c',
			// 用户个人中心三级字体颜色
			'--user-profile-third-text-color': 'rgba(99, 226, 180, 1)',
			// 用户个人中心一级边框颜色
			'--user-profile-first-border-color': '#434343',
			//=====================上传图片示例页面====================
			// 上传图片示例页面一级背景色
			'--image-index-first-bg-color': '#121212',
			// 上传图片示例页面二级背景色
			'--image-index-second-bg-color': '#1e1e1e',
			// 上传图片示例页面三级背景色
			'--image-index-third-bg-color': '#252525',
			// 上传图片示例页面四级背景色
			'--image-index-fourth-bg-color': '#2d2d2d',
			// 上传图片示例页面一级字体色
			'--image-index-first-text-color': '#ffffff',
			// 上传图片示例页面二级字体色
			'--image-index-second-text-color': '#e0e0e0',
			// 上传图片示例页面三级字体色
			'--image-index-third-text-color': '#64b5f6',
			// 上传图片示例页面四级字体色
			'--image-index-fourth-text-color': '#bb86fc',
			// 上传图片示例页面五级字体色
			'--image-index-fifth-text-color': '#4db6ac',
			// 上传图片示例页面六级字体色
			'--image-index-sixth-text-color': '#ef5350',
			// 上传图片示例页面七级字体色
			'--image-index-seventh-text-color': '#90caf9',
			// 上传图片示例页面一级边框颜色
			'--image-index-first-border-color': '#333333',
			//=====================elementPlus变量替换====================
			//===============root============

			//============el-switch==========
			// 主题设置开关打开颜色
			'--el-switch-on-color-cover': 'rgba(99, 226, 180, 1)',
			// 主题设置开关关闭颜色
			'--el-switch-off-color-cover': 'rgba(205, 209, 211, 0.1)',
			/*====================================================JS 变量使用驼峰命名=================================================*/
			//=====================通用列表页====================
			searchBtnClass: 'green-btn',
      // 重置按钮类名
			resetBtnClass: 'pan-blue-btn',
      // 添加按钮ElementPlus按钮类型
			addBtnElType: 'primary' as ButtonType,
      // 编辑按钮ElementPlus按钮类型
			editBtnElType: 'success' as ButtonType,
      // 删除按钮ElementPlus按钮类型
			deleteBtnElType: 'danger' as ButtonType,
      // 重置密码按钮ElementPlus按钮类型
			resetPasswordBtnElType: 'warning' as ButtonType,
			// 字列表按钮ElementPlus按钮类型
			subListBtnElType: 'info' as ButtonType,
      // 数据表格内ElementPlus标签类型
			tableElTagType: 'info' as any,
			//=====================在线用户页====================
      // 强制退出按钮ElementPlus按钮类型
			compulsoryWithdrawalBtnElType: 'warning' as ButtonType,
			//=====================代码生成页====================
      // 刷新生成按钮ElementPlus按钮类型
			refreshGenBtnElType: 'info' as ButtonType,
      // 生成代码按钮ElementPlus按钮类型
			genCodeBtnElType: 'primary' as ButtonType,
			//=====================布局主页====================
      // 是否显示侧边栏底部图片
			isShowSidebarBottomPic: false,
      // 侧边栏底部图片地址
			sidebarBottomPic: '',
			// 当前主题是基于ElementPlus的暗色主题经过修改后得到，所以这里需要首先开启ElementPlus的暗色模式
			isOpenElementPlusDarkMode: true,
    }
  }

  // 计算主题变量
  const themeVariables = computed(() => {
    const themeType = themeSetting.theme.value
    return themeConfig[themeType]
  })

  // 自动将CSS变量应用到根元素
  watchEffect(() => {
    const root = document.documentElement
    const theme = themeVariables.value

    // 只设置CSS变量（带--前缀的属性）
    Object.entries(theme).forEach(([key, value]) => {
      if (key.startsWith('--')) {
        root.style.setProperty(key, String(value))
      }
    })
  })

  return {
    themeVariables,
    themeSetting,
	themeStore,
  }
}
