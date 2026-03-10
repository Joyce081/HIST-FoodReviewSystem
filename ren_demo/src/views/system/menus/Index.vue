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
					placeholder="菜单名称/路由名称/权限"
					autocomplete="off"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="handleQuery" :class="themeVariables.searchBtnClass || 'light-blue-btn'" icon="i-ep-search">搜索</el-button>
				<el-button @click="handleResetQuery(searchFormRef)" :class="themeVariables.resetBtnClass || 'pan-blue-btn'" icon="i-ep-refresh">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
	<el-row :gutter="10" class="mb8">
		<el-col :span="24">
			<el-button :type="themeVariables.addBtnElType || 'primary'" plain icon="i-ep-plus" size="large" @click="openAddDialog">添加菜单</el-button>
		</el-col>
	</el-row>

	<el-table
		:data="tableData"
		row-key="menuId"
		:border="true"
		stripe
		default-expand-all
		:default-sort="{ prop: 'orderNum', order: 'ascending' }"
		:tree-props="{ children: 'children' }"
		class="custom-table"
		v-loading="isLoading"
	>
		<el-table-column prop="menuName" label="菜单名称" sortable width="180" />
		<el-table-column prop="icon" label="菜单图标" sortable width="120" :align="'center'">
			<template #default="item">
				<!-- 使用自定义动态绑定组件，可以判断组件是否存在，防止报错 -->
				<SafeDynamicComponent :componentName="item.row.icon" />
			</template>
		</el-table-column>
		<el-table-column prop="orderNum" label="排序" sortable width="120" align="center" />
		<el-table-column prop="perms" label="权限标识" sortable />
		<el-table-column prop="routeName" label="路由名称" sortable width="180" />
		<el-table-column prop="path" label="路由地址" sortable width="200" />
		<el-table-column prop="component" label="组件路径" sortable />
		<el-table-column prop="isStop" label="是否停用" sortable width="120" align="center">
			<template #default="item">
				<el-tag v-if="item.row.isStop == 0" :type="themeVariables.tableElTagType || 'info'">否</el-tag>
				<el-tag v-if="item.row.isStop == 1" :type="themeVariables.tableElTagType || 'info'">是</el-tag>
			</template>
		</el-table-column>
		<el-table-column prop="createTimeStr" label="创建时间" sortable width="180" />
		<el-table-column fixed="right" label="操作" width="280" align="center">
			<template #default="scope">
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.addBtnElType || 'primary'"
					size="small"
					icon="i-ep-plus"
					@click="openAddOrModifySubMenuDialog(scope.$index, scope.row)"
					>添加子级菜单</el-button
				>
				<el-button
					text
					class="compact-btn"
					:type="themeVariables.editBtnElType || 'success'"
					size="small"
					icon="i-ep-edit"
					@click="openModifyDialog(scope.$index, scope.row)"
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
			</template>
		</el-table-column>
	</el-table>
	<el-dialog
		:title="addOrModifyTag == 1 ? '添加菜单' : '修改菜单'"
		v-model="dialogFormAddOrModifyMenu"
		width="700px"
	>
		<el-form
			:model="addOrModifyMenuForm"
			:rules="addOrModifyMenuFormRules"
			ref="addOrModifyMenuFormRef"
			:inline="true"
			label-width="80px"
		>
			<el-form-item label="上级菜单" prop="parentId" class="bigLine">
				<el-tree-select
					v-model="addOrModifyMenuForm.parentId"
					:data="parentMenuList"
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
					show-checkbox
				/>
			</el-form-item>
			<el-form-item label="菜单类型" prop="menuType" class="bigLine">
				<el-radio-group v-model="addOrModifyMenuForm.menuType" size="large" fill="#6cf">
					<el-radio-button label="目录" value="M" />
					<el-radio-button label="菜单" value="C" />
					<el-radio-button label="按钮" value="F" />
				</el-radio-group>
			</el-form-item>
			<el-form-item label="菜单图标" prop="icon" class="bigLine" v-if="addOrModifyMenuForm.menuType == 'M' || addOrModifyMenuForm.menuType == 'C'">
				<IconPicker v-model="addOrModifyMenuForm.icon" />
			</el-form-item>
			<el-form-item label="菜单名称" prop="menuName" class="smallLine">
				<el-input v-model="addOrModifyMenuForm.menuName" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="显示顺序" prop="orderNum" class="smallLine">
				<el-input v-model="addOrModifyMenuForm.orderNum" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="路由名称" prop="routeName" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'M' || addOrModifyMenuForm.menuType == 'C'">
				<el-input v-model="addOrModifyMenuForm.routeName" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="路由地址" prop="path" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'M' || addOrModifyMenuForm.menuType == 'C'">
				<el-input v-model="addOrModifyMenuForm.path" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="是否外链" prop="isFrame" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'M' || addOrModifyMenuForm.menuType == 'C'">
				<el-radio-group v-model="addOrModifyMenuForm.isFrame" size="large" fill="#6cf">
					<el-radio-button label="否" :value="0" />
					<el-radio-button label="是" :value="1" />
				</el-radio-group>
			</el-form-item>
			<el-form-item label="组件路径" prop="component" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'C'">
				<el-input v-model="addOrModifyMenuForm.component" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="权限字符" prop="perms" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'C' || addOrModifyMenuForm.menuType == 'F'">
				<el-input v-model="addOrModifyMenuForm.perms" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="路由参数" prop="query" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'C'">
				<el-input v-model="addOrModifyMenuForm.query" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="是否缓存" prop="isCache" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'C'">
				<el-radio-group v-model="addOrModifyMenuForm.isCache" size="large" fill="#6cf">
					<el-radio-button label="否" :value="0" />
					<el-radio-button label="是" :value="1" />
				</el-radio-group>
			</el-form-item>
			<el-form-item label="是否隐藏" prop="isVisible" class="smallLine" v-if="addOrModifyMenuForm.menuType == 'M' || addOrModifyMenuForm.menuType == 'C'">
				<el-radio-group v-model="addOrModifyMenuForm.isVisible" size="large" fill="#6cf">
					<el-radio-button label="否" :value="0" />
					<el-radio-button label="是" :value="1" />
				</el-radio-group>
			</el-form-item>
			<el-form-item label="是否停用" prop="isStop" class="smallLine">
				<el-radio-group v-model="addOrModifyMenuForm.isStop" size="large" fill="#6cf">
					<el-radio-button label="否" :value="0" />
					<el-radio-button label="是" :value="1" />
				</el-radio-group>
			</el-form-item>
			<el-form-item label="备注" prop="remark" class="bigLine">
				<el-input v-model="addOrModifyMenuForm.remark" autocomplete="off"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="closeAddOrModifyDialog">取 消</el-button>
				<el-button type="primary" @click="handleAddOrModify(addOrModifyMenuFormRef)">
					确 定
				</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="menus">
