/*=================================================================JS相关内容==================================================================*/
// 引入app组件
import App from "./App.vue";
// 引入路由组件
import router from "./router";
//引入axios过滤器
import { setupInterceptors } from "@/services/axios";
// el-icon
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// 引入ElementPlus（全局中文配置相关内容）
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
// 引入表单设计器
import FcDesigner from '@form-create/designer';
// 引入字典插件
import {dictPlugin} from '@/plugins/dict';

/*=================================================================自定义ICON相关==================================================================*/
// 引入SvgIcon字体图标
// import SvgIcon from '@/components/SvgIcon/index.vue';
// 配合vite.config.ts中的createSvgIconsPlugin，用于生成雪碧图（注意，必须入口文件main.ts引入，不能在vite.config.ts引入，因为他以来了node.js）
import "virtual:svg-icons-register";

/*=================================================================CSS相关内容==================================================================*/
// 引入element-plus的css
import 'element-plus/dist/index.css';
// ElementPlus暗黑模式文件
import 'element-plus/theme-chalk/dark/css-vars.css'
// 引入自定义全局 CSS
import "@/assets/css/main.scss";
// 引入全局css
import "@/assets/css/views/index.scss";
// 引入Ant Design Vue的css（由于Ant Design Vue的特殊性，所以即使在vite.config.ts中配置了Ant Design Vue的自动导入，还是需要在这里引入一下Ant Design Vue的CSS）
import 'ant-design-vue/dist/reset.css'

// 创建app
const app = createApp(App);
// 创建pinia
const pinia = createPinia();

// app挂载（注册）pinia，注意：这里只是对pinia进行了注册，后面自定义组件中使用的时候，还需要进行导入
app.use(pinia);
// app挂载（注册）router，注意：这里只是对router进行了注册，后面自定义组件中使用的时候，还需要进行导入
app.use(router);
// app挂载（注册）ElementPlus，并设置全局中文，注意：这里只是对ElementPlus进行了注册，后面自定义组件中使用的时候，还需要进行导入
// 其实这里按理来说无需再进行全局注册了，但是由于有一个外置模块（表单构建）用到了他，所以这里还需要进行全局注册
app.use(ElementPlus, {
	locale: zhCn,
});
// app挂载（注册）表单设计器，注意：这里只是对FcDesigner进行了注册，后面自定义组件中使用的时候，还需要进行导入
app.use(FcDesigner);
app.use(FcDesigner.formCreate);
// app挂载（注册）自定义字典插件，注意：这里只是对dictPlugin进行了注册，后面自定义组件中使用的时候，还需要进行导入
app.use(dictPlugin);

// 注册Service Worker（文件位置在/public/sw.js）（主要给Swagger请求Vue获取Token使用）
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/sw.js', { scope: '/' }).then(reg => {
      // 关键：等待 Service Worker 激活
      if (reg.active) {
        sendRegistrationMessage();
      } else {
        reg.installing?.addEventListener('statechange', () => {
          if (reg.active?.state === 'activated') sendRegistrationMessage();
        });
      }
    })
    .catch(console.error);
}
//发送注册消息
function sendRegistrationMessage() {
	//当前页面发送一个消息到serviceWorker，主要用于在serviceWorker中获取消息，然后serviceworker就能获取到发送这个消息的客户端（也就是Vue所属的客户端）
  navigator.serviceWorker.controller?.postMessage({
    type: "REGISTER_AS_MAIN_CLIENT",
    isMain: true // 显式标识主客户端
  });
}

// 注册自定义 SVG 组件
// ​第一个参数 'SvgIcon'，表示 ​组件在模板中使用的标签名​（字符串类型），在模板中通过 <svg-icon> 或 <SvgIcon> 调用（Vue 支持两种写法，但需注意命名规范），推荐使用 kebab-case（短横线分隔）如 svg-icon，但 PascalCase（大驼峰）如 SvgIcon 也兼容
// 第二个参数 SvgIcon，表示 ​具体的组件对象​（变量名），通常是导入的 .vue 文件或 JSX 组件
// 因为项目中没有用到针对这个自定义组件的动态绑定，只正常用到了模板中使用，并且我已经在vite.config.ts中配置了按需导入自定义组件，所以这里就无需全局注册SvgIcon了
// app.component('SvgIcon', SvgIcon);

// 循环​批量注册 Element Plus 图标，注册为i-ep-xxx 格式
// 如果项目中没有使用动态绑定组件，那么main.ts中的全局注册和vite.config.ts中的按需导入，只需要任选其一即可（vite.config.ts中的按需导入优先，性能更好）
// 如果项目中使用到了动态绑定组件，那么这里必须配置全局注册，vite.config.ts中就无需配置按需导入，当然配置了也不会出错
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(`i-ep-${key.toLowerCase()}`, component);
}
//挂载（注册）App
app.mount("#app");
//在Pinia初始化完成之后创建axios拦截器
setupInterceptors();
