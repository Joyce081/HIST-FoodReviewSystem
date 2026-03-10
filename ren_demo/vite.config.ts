import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import Components from 'unplugin-vue-components/vite';
import ElementPlus from 'unplugin-element-plus/vite'
import AutoImport from 'unplugin-auto-import/vite'
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import path from 'path';
import Icons from 'unplugin-icons/vite'
// 引入自动导入解析器
// 比较大的UI库，由unplugin-vue-components官方进行维护
import { ElementPlusResolver,AntDesignVueResolver } from 'unplugin-vue-components/resolvers';
// 比较小的UI库，由UI库自己进行维护
import IconsResolver from 'unplugin-icons/resolver'
import {AntDesignXVueResolver} from 'ant-design-x-vue/resolver';

// 函数式配置示例（需要环境变量时）
// command表示当前运行的命令（可用于判断是开发环境还是生产环境），mode当前运行的环境模式（可用于根据不同的环境加载不同的环境变量）
export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), 'VITE_APP_')

  // 创建路径别名解析器
  const pathSrc = fileURLToPath(new URL('./src', import.meta.url))

  return {
    //配置路径映射，@对应到src目录
    resolve: {
      alias: {
        '@': pathSrc
      },
    },

    //插件相关配置
    plugins: [
      vue(),
      //Vue开发工具辅助（注意，一旦开启这个，浏览器插件将失效，因为会打架，但是没关系，这个更好）（同时注意，这个插件只能在Vite+Vue3.3+以上版本才可以用，低版本的还是只能用浏览器自带插件）
      vueDevTools(),
      VueSetupExtend(),

      // 配置API自动导入
      AutoImport({

        imports: [
					// 自动导入 Vue 相关函数，如：ref, reactive, toRef 等
					'vue',
          'pinia',
					// 自动导入VUE路由相关函数
					{
						'vue-router': ['useRoute', 'useRouter'],
					},
					//自动导入单个自定义函数(支持文件内普通函数导出、普通 JavaScript 对象导出，但是注意，不支持嵌套函数以及类的导出)
					{
						'@/services/navigation.service': ['navigationService'],
						'@/plugins/dict': ['useDict'],
						'@/composables/tab': ['tabComposable'],
						'@/services/axios': ['service'],
						'@/services/sseService': ['sseService'],
						'@/composables/useCrypto': ['useCrypto'],
					}
				],
        // 按需加载 Element Plus 所需函数
        resolvers: [
          // 自动按需导入 Element Plus 函数，如ElMessageBox、ElMessage、EffectScope
          ElementPlusResolver(),
          // 自动导入图标组件（这里对应的是JS中的导入）
          // 相当于替代了 import IconEpEdit from 'virtual:icons/ep/edit';
          // 可以直接在JS中使用 const edIcon = IconEpEdit（注意应为Icon + 驼峰式图标名）
          IconsResolver({
            prefix: 'Icon', // JS变量名前缀（驼峰式）如（IconEpEdit）
            enabledCollections: ['ep'] // 只导入Element Plus图标集
          }),
        ],
				// 导入目录文件
				dirs: [
					// 自动导入 src/utils 目录下所有自定义函数
					'./src/utils',
				],
        // 生成 auto-imports.d.ts 类型声明文件
        dts: true,
      }),

      // 配置组件自动导入
      Components({
        // 按需加载 UI 库（例如 Element Plus）
        resolvers: [
          // 自动按需导入 Element Plus 组件，如<el-menu></el-menu>
          ElementPlusResolver(),
          // 自动导入图标组件（这里对应的是模板中的导入）
          // 相当于替代了 import { House } from '@element-plus/icons-vue';
          // 可以直接在模板中使用 <el-icon><i-ep-house /></el-icon>（注意应为i- + 连字符图标名）
          IconsResolver({
            prefix: 'i', // 图标组件前缀（对应菜单中的 `i-ep-house`）
            enabledCollections: ['ep'], // 启用 Element Plus 图标集
            // 图标名称转换规则
            alias: {
              'ep': 'element-plus' // 将 'ep' 映射到 @element-plus/icons-vue
            }
          }),
          // 自动按需导入 Ant Design Vue 组件（由于AntDesignVueResolver解析器是unplugin-vue-components维护的，unplugin-vue-components为了兼容新旧版本，所以配置项不能随便改，所以这里需要开发者手动指定配置项）
          AntDesignVueResolver({importStyle: false, resolveIcons: true}),
          // 自动按需导入 Ant Design X Vue 组件
          AntDesignXVueResolver(),
        ],

        // 自动导入自定义组件（src/components 下的组件）
        dirs: [
          'src/components',
          'src/components/**' // 递归扫描子目录
        ],
        // 启用深度扫描
        deep: true,

        // 使用默认配置，自动生成 components.d.ts 类型声明文件
        dts: true,
        // 指定声明文件的生成目录，即生成到 src/auto-imports.d.ts
        //dts: path.resolve(pathSrc, 'auto-imports.d.ts')
      }),

      // 图标加载器
      Icons({
        compiler: 'vue3',
        autoInstall: true,// 自动安装图标库
        scale: 1, // 确保图标缩放比例正确
        defaultClass: 'ep-icon', // 添加默认类名（可用于加样式）
      }),

      // 配置样式自动导入（各组件不一样需要单独配置）
      ElementPlus({
        useSource: true,
      }),

			// 原生 SVG Sprite 技术（用于应对ElementPlus原生SVG图标不够多的情况）
			// 基于 SVG Sprite 技术。所有图标被合并到一个大的 SVG 文件中（称为雪碧图），每个图标通过 <symbol> 定义。
			// <use> 标签通过 <use xlink:href="#icon-user"> 引用雪碧图中的特定图标。
			// src/assets/icons是雪碧图文件路径
			// symbolId: 'icon-[name]'定义了图标在雪碧图中的ID格式，[name]会被图标文件名替换
			// 使用原生的SVG技术，定义了一个自定义组件（SvgIcon.vue），可以直接通过该自定义组件简单快捷的使用原生SVG图标
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')], //指定静态图标目录
        symbolId: 'icon-[name]', // 符号ID格式，如#icon-user
        svgoOptions: {
          plugins: [{ name: 'removeAttrs', params: { attrs: ['fill'] } }] // 移除默认颜色
        }
      }),

    ],

		// 添加 CSS 配置部分
    css: {
      preprocessorOptions: {
        scss: {
          // 全局注入 SCSS 变量和 mixins
          additionalData: `
						@use "@/assets/css/base/variables.scss" as *;
          `,
          // 其他 SCSS 配置选项...
        }
      }
    },

    // 开发服务器配置
    server: {
      // 将此设置为 '0.0.0.0' 是关键步骤
      host: '0.0.0.0', 
      // 设置开发服务器端口号
      port: 5173,
      // 允许指定主机访问，允许外网访问开发环境
      allowedHosts: ['xtwitovmfcgx.sealoshzh.site'], 
      // 启动时自动打开浏览器
      open: false,
      // 代理配置（解决跨域问题的关键）
      proxy: {
        // 使用动态属性名作为代理路径前缀
        [env.VITE_APP_BASE_API]: {
          // 目标服务器地址（后端API的真实地址）
          target: env.VITE_APP_BACKEND_URL,
          // 允许跨域
          changeOrigin: true,
          // 禁用 HTTPS 证书验证
          secure: false,
          // 路径重写，将路径中的 VITE_APP_BASE_API 替换为空字符串
          rewrite: path => path.replace(new RegExp(`^${env.VITE_APP_BASE_API}`), '')
        }
      }
    },

    // 生产环境配置
    build: {
      outDir: 'dist',
      sourcemap: true // 启用源映射，用于生产环境调试
    }
  }
})