import type { FormInstance, FormRules } from "element-plus";
import {
	listTreeMenu,
	addMenu,
	modifyMenu,
	deleteMenu,
	getDoesNotContainSelfMenuTreeSelect,
} from "@/api/system/menu/index";
import { useTheme } from '@/composables/useTheme';
/*============================通用参数开始============================*/
//表格数据
const tableData = ref<[]>([]);
interface TableParams {
	searchLike: string;
}
//查询参数
const tableParams = ref<TableParams>({
	//查询参数
	searchLike: "",
});
//查询表单
const searchFormRef = ref<FormInstance>();
//表单验证规则（即使用不到，为了重置方法，也需要写）
const rules = reactive<FormRules<TableParams>>({});
//添加修改菜单下拉框树形列表
const parentMenuList = ref();
//添加或修改(1添加，2修改)
const addOrModifyTag = ref(1);
// 主题变量
const { themeVariables } = useTheme();
//是否加载中
const isLoading = ref(true);
/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
//获取列表方法
const handleListTree = async () => {
	isLoading.value = true;
	const result = await listTreeMenu(tableParams.value);
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
	handleListTree();
};
//重置
const handleResetQuery = (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	formEl.resetFields();
};
/*********添加或修改菜单*********/
//弹出框是否显示
const dialogFormAddOrModifyMenu = ref(false);
//添加菜单表单初始值
const initialAddOrModifyMenuForm = {
	//主键
	menuId: 0,
	//上级菜单标识
	parentId: -1,
	//菜单类型
	menuType: "M",
	//菜单图标
	icon: "",
	//菜单名称
	menuName: "",
	//显示顺序
	orderNum: "",
	//是否外链
	isFrame: 0,
	//路由名称
	routeName: "",
	//路由地址
	path: "",
	//组件路径
	component: "",
	//权限字符
	perms: "",
	//路由参数
	query: "",
	//是否缓存
	isCache: 0,
	//是否隐藏
	isVisible: 0,
	//是否停用
	isStop: 0,
	//备注
	remark: "",
};
//添加菜单表单对象
const addOrModifyMenuForm = ref({ ...initialAddOrModifyMenuForm });
//添加菜单表单对象
const addOrModifyMenuFormRef = ref<FormInstance>();
//添加菜单表单验证规则
const addOrModifyMenuFormRules = reactive<FormRules>({
	parentId: [
		{ pattern: /^(?!-1$|null$).*/, message: "请选择上级菜单", trigger: "blur" },
		{ required: true, message: "请选择上级菜单", trigger: "blur" },
	],
	menuName: [{ required: true, message: "请填写菜单名称", trigger: "blur" }],
	orderNum: [
		{ required: true, message: "请填写显示顺序", trigger: "blur" },
		{
			pattern: /^[1-9]\d*$/,
			message: "只能输入正整数",
			trigger: ["blur", "change"],
		},
	],
	path: [{ required: true, message: "请填写路由地址", trigger: "blur" }],
	routeName: [{ required: true, message: "请填写路由名称", trigger: "blur" }],
});
//打开添加弹框
const openAddDialog = async () => {
	//表单值恢复为初始值
	addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
	//清除验证状态
	addOrModifyMenuFormRef.value?.clearValidate();
	//获取上级菜单列表
	try {
		const result = await getDoesNotContainSelfMenuTreeSelect();
		if (result.code == 200) {
			parentMenuList.value = result.data;
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("获取上级菜单列表失败");
	}
	addOrModifyTag.value = 1;
	dialogFormAddOrModifyMenu.value = true;
};
//打开修改弹框
const openModifyDialog = async (index: number, row: any) => {
	//表单值恢复为初始值
	addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
	//清除验证状态
	addOrModifyMenuFormRef.value?.clearValidate();
	//获取上级菜单列表
	try {
		const result = await getDoesNotContainSelfMenuTreeSelect(row.menuId);
		if (result.code == 200) {
			parentMenuList.value = result.data;

			// 等待树形组件渲染完成
			await nextTick();

			// 设置默认选中值
			addOrModifyMenuForm.value.parentId = row.parentId;
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("获取上级菜单列表失败");
	}
	addOrModifyTag.value = 2;

	addOrModifyMenuForm.value.menuId = row.menuId;
	addOrModifyMenuForm.value.menuType = row.menuType;
	addOrModifyMenuForm.value.icon = row.icon;
	addOrModifyMenuForm.value.menuName = row.menuName;
	addOrModifyMenuForm.value.orderNum = row.orderNum;
	addOrModifyMenuForm.value.isFrame = row.isFrame;
	addOrModifyMenuForm.value.routeName = row.routeName;
	addOrModifyMenuForm.value.path = row.path;
	addOrModifyMenuForm.value.component = row.component;
	addOrModifyMenuForm.value.perms = row.perms;
	addOrModifyMenuForm.value.query = row.query;
	addOrModifyMenuForm.value.isCache = row.isCache;
	addOrModifyMenuForm.value.isVisible = row.isVisible;
	addOrModifyMenuForm.value.isStop = row.isStop;
	addOrModifyMenuForm.value.remark = row.remark;

	dialogFormAddOrModifyMenu.value = true;
};
//打开添加子级菜单弹框
const openAddOrModifySubMenuDialog = async (index: number, row: any) => {
	//表单值恢复为初始值
	addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
	//清除验证状态
	addOrModifyMenuFormRef.value?.clearValidate();
	//获取上级菜单列表
	try {
		const result = await getDoesNotContainSelfMenuTreeSelect();
		if (result.code == 200) {
			parentMenuList.value = result.data;
			// 等待树形组件渲染完成
			await nextTick();
			// 设置默认选中值
			addOrModifyMenuForm.value.parentId = row.menuId;
		} else {
			ElMessage.error(result.msg);
		}
	} catch {
		ElMessage.error("获取上级菜单列表失败");
	}
	addOrModifyTag.value = 1;
	dialogFormAddOrModifyMenu.value = true;
};
//实现添加
const handleAddOrModify = async (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	await formEl.validate(async (valid) => {
		if (valid) {
			//调用添加或修改方法
			if (addOrModifyTag.value == 1) {
				const result = await addMenu(addOrModifyMenuForm.value);
				if (result.code == 200) {
					ElMessage({
						message: "添加成功",
						type: "success",
					});
					//关闭弹框
					dialogFormAddOrModifyMenu.value = false;
					//表单值恢复为初始值
					addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
					//重新加载表单
					handleQuery();
				} else {
					ElMessage.error(result.msg);
				}
			} else {
				const result = await modifyMenu(addOrModifyMenuForm.value);
				if (result.code == 200) {
					ElMessage({
						message: "修改成功",
						type: "success",
					});
					//关闭弹框
					dialogFormAddOrModifyMenu.value = false;
					//表单值恢复为初始值
					addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
					//重新加载表单
					handleQuery();
				} else {
					ElMessage.error(result.msg);
				}
			}
		}
	});
};
//取消弹框
const closeAddOrModifyDialog = async () => {
	//关闭弹框
	dialogFormAddOrModifyMenu.value = false;
	//表单值恢复为初始值
	addOrModifyMenuForm.value = { ...initialAddOrModifyMenuForm };
};
/*********删除菜单*********/
const handleDelete = async (index: number, row: any) => {
	//调用删除方法
	try {
		const result = await deleteMenu(row.menuId);
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
/*============================页面方法结束============================*/

/*============================生命周期钩子开始============================*/
// 组件加载完成后执行
// 初始化表格数据
onMounted(async () => {
	handleQuery();
});
/*============================生命周期钩子结束============================*/
</script>
