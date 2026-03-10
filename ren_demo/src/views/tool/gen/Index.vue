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
				<el-button @click="handleQuery" :class="themeVariables.addBtnElType || 'primary'" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" :class="themeVariables.addBtnElType || 'primary'" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-row :gutter="10" class="mb8">
		<el-col :span="24">
			<el-button :type="themeVariables.addBtnElType || 'primary'" plain icon="i-ep-plus" size="large" @click="openAddDialog">添加代码生成</el-button>
			<el-button :type="themeVariables.addBtnElType || 'primary'" plain icon="i-ep-plus" size="large" @click="openCreateTableDialog">创建表</el-button>
		</el-col>
	</el-row>
	<el-alert
		class="custom-alert"
		title="提示"
		type="warning"
		:closable="false"
		description="注意：当前模块生成的代码深度依赖本项目的工具类库和依赖包，且前端基于特定的自动导入配置。因此仅推荐在当前项目直接使用，如需在其他项目中使用，必须手动调整依赖引用和自动导入逻辑，并进行环境适配性验证。"
	/>
	<el-table border stripe row-key="tableId" :data="tableData" class="custom-table" @sort-change="handleTableSort" v-loading="isLoading">
		<el-table-column label="表名称" prop="tableName" :show-overflow-tooltip="true" width="300" />
		<el-table-column label="表描述" prop="tableComment" :show-overflow-tooltip="true" />
		<el-table-column label="实体" prop="className" :show-overflow-tooltip="true" width="200" />
		<el-table-column label="创建时间" prop="createTimeStr" width="180" />
		<el-table-column label="更新时间" prop="updateTimeStr" width="180" />
		<el-table-column fixed="right" label="操作" width="350" align="center">
			<template #default="scope">
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.editBtnElType || 'success'"
					size="small"
					icon="i-ep-edit"
					@click="openModifyPage(scope.$index, scope.row)"
					>修改</el-button
				>
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.deleteBtnElType || 'danger'"
					size="small"
					icon="i-ep-delete"
					@click="handleDelete(scope.$index, scope.row)"
					>删除</el-button
				>
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.refreshGenBtnElType || 'info'"
					size="small"
					icon="i-ep-refreshright"
					@click="handleRefreshGen(scope.$index, scope.row)"
					>重新同步表结构</el-button
				>
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.genCodeBtnElType || 'primary'"
					size="small"
					icon="i-ep-finished"
					@click="handleGenCode(scope.$index, scope.row)"
					>生成</el-button
				>
			</template>
		</el-table-column>
	</el-table>
	<Pagination
		:total="total"
		v-model:page="tableParams.pageNum"
		v-model:limit="tableParams.pageSize"
		@pagination="handlePageList"
	/>

	<el-dialog
		title="添加代码生成"
		v-model="dialogFormAddGen"
		width="1100px"
	>
		<GenAdd ref="genAddRef" :refreshList="handleRefreshList" />
	</el-dialog>

	<el-dialog
			title="创建表并导入"
			v-model="dialogFormCreateTable"
			width="800px"
		>
		<el-form
			:model="createTableForm"
			:rules="createTableFormRules"
			ref="createTableFormRef"
		>
			<el-form-item label="建表语句" label-width="80px" prop="sqlStr">
				<el-input v-model="createTableForm.sqlStr" type="textarea" autocomplete="off" placeholder="请输入建表语句" :rows="13"/>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="closeCreateTableDialog">取 消</el-button>
				<el-button type="primary" @click="handleCreateTable(createTableFormRef)">
					确 定
				</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="gen">