// 静态配置（适合简单场景）
// export default defineConfig({
//   //配置路径映射，@对应到src目录
//   resolve: {
//     alias: {
//       '@': fileURLToPath(new URL('./src', import.meta.url))
//     },
//   },
//   //插件相关配置
//   plugins: [
//     vue(),
//     //Vue开发工具辅助（注意，一旦开启这个，浏览器插件将失效，因为会打架，但是没关系，这个更好）（同时注意，这个插件只能在Vite+Vue3.3+以上版本才可以用，低版本的还是只能用浏览器自带插件）
//     vueDevTools(),
//     VueSetupExtend(),

//     // 配置API自动导入
//     AutoImport({
//       // 自动导入 Vue 相关函数，如：ref, reactive, toRef 等
//       imports: ['vue'],
//       // 按需加载 Element Plus 所需函数
//       resolvers: [
//         // 自动按需导入 Element Plus 函数，如ElMessageBox
//         ElementPlusResolver(),
//         // 自动导入图标组件（这里对应的是JS中的导入）
//         // 相当于替代了 import IconEpEdit from 'virtual:icons/ep/edit';
//         // 可以直接在JS中使用 const edIcon = IconEpEdit（注意应为Icon + 驼峰式图标名）
//         IconsResolver({
//           prefix: 'Icon', // JS变量名前缀（驼峰式）如（IconEpEdit）
//           enabledCollections: ['ep'] // 只导入Element Plus图标集
//         }),
//       ],
//       // 生成 auto-imports.d.ts 类型声明文件
//       dts: true,
//     }),

