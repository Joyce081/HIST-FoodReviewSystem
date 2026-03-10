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
					placeholder="表名"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="handleQuery" class="light-blue-btn" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" class="pan-blue-btn" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-table border stripe row-key="tableId" ref="tableRef" :data="tableData" class="custom-table" @sort-change="tableSortHandle" @selection-change="handleSelectionChange" v-loading="isLoading">
		<el-table-column type="selection" width="50" align="center" />
		<el-table-column label="表名称" prop="tableName" :show-overflow-tooltip="true" width="200" />
		<el-table-column label="表描述" prop="tableComment" :show-overflow-tooltip="true" />
		<el-table-column label="创建时间" align="center" prop="createTimeStr" width="180" />
		<el-table-column label="更新时间" align="center" prop="updateTimeStr" width="180" />
	</el-table>
	<Pagination
		:total="total"
		v-model:page="tableParams.pageNum"
		v-model:limit="tableParams.pageSize"
		@pagination="getList"
	/>

	<div class="dialog-footer" style="text-align: right;">
		<el-button @click="addGenCancel">取 消</el-button>
		<el-button type="primary" @click="addGenConfirm">
			确 定
		</el-button>
	</div>
</template>

<script setup lang="ts" name="gen">
import type { FormInstance, FormRules } from "element-plus";
import { addGen,getGenListForDataBase } from "@/api/tool/gen/index";
/*============================通用参数开始============================*/
//表格数据
const tableData = ref<addGenTable[]>([]);
interface TableParams {
	searchLike: string;
	pageNum: number;
	pageSize: number;
	orderByColumn: string;
	orderByWay: string;
}
//查询参数
const tableParams = ref<TableParams>({
	//查询参数
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
//从父页面接收数据
const props = defineProps(['refreshList']);
//表格
interface addGenTable {
	tableName: string;
	tableComment: string;
	createTimeStr: string;
	updateTimeStr: string;
}
//是否加载中
const isLoading = ref(true);
/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
//获取列表
const getList = async () => {
	isLoading.value = true;
	const result = await getGenListForDataBase(tableParams.value);
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
	getList();
};
//重置
const handleResetQuery = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.resetFields();
};
//监听表格选中变化
const tableRef = ref();
const addTableList = ref<addGenTable[]>([]);
const handleSelectionChange = (val: addGenTable[]) => {
	addTableList.value = val;
}
//添加完成后的列表刷新
//添加取消
const addGenCancel = () => {
	props.refreshList();
};
//添加确认
const addGenConfirm = async () => {
	if (addTableList.value.length == 0) {
		ElMessage.error("请选择要添加的表");
		return;
	}
	const result = await addGen(addTableList.value);
	if (result.code == 200) {
		ElMessage({
			message: "添加成功",
			type: "success",
		});
		props.refreshList();
	} else {
		ElMessage.error(result.msg);
	}
};
/*********排序相关*********/
// 定义前端字段名到数据库字段名的映射
// 注意，这里只需要定义前端页面与数据库字段名不相同的场景，如数据库名为login_date,而前端页面字段名为loginDateStr
// 但是，如果仅仅是驼峰与下划线命名不同，可以不定义，如数据库为login_date，而前端页面字段名为loginDate
// 如果不同且未定义，可能会导致查询失败
const sortFieldMap = {};
const tableSortHandle = (params: {
	column: any;
	prop: keyof typeof sortFieldMap; // 告诉ts，从prop中取出来的值，一定是sortFieldMap的键
	order: string;
}) => {
	// 参数说明：
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
