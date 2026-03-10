<template>
  <el-form ref="basicInfoForm" :model="localInfo" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item label="表名称" prop="tableName">
          <el-input placeholder="请输入" v-model="localInfo.tableName"/>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="表描述" prop="tableComment">
          <el-input placeholder="请输入" v-model="localInfo.tableComment" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="实体类名称" prop="className">
          <el-input placeholder="请输入" v-model="localInfo.className" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="作者" prop="functionAuthor">
          <el-input placeholder="请输入" v-model="localInfo.functionAuthor" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="备注" prop="remark">
          <el-input placeholder="请输入" v-model="localInfo.remark" :rows="3"/>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
	import type { ElForm, FormRules, FormInstance } from 'element-plus'
	import { isEqual, cloneDeep } from 'lodash-es'

	//接收父组件传递的info
	const props = defineProps({
		info: {
			type: Object,
			default: () => ({})
		},
	})

	//定义自定义事件
	const emit = defineEmits(["update:info"])

	//定义本地变量
	// 方法1：使用展开运算符（浅拷贝）（浅层次对象复制）
	const localInfo = ref(cloneDeep(props.info))
	// 方法2：使用JSON方法（深拷贝）（深层次对象复制）
	//const localInfo = ref(JSON.parse(JSON.stringify(props.info)))

	//监听父组件传递的info变化
	watch(() => props.info, (newVal) => {
		// 只有当父组件数据实际变化时才更新本地状态
		if (!isEqual(newVal, localInfo.value)) {
			localInfo.value = cloneDeep(newVal)
		}
	}, {
		deep: true, // 监听深层属性变化
		immediate: true // 初始化时也触发回调
	})

	//定义一个监听器（当localInfo变化时，调用自定义函数修改父组件值）
	watch(localInfo, (newVal) => {
		// 只有当本地数据实际变化时才触发更新
		if (!isEqual(newVal, props.info)) {
			emit('update:info', cloneDeep(newVal))
		}
	}, {
		deep: true
	})

	//表单验证规则
	const rules = ref<FormRules>({
		tableName: [{ required: true, message: "请输入表名称", trigger: "blur" }],
		tableComment: [{ required: true, message: "请输入表描述", trigger: "blur" }],
		className: [{ required: true, message: "请输入实体类名称", trigger: "blur" }],
		functionAuthor: [{ required: true, message: "请输入作者", trigger: "blur" }]
	})

	// 表单引用 - 直接暴露给父组件（父组件提交时统一进行表单验证）
	const basicInfoForm = ref<FormInstance | null>(null)
	defineExpose({ basicInfoForm })
</script>