//     // 配置组件自动导入
//     Components({
//       // 按需加载 UI 库（例如 Element Plus）
//       resolvers: [
//         // 自动按需导入 Element Plus 组件，如<el-menu></el-menu>
//         ElementPlusResolver(),
//         // 自动导入图标组件（这里对应的是模板中的导入）
//         // 相当于替代了 import { House } from '@element-plus/icons-vue';
//         // 可以直接在模板中使用 <el-icon><i-ep-house /></el-icon>（注意应为i- + 连字符图标名）
//         IconsResolver({
//           prefix: 'i', // 图标组件前缀（对应菜单中的 `i-ep-house`）
//           enabledCollections: ['ep'], // 启用 Element Plus 图标集
//           // 图标名称转换规则
//           alias: {
//             'ep': 'element-plus' // 将 'ep' 映射到 @element-plus/icons-vue
//           }
//         })
//       ],

//       // 自动导入自定义组件（src/components 下的组件）
//       dirs: [
//         'src/components',
//         'src/components/**' // 递归扫描子目录
//       ],
//       // 启用深度扫描
//       deep: true,

//       // 使用默认配置，自动生成 components.d.ts 类型声明文件
//       dts: true,
//       // 指定声明文件的生成目录，即生成到 src/auto-imports.d.ts
//       //dts: path.resolve(pathSrc, 'auto-imports.d.ts')
//     }),

//     // 图标加载器
//     Icons({
//       compiler: 'vue3',
//       autoInstall: true,// 自动安装图标库
//       scale: 1, // 确保图标缩放比例正确
//       defaultClass: 'ep-icon', // 添加默认类名（可用于加样式）
//     }),

//     // 配置样式自动导入（各组件不一样需要单独配置）
//     ElementPlus({
//       useSource: true,
//     }),

//     // SvgIcon配置插件，指定 SVG 图标目录和 Symbol ID 格式
//     // 将自己本地静态的icon图标（svg后缀）生成雪碧图符号
//     // 需配合 <use xlink:href="#icon-user"> 使用，依赖 CSS 覆盖原生属性（如 fill）
//     createSvgIconsPlugin({
//       iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')], //指定静态图标目录
//       symbolId: 'icon-[name]', // 符号ID格式，如#icon-user
//       svgoOptions: {
//         plugins: [{ name: 'removeAttrs', params: { attrs: ['fill'] } }] // 移除默认颜色
//       }
//     }),
//   ],
// })