import type { FormInstance, FormRules } from "element-plus";
import { listPageGen, deleteGen, createTable, refreshGen, genCode } from "@/api/tool/gen/index";
import GenAdd from "@/views/tool/gen/GenAdd.vue";
import { useTheme } from '@/composables/useTheme';
/*============================通用参数开始============================*/
//表格数据
const tableData = ref([]);
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
// 主题变量
const { themeVariables } = useTheme();
//是否加载中
const isLoading = ref(true);
/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
//获取列表
const handlePageList = async () => {
	isLoading.value = true;
	const result = await listPageGen(tableParams.value);
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
	handlePageList();
};
//重置
const handleResetQuery = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.resetFields();
};
/*********删除代码生成*********/
const handleDelete = async (index: number, row: any) => {
	//调用删除方法
	try {
		const result = await deleteGen({ tableIds: row.tableId });
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
/*********重新同步表结构*********/
const handleRefreshGen = async (index: number, row: any) => {
	//调用重新同步方法
	try {
		const result = await refreshGen({ tableId: row.tableId });
		if (result.code == 200) {
			ElMessage({
				message: "重新同步成功",
				type: "success",
			});
			//重新加载表单
			handleQuery();
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("重新同步失败");
	}
};
/*********生成*********/
const handleGenCode = async (index?:number, row?:any) => {
	//调用生成方法
	try {
		const tableIds = row.tableId;
		if (tableIds == "") {
			ElMessage({
				message: "请选择要生成的数据",
				type: "success",
			});
			return
		}
		if(row.genType === "1"){
			const result = await genCode({ tableIds });
			if (result.code == 200) {
				ElMessage({
					message: "生成成功",
					type: "success",
				});
			} else {
				ElMessage.error(result.msg);
			}
		}else{
			await downloadUtils.zip("/gen/genCodeZip","ren.zip",{ tableIds })
		}
		//重新加载表单
		handleQuery();
	} catch {
		ElMessage.error("生成失败");
	}
};
/*********添加修改代码生成*********/
//添加代码生成弹窗是否显示
const dialogFormAddGen = ref(false);
//打开添加弹框
const openAddDialog = async () => {
	dialogFormAddGen.value = true;
};
//添加完成后的列表刷新（提供给子页面调用，子页面添加完成后，关闭弹窗，刷新列表）
const handleRefreshList = async () => {
	//关闭弹窗
	dialogFormAddGen.value = false;
	//刷新列表
	handleQuery();
};
//打开修改弹框
const openModifyPage = async (index: number, row: any) => {
	const tableId = row.tableId;
	tabComposable.openPageQuery("/tool/gen/modify", "编辑代码生成", { tableId });
};

/*********创建表*********/
//弹出框是否显示
const dialogFormCreateTable = ref(false);
//创建表表单初始值
const initialCreateTableForm = {
	//备注
	sqlStr: "",
};
//创建表表单对象
const createTableForm = ref({ ...initialCreateTableForm });
//创建表表单对象
const createTableFormRef = ref<FormInstance>();
//创建表表单验证规则
const createTableFormRules = reactive<FormRules>({
	sqlStr: [{ required: true, message: "请填写建表语句", trigger: "blur" }],
});
//打开创建表弹框
const openCreateTableDialog = () => {
	//表单值恢复为初始值
	createTableForm.value = { ...initialCreateTableForm };
	//清除验证状态
	createTableFormRef.value?.clearValidate();
	//弹出框显示
	dialogFormCreateTable.value = true;
};
//创建表表单提交方法
const handleCreateTable = async (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	await formEl.validate(async (valid) => {
		if (valid) {
			const result = await createTable(createTableForm.value);
			if (result.code == 200) {
				ElMessage({
					message: "创建成功",
					type: "success",
				});
				//关闭弹框
				dialogFormCreateTable.value = false;
				//表单值恢复为初始值
				createTableForm.value = { ...initialCreateTableForm };
				//重新加载表单
				handleQuery();
			} else {
				ElMessage.error(result.msg);
			}
		}
	});
};
//创建表表单取消方法
const closeCreateTableDialog = () => {
	dialogFormCreateTable.value = false;
	createTableForm.value = { ...initialCreateTableForm };
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
