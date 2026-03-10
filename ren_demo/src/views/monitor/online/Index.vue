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
			<el-form-item label="登陆地址" prop="ipaddr">
				<el-input
					v-model="tableParams.ipaddr"
					placeholder="登陆地址"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item label="用户名称" prop="userName">
				<el-input
					v-model="tableParams.userName"
					placeholder="用户名称"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="handleQuery" :class="themeVariables.searchBtnClass || 'light-blue-btn'" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" :class="themeVariables.resetBtnClass || 'pan-blue-btn'" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-table border stripe row-key="tokenId" :data="paginatedData" class="custom-table" v-loading="isLoading">
		<el-table-column
			prop="deptName"
			label="部门名称"
			width="150"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="userName"
			label="用户名称"
			width="150"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="ipaddr"
			label="登录IP地址"
			width="200"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="loginLocation"
			label="登录地址"
			width="200"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="browser"
			label="浏览器类型"
			width="200"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="os"
			label="操作系统"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column
			prop="loginTime"
			label="登录时间"
			width="180"
			show-overflow-tooltip
		></el-table-column>
		<el-table-column fixed="right" label="操作" width="100" align="center">
			<template #default="scope">
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.compulsoryWithdrawalBtnElType || 'warning'"
					size="small"
					icon="i-ep-close"
					@click="handleCompulsoryWithdrawal(scope.$index, scope.row)"
					>强退</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<Pagination :total="tableData.length" v-model:page="pageNum" v-model:limit="pageSize" />
</template>

<script setup lang="ts" name="sysUserOnline">
import type { FormInstance, FormRules } from "element-plus";
import {
	listSysUserOnline,
	compulsoryWithdrawal,
} from "@/api/monitor/online/index";
import { useTheme } from '@/composables/useTheme';
/*============================通用参数开始============================*/
//表格数据
const tableData = ref([]);
interface TableParams {
	ipaddr: string;
	userName: string;
}
//查询参数
const tableParams = ref<TableParams>({
	//登陆地址
	ipaddr: "",
	//用户名称
	userName: "",
});
//查询表单
const searchFormRef = ref<FormInstance>();
//表单验证规则（即使用不到，为了重置方法，也需要写）
const rules = reactive<FormRules<TableParams>>({});
//当前页
const pageNum = ref(1);
//每页显示多少条
const pageSize = ref(10);
//主题变量
const {themeVariables} = useTheme();
//是否加载中
const isLoading = ref(true);
/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
//获取列表
const handleListSysUserOnline = async () => {
	isLoading.value = true;
	const result = await listSysUserOnline(tableParams.value);
	try {
		if (result.code == 200) {
			tableData.value = result.data;
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
	pageNum.value = 1;
	handleListSysUserOnline();
};
//重置
const handleResetQuery = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.resetFields();
};
/*********强退在线用户*********/
const handleCompulsoryWithdrawal = async (index: number, row: any) => {
	//调用强退方法
	try {
		const result = await compulsoryWithdrawal(row.tokenId);
		if (result.code == 200) {
			ElMessage({
				message: "操作成功",
				type: "success",
			});
			//重新加载表单
			handleQuery();
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("操作失败");
	}
};
/*============================页面方法结束============================*/

/*============================计算属性开始============================*/
// 核心计算属性：计算当前页数据
const paginatedData = computed(() => {
	const start = (pageNum.value - 1) * pageSize.value;
	const end = start + pageSize.value;
	return tableData.value.slice(start, end);
});
/*============================计算属性结束============================*/

/*============================生命周期钩子开始============================*/
// 组件加载完成后执行
// 初始化表格数据
onMounted(async () => {
	handleQuery();
});
/*============================生命周期钩子结束============================*/
</script>
