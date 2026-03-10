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
					placeholder="模块标题/业务类型"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="handleQuery" :class="themeVariables.searchBtnClass || 'light-blue-btn'" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" :class="themeVariables.resetBtnClass || 'pan-blue-btn'" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-table border stripe row-key="operId" :data="tableData" class="custom-table" @sort-change="handleTableSort" v-loading="isLoading">
		<el-table-column prop="title" label="模块标题" show-overflow-tooltip width="150"></el-table-column>
		<el-table-column prop="businessType" label="业务类型" width="120" sortable align="center">
			<template #default="item">
				<el-tag v-if="item.row.businessType == 0" :type="themeVariables.tableElTagType || 'info'">其它</el-tag>
				<el-tag v-if="item.row.businessType == 1" :type="themeVariables.tableElTagType || 'info'">新增</el-tag>
				<el-tag v-if="item.row.businessType == 2" :type="themeVariables.tableElTagType || 'info'">修改</el-tag>
				<el-tag v-if="item.row.businessType == 3" :type="themeVariables.tableElTagType || 'info'">删除</el-tag>
			</template>
		</el-table-column>
		<el-table-column
			prop="method"
			label="方法名称"
			width="450"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="requestMethod"
			label="请求方式"
			width="140"
			show-overflow-tooltip
			align="center"
		></el-table-column>
		<el-table-column prop="operatorType" label="操作类别" width="120" sortable align="center">
			<template #default="item">
				<el-tag v-if="item.row.operatorType == 0" :type="themeVariables.tableElTagType || 'info'">其它</el-tag>
				<el-tag v-if="item.row.operatorType == 1" :type="themeVariables.tableElTagType || 'info'">后台用户</el-tag>
				<el-tag v-if="item.row.operatorType == 2" :type="themeVariables.tableElTagType || 'info'">手机端用户</el-tag>
			</template>
		</el-table-column>
		<el-table-column
			prop="operName"
			label="操作人员"
			width="150"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="deptName"
			label="部门名称"
			width="150"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="operUrl"
			label="请求URL"
			width="300"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="operIp"
			label="主机地址"
			width="200"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="operLocation"
			label="操作地点"
			width="200"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="operParam"
			label="请求参数"
			width="300"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="jsonResult"
			label="返回参数"
			width="300"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column prop="isNormal" label="是否正常" width="120" sortable align="center">
			<template #default="item">
				<el-tag v-if="item.row.isNormal == 0" :type="themeVariables.tableElTagType || 'info'">否</el-tag>
				<el-tag v-if="item.row.isNormal == 1" :type="themeVariables.tableElTagType || 'info'">是</el-tag>
			</template>
		</el-table-column>
		<el-table-column
			prop="errorMsg"
			label="错误消息"
			width="300"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="operTimeStr"
			label="操作时间"
			width="180"
			show-overflow-tooltip
		></el-table-column>
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
import { listPageOperLog, deleteOperLog } from "@/api/system/operLog/index";
import { useTheme } from '@/composables/useTheme';
/*============================通用操作日志开始============================*/
//表格数据
const tableData = ref([]);
interface TableParams {
	searchLike: string;
	pageNum: number;
	pageSize: number;
	orderByColumn: string;
	orderByWay: string;
}
//查询操作日志
const tableParams = ref<TableParams>({
	//查询操作日志
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
// 主题变量
const { themeVariables } = useTheme();
//表格加载
const isLoading = ref(true);
/*============================通用操作日志结束============================*/

/*============================页面方法开始============================*/
//获取列表
const handleListPage = async () => {
	isLoading.value = true;
	const result = await listPageOperLog(tableParams.value);
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
/*********删除操作日志*********/
const handleDelete = async (index: number, row: any) => {
	//调用删除方法
	try {
		const result = await deleteOperLog(row.operId);
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
	// 操作日志说明：
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
