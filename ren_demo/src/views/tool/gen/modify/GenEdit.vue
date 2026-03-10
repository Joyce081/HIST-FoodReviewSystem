<template>
  <el-card>
    <el-tabs v-model="activeName">
      <el-tab-pane label="基本信息" name="basic">
				<BasicInfoForm ref="basicInfoRef" v-model:info="info" />
			</el-tab-pane>

			<el-tab-pane label="字段信息" name="columnInfo">
				<el-table ref="dragTableRef" :data="columns" row-key="columnId" :max-height="tableHeight">
					<el-table-column label="序号" type="index" min-width="5%" class-name="allowDrag" />
					<el-table-column label="字段列名" prop="columnName" min-width="10%" :show-overflow-tooltip="true" class-name="allowDrag" />

					<el-table-column label="字段描述" min-width="10%">
						<template #default="scope">
							<el-input v-model="scope.row.columnComment" />
						</template>
					</el-table-column>

					<el-table-column
						label="物理类型"
						prop="columnType"
						min-width="10%"
						:show-overflow-tooltip="true"
					/>

					<el-table-column label="Java类型" min-width="11%">
						<template #default="scope">
							<el-select v-model="scope.row.javaType">
								<el-option label="Long" value="Long" />
								<el-option label="String" value="String" />
								<el-option label="Integer" value="Integer" />
								<el-option label="Double" value="Double" />
								<el-option label="BigDecimal" value="BigDecimal" />
								<el-option label="Date" value="Date" />
								<el-option label="Boolean" value="Boolean" />
							</el-select>
						</template>
					</el-table-column>

					<el-table-column label="java属性" min-width="10%">
						<template #default="scope">
							<el-input v-model="scope.row.javaField" />
						</template>
					</el-table-column>

					<el-table-column label="插入" min-width="5%">
						<template #default="scope">
							<el-checkbox
								v-model="scope.row.isInsert"
								true-value="1"
								false-value="0"
							/>
						</template>
					</el-table-column>

					<el-table-column label="编辑" min-width="5%">
						<template #default="scope">
							<el-checkbox
								v-model="scope.row.isEdit"
								true-value="1"
								false-value="0"
							/>
						</template>
					</el-table-column>

					<el-table-column label="列表" min-width="5%">
						<template #default="scope">
							<el-checkbox
								v-model="scope.row.isList"
								true-value="1"
								false-value="0"
							/>
						</template>
					</el-table-column>

					<el-table-column label="查询" min-width="5%">
						<template #default="scope">
							<el-checkbox
								v-model="scope.row.isQuery"
								true-value="1"
								false-value="0"
							/>
						</template>
					</el-table-column>

					<el-table-column label="查询方式" min-width="10%">
						<template #default="scope">
							<el-select v-model="scope.row.queryType">
								<el-option label="=" value="EQ" />
								<el-option label="!=" value="NE" />
								<el-option label=">" value="GT" />
								<el-option label=">=" value="GTE" />
								<el-option label="<" value="LT" />
								<el-option label="<=" value="LTE" />
								<el-option label="LIKE" value="LIKE" />
								<el-option label="BETWEEN" value="BETWEEN" />
							</el-select>
						</template>
					</el-table-column>

					<el-table-column label="必填" min-width="5%">
						<template #default="scope">
							<el-checkbox
								v-model="scope.row.isRequired"
								true-value="1"
								false-value="0"
							/>
						</template>
					</el-table-column>

					<el-table-column label="显示类型" min-width="12%">
						<template #default="scope">
							<el-select v-model="scope.row.htmlType">
								<el-option label="文本框" value="input" />
								<el-option label="文本域" value="textarea" />
								<el-option label="下拉框" value="select" />
								<el-option label="单选框" value="radio" />
								<el-option label="复选框" value="checkbox" />
								<el-option label="日期控件" value="datetime" />
								<el-option label="图片上传" value="imageUpload" />
								<el-option label="文件上传" value="fileUpload" />
								<el-option label="富文本控件" value="editor" />
							</el-select>
						</template>
					</el-table-column>

					<el-table-column label="字典类型" min-width="12%">
						<template #default="scope">
							<el-select v-model="scope.row.dictType" clearable filterable placeholder="请选择">
								<el-option
									v-for="dict in dictOptions"
									:key="dict.dictType"
									:label="dict.dictName"
									:value="dict.dictType"
								>
									<span style="float: left">{{ dict.dictName }}</span>
									<span style="float: right; color: #8492a6; font-size: 13px">
										{{ dict.dictType }}
									</span>
								</el-option>
							</el-select>
						</template>
					</el-table-column>
				</el-table>
			</el-tab-pane>

			<el-tab-pane label="生成信息" name="genInfo">
				<!-- 只针对info进行双向绑定，tables与menus在子组件内部未进行修改，所以仅需进行数据提供，无需进行双向绑定 -->
				<GenInfoForm
					ref="genInfoRef"
					v-model:info="info"
					:tables="tables"
					:menus="menus"
				/>
			</el-tab-pane>
    </el-tabs>

    <div class="form-actions">
      <el-button type="primary" @click="handleSubmit">提交</el-button>
      <el-button @click="handleClose">返回</el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
	import Sortable from 'sortablejs'
	import GenInfoForm from './GenInfoForm.vue'
	import BasicInfoForm from './BasicInfoForm.vue'
	import { getGenTable, updateGenTable } from "@/api/tool/gen"
	import { listNotPageDictType } from "@/api/system/dict/dictType"
	import { listTreeSelectMenu } from "@/api/system/menu"

	//路由对象，用于获取当前路由信息（参数/查询字符串）等
	const route = useRoute()

	// 标签页双向绑定（当前激活的标签页）
	const activeName = ref("columnInfo")
	// 表格高度
	const tableHeight = ref(`${document.documentElement.scrollHeight - 245}px`)

	// 基础信息数据
	const info = ref({} as any)
	// 当前表列数据
	const columns = ref<any[]>([])
	// 字典数据
	const dictOptions = ref<any[]>([])
	// 子表数据
	const tables = ref([])
	// 菜单数据
	const menus = ref([])


	// 基础信息组件
	const basicInfoRef = ref<InstanceType<typeof BasicInfoForm> | null>(null)
	// 可拖拽表格
	const dragTableRef = ref<any>(null)
	// 生成信息组件
	const genInfoRef = ref<InstanceType<typeof GenInfoForm> | null>(null)


	// 组件挂载完成后，获取数据并初始化可拖拽表格
	onMounted(() => {
		//初始化可拖拽表格
		initSortable()
		// 异步获取数据
		fetchData()
	})

	// 初始化可拖拽表格
	const initSortable = () => {
		nextTick(() => {
			if (!dragTableRef.value) return
			const el = dragTableRef.value.$el.querySelector('.el-table__body-wrapper tbody')
			if (!el) return

			Sortable.create(el, {
				handle: ".allowDrag",
				onEnd: (evt) => {
					if (evt.oldIndex === undefined || evt.newIndex === undefined) return

					const targetRow = columns.value.splice(evt.oldIndex, 1)[0]
					columns.value.splice(evt.newIndex, 0, targetRow)

					// 更新排序号
					columns.value.forEach((col, index) => {
						col.sort = index + 1
					})
				}
			})
		})
	}

	// 获取表格列信息、基础信息、子表信息、字典数据、菜单数据
	const fetchData = async () => {
		const tableId = route.query?.tableId
		if (!tableId) return

		try {
			const tableRes = await getGenTable({tableId})
			columns.value = tableRes.genTableColumnList
			info.value = tableRes.genTable
			tables.value = tableRes.subTables

			const dictRes = await listNotPageDictType()
			dictOptions.value = dictRes.data

			const menuRes = await listTreeSelectMenu()
			menus.value = menuRes.data
		} catch (error) {
			console.error("获取数据失败:", error);
			ElMessage.error("获取数据失败，请重试");
		}
	}

	// 提交表单
	const handleSubmit = async () => {
		//如果基本表单组件或生成信息表单组件实例不存在（未正确初始化），则立即停止提交操作并退出函数。
		if (!basicInfoRef.value || !genInfoRef.value) return

		// 获取基础信息表单和生成信息表单
		const basicForm = basicInfoRef.value.basicInfoForm as any
		const genForm = genInfoRef.value.genInfoForm as any

		// 进行基础信息表单和生成信息表单的校验
		const [basicValid, genValid] = await Promise.all([
			getFormPromise(basicForm),
			getFormPromise(genForm)
		])

		// 校验通过，进行数据提交
		if (basicValid && genValid) {
			// 三个页面数据合并在一起
			const genTable = {
				...info.value,
				columns: columns.value,
				optionsMaps: {
					treeCode: info.value.treeCode,
					treeName: info.value.treeName,
					treeParentCode: info.value.treeParentCode,
					parentMenuId: info.value.parentMenuId,
					parentMenuName: info.value.parentMenuName
				}
			}

			try {
				// 提交数据
				const res = await updateGenTable(genTable)
				ElMessage.success(res.msg)
				if (res.code === 200) {
					handleClose()
				}
			} catch (error) {
				console.error("更新失败:", error);
    		ElMessage.error("更新失败，请重试");
			}
		} else {
			ElMessage.error("表单校验未通过，请重新检查提交内容")
		}
	}

	// 表单验证Promise封装
	const getFormPromise = (form: any) => {
		return new Promise(resolve => {
			form.validate(resolve)
		})
	}

	// 关闭当前页面跳转到列表页
	const handleClose = () => {
		tabComposable.closePage({
			targetPath: "/tool/gen",
		})
	}
</script>

<style scoped>
	.form-actions {
		text-align: center;
		margin-top: 20px;
	}
</style>
