<template>
  <el-form ref="genInfoForm" :model="localInfo" :rules="rules" label-width="150px">
    <el-row>
			<!-- 基础信息 -->
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <template #label>生成模板</template>
          <el-select v-model="localInfo.tplCategory" @change="tplSelectChange">
            <el-option label="单表（增删改查）" value="crud" />
            <el-option label="树表（增删改查）" value="tree" />
            <el-option label="主子表（增删改查）" value="sub" />
          </el-select>
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="packageName">
          <template #label>
            生成包路径
            <el-tooltip content="生成在哪个java包下，例如 com.ruoyi.system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-input v-model="localInfo.packageName" />
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="moduleName">
          <template #label>
            生成模块名
            <el-tooltip content="可理解为子系统名，例如 system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-input v-model="localInfo.moduleName" />
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="businessName">
          <template #label>
            生成业务名
            <el-tooltip content="可理解为功能英文名，例如 user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-input v-model="localInfo.businessName" />
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="functionName">
          <template #label>
            生成功能名
            <el-tooltip content="用作类描述，例如 用户" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-input v-model="localInfo.functionName" />
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="genType">
          <template #label>
            生成代码方式
            <el-tooltip content="默认为zip压缩包下载，也可以自定义生成路径" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-radio v-model="localInfo.genType" label="0">zip压缩包</el-radio>
          <el-radio v-model="localInfo.genType" label="1">自定义路径</el-radio>
        </el-form-item>
      </el-col>
			<el-col :span="12">
        <el-form-item prop="parentMenuId">
          <template #label>
            上级菜单
            <el-tooltip content="分配到指定菜单下，例如 系统管理" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-tree-select
						v-model="localInfo.parentMenuId"
						:data="menus"
						check-strictly
						:render-after-expand="false"
						:empty-values="[null, undefined, -1]"
						:value-on-clear="undefined"
						placeholder="请选择"
						node-key="id"
						:props="{
							value: 'id',
							label: 'label',
							children: 'children',
							disabled: 'disabled',
						}"
					/>
        </el-form-item>
      </el-col>
			<el-col :span="12" v-if="localInfo.genType == '1'">
        <el-form-item prop="genPath">
          <template #label>
            自定义路径
            <el-tooltip content="填写磁盘绝对路径，若不填写，则生成到当前Web项目下" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-input v-model="localInfo.genPath" />
        </el-form-item>
      </el-col>
    </el-row>

		<!-- 当生成模板选择树表时显示 -->
		<el-row v-if="localInfo.tplCategory == 'tree'">
			<el-divider content-position="left"><span style="color: #409eff;font-size: 16px;font-weight: bolder;">其他信息</span></el-divider>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            树编码字段
            <el-tooltip content="树显示的编码字段名， 如：dept_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-select v-model="localInfo.treeCode" placeholder="请选择">
            <el-option
              v-for="(column, index) in localInfo.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            树父编码字段
            <el-tooltip content="树显示的父编码字段名， 如：parent_Id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-select v-model="localInfo.treeParentCode" placeholder="请选择">
            <el-option
              v-for="(column, index) in localInfo.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            树名称字段
            <el-tooltip content="树节点的显示名称字段名， 如：dept_name" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-select v-model="localInfo.treeName" placeholder="请选择">
            <el-option
              v-for="(column, index) in localInfo.columns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
		</el-row>

		<!-- 当生成模板选择主子表时显示 -->
		<el-row v-show="localInfo.tplCategory == 'sub'">
			<el-divider content-position="left"><span style="color: #409eff;font-size: 16px;font-weight: bolder;">关联信息</span></el-divider>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            关联子表的表名
            <el-tooltip content="关联子表的表名， 如：sys_user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-select v-model="localInfo.subTableName" placeholder="请选择" @change="subSelectChange">
            <el-option
              v-for="(table, index) in tables"
              :key="index"
              :label="table.tableName + '：' + table.tableComment"
              :value="table.tableName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <template #label>
            子表关联的外键名
            <el-tooltip content="子表关联的外键名， 如：user_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </template>
          <el-select v-model="localInfo.subTableFkName" placeholder="请选择">
            <el-option
              v-for="(column, index) in subColumns"
              :key="index"
              :label="column.columnName + '：' + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
	import type { ElForm, FormRules, FormInstance } from 'element-plus'
	import { isEqual, cloneDeep } from 'lodash-es'

	//接收父组件传输的参数
	const props = defineProps({
		info: {
			type: Object,
			default: () => ({})
		},
		tables: {
			type: Array as PropType<any[]>,
			default: () => []
		},
		menus: {
			type: Array as PropType<any[]>,
			default: () => []
		}
	})

	//定义表单验证规则
	const rules = ref<FormRules>({
		tplCategory: [{ required: true, message: "请选择生成模板", trigger: "blur" }],
		packageName: [{ required: true, message: "请输入生成包路径", trigger: "blur" }],
		moduleName: [{ required: true, message: "请输入生成模块名", trigger: "blur" }],
		businessName: [{ required: true, message: "请输入生成业务名", trigger: "blur" }],
		functionName: [{ required: true, message: "请输入生成功能名", trigger: "blur" }]
	})

	// 定义本地变量
	const localInfo = ref(cloneDeep(props.info))

	// 监听props.info变化
	watch(() => props.info, (newVal) => {
		// 只有当父组件数据实际变化时才更新本地状态
		if (!isEqual(newVal, localInfo.value)) {
			localInfo.value = cloneDeep(newVal)
		}
	}, {
		deep: true, // 监听深层属性变化
		immediate: true // 初始化时也触发回调
	})

	//定义事件
	const emit = defineEmits(["update:info"])

	//监听localInfo变化，当localInfo变化时，调用自定义函数修改父组件值
	watch(localInfo, (newVal) => {
		const payload = {
			...newVal,
			parentMenuName: parentMenuName.value
		}
		// 只有当本地数据实际变化时才触发更新
		if (!isEqual(payload, props.info)) {
			emit('update:info', cloneDeep(payload))
		}
	}, { deep: true })

	//监听localInfo.subTableName变化，当localInfo.subTableName变化时，修改子表字段列表（用于选取关联外键字段）
	watch(() => localInfo.value.subTableName, (newVal) => {
		setSubTableColumns(newVal);
	})

	// 添加计算属性，联动修改parentMenuName
	const parentMenuName = computed(() => {
		const findMenu = (menus: any[]): any => {
			for (const menu of menus) {
				if (menu.id == localInfo.value.parentMenuId) return menu
				if (menu.children?.length) {
					const found = findMenu(menu.children)
					if (found) return found
				}
			}
			return null
		}
		const menu = findMenu(props.menus) || {}
		return menu.label || ''
	})

	// 选择子表名触发
	const subSelectChange = () => {
		if (props.info) {
			localInfo.value.subTableFkName = ''
		}
	}
	// 选择生成模板触发
	const tplSelectChange = (value: string) => {
		if (value !== 'sub' && props.info) {
			localInfo.value.subTableName = ''
			localInfo.value.subTableFkName = ''
		}
	}

	/** 设置关联外键 */
	const subColumns = ref<any[]>([]);
	const setSubTableColumns = (value: string) => {
		for (const table of props.tables) {
			if (value === table.tableName) {
				subColumns.value = table.columns;
				break;
			}
		}
	}

	// 表单引用 - 直接暴露给父组件（父组件提交时统一进行表单验证）
	const genInfoForm = ref<FormInstance | null>(null)
	defineExpose({ genInfoForm })
</script>
