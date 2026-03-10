import { type App } from 'vue'

// 定义字典服务接口类型（包含两个方法，load用于初始化字典数据，get用于在load初始化数据后获取数据）
interface DictService {
	load: (dictType: string) => Promise<void>
  get: (dictType: string) => any[]
}

// 定义注入键（用于类型安全）
const dictPluginKey: InjectionKey<DictService> = Symbol('dictService')

// 创建一个字典插件
export const dictPlugin = {
	//使用vue插件的方式安装
  install(app: App) {
		// 创建字典容器
		const globalDictRecord = reactive<Record<string, any>>({})

    // 定义字典插件中拥有的方法，包括加载字典数据和获取字典数据
    const service: DictService = {
			// 加载字典方法
      async load(dictType: string) {
				// 先判断字典是否已经加载过（其实就是看字典容器中是否已经有该数据），加载过则直接返回
        if (globalDictRecord[dictType]) return
        // 没有加载过，则异步获取字典数据
        const dictData = await fetchDict(dictType)
        // 加载完成后，将数据赋值给字典容器
        globalDictRecord[dictType] = dictData
      },

      // 获取字典方法
      get: (dictType: string) => globalDictRecord[dictType] || []
    }
		// 提供字典服务
    app.provide(dictPluginKey, service)
  }
}

// 异步获取字典数据
async function fetchDict(dictType: string) {
  console.log(`正在加载字典: ${dictType}`)

  return service({
		method: "get",
		url: "/dictData/list",
		params: {
			dictType: dictType
		},
	}).then((response) => response.data.data);
}

// 3. 组件使用的Hook
export function useDict(dictTypes: string[] = []) {
  // 获取字典上下文
  const dictService = inject(dictPluginKey)!

  // 调用字典插件中的load方法加载字典数据
  dictTypes.forEach(key => dictService.load(key))

  return {
    // 由于上方已经调用过load方法加载了字典数据，所以这里直接调用get方法获取数据即可
    getDict: dictService.get,
    // 给外部暴漏一个手动初始化字典数据的方法（一般情况下用不到，只有在一些特殊情况才会使用）
    loadDict: dictService.load
  }
}
