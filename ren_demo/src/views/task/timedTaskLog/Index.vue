<template>
	<div class="search-form-div">
		<el-form
			ref="searchFormRef"
			:rules="rules"
			:inline="true"
			:model="tableParams"
			class="search-form"
			label-width="68px"
			size="large"
		>
			<el-form-item label="查询" prop="searchLike">
				<el-input
					v-model="tableParams.searchLike"
					placeholder="任务名称/任务组名/调用目标字符串"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="handleQuery" :class="themeVariables.searchBtnClass || 'light-blue-btn'" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" :class="themeVariables.resetBtnClass || 'pan-blue-btn'" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-table border stripe row-key="taskLogId" :data="tableData" class="custom-table" @sort-change="handleTableSort" v-loading="isLoading">
		<el-table-column prop="taskName" label="任务名称" width="200" show-overflow-tooltip></el-table-column>
		<el-table-column prop="taskGroup" label="任务组名" width="100" show-overflow-tooltip></el-table-column>
		<el-table-column prop="invokeTarget" label="调用目标字符串" width="500" show-overflow-tooltip></el-table-column>
		<el-table-column prop="taskMessage" label="日志信息" width="400" show-overflow-tooltip></el-table-column>
		<el-table-column prop="status" label="执行状态" width="120" show-overflow-tooltip sortable align="center">
			<template #default="item">
				<el-tag v-if="item.row.status == 0" type="info">正常</el-tag>
				<el-tag v-if="item.row.status == 1" type="info">失败</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="exceptionInfo" label="异常信息" show-overflow-tooltip></el-table-column>
		<el-table-column prop="startTimeStr" label="开始时间" width="180" show-overflow-tooltip ></el-table-column>
		<el-table-column prop="stopTimeStr" label="停止时间" width="180" show-overflow-tooltip ></el-table-column>
		<el-table-column fixed="right" label="操作" width="100" align="center">
			<template #default="scope">
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.deleteBtnElType || 'danger'"
					size="small"
					icon="i-ep-delete"
					@click="handleDelete(scope.$index, scope.row)"
					>删除</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<Pagination
		:total="total"
		v-model:page="tableParams.pageNum"
		v-model:limit="tableParams.pageSize"
		@pagination="handleListPage"
	/>
</template>

<script setup lang="ts" name="operLog">
import type { FormInstance, FormRules } from "element-plus";
import { listPageTimedTaskLog, deleteTimedTaskLog } from "@/api/task/timedTaskLog/index";
import { useTheme } from '@/composables/useTheme';
/*============================通用任务日志开始============================*/
//表格数据
const tableData = ref([]);
interface TableParams {
	searchLike: string;
	pageNum: number;
	pageSize: number;
	orderByColumn: string;
	orderByWay: string;
}
//查询任务日志
const tableParams = ref<TableParams>({
	//查询任务日志
	searchLike: "",
	//当前页
	pageNum: 1,
	//每页显示多少条
	pageSize: 10,
	//排序字段
	orderByColumn: "",
	//排序方式
	orderByWay: "",
});
//查询表单
const searchFormRef = ref<FormInstance>();
//表单验证规则（即使用不到，为了重置方法，也需要写）
const rules = reactive<FormRules<TableParams>>({});
//总数据数量
const total = ref(0);
//总页数
const totalPage = ref(1);
//主题变量
const {themeVariables} = useTheme();
//是否加载中
const isLoading = ref(true);
/*============================通用任务日志结束============================*/

/*============================页面方法开始============================*/
//获取列表
const handleListPage = async () => {
	isLoading.value = true;
	const result = await listPageTimedTaskLog(tableParams.value);
	try {
		if (result.code == 200) {
			tableData.value = result.rows;
			tableParams.value.pageNum = result.pageNum;
			tableParams.value.pageSize = result.pageSize;
			total.value = result.total;
			totalPage.value = result.totalPage;
		} else {
			ElMessage.error(result.msg);
		}
	} finally {
		isLoading.value = false;
	}
};
//页面搜索方法
const handleQuery = async () => {
	isLoading.value = true;
	tableParams.value.pageNum = 1;
	handleListPage();
};
//重置
const handleResetQuery = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.resetFields();
};
/*********删除定时任务日志*********/
const handleDelete = async (index: number, row: any) => {
	//调用删除方法
	try {
		const result = await deleteTimedTaskLog(row.taskLogId);
		if (result.code == 200) {
			ElMessage({
				message: "删除成功",
				type: "success",
			});
			//重新加载表单
			handleQuery();
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("删除失败");
	}
};
/*********排序相关*********/
// 定义前端字段名到数据库字段名的映射
// 注意，这里只需要定义前端页面与数据库字段名不相同的场景，如数据库名为login_date,而前端页面字段名为loginDateStr
// 但是，如果仅仅是驼峰与下划线命名不同，可以不定义，如数据库为login_date，而前端页面字段名为loginDate
// 如果不同且未定义，可能会导致查询失败
const sortFieldMap = {};
const handleTableSort = (params: {
	column: any;
	prop: keyof typeof sortFieldMap; // 告诉ts，从prop中取出来的值，一定是sortFieldMap的键
	order: string;
}) => {
	// 任务日志说明：
	// - column: 当前列的配置对象
	// - prop: 排序的字段名（对应列的 prop）
	// - order: 排序方式（'ascending' 升序 / 'descending' 降序 / null 默认）

	const orderByColumn = sortFieldMap[params.prop] || params.prop;
	const orderByWay = params.order == null ? "" : params.order == "ascending" ? "asc" : "desc";

	if (orderByColumn != undefined && orderByWay != "") {
		tableParams.value.orderByColumn = orderByColumn;
		tableParams.value.orderByWay = orderByWay;
	} else {
		tableParams.value.orderByColumn = "";
		tableParams.value.orderByWay = "";
	}
	handleQuery();
};
/*============================页面方法结束============================*/

/*============================生命周期钩子开始============================*/
// 组件加载完成后执行
// 初始化表格数据
onMounted(async () => {
	handleQuery();
});
/*============================生命周期钩子结束============================*/
</script>
