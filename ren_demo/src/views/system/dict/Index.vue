<template>
	<CustomTabs v-model="activeName" :tabs="tabs" @tab-click="handleTabClick" />
	<RouterView />
</template>

<script lang="ts" setup name="dict">
const tabs = ref([
	{
		name: "dictType",
		label: "字典类型",
		url: "/system/dict/dictType"
	},
	{
		name: "dictData",
		label: "字典数据",
		url: "/system/dict/dictData"
	},
]);

const activeName = ref("dictType");

// Tab 点击切换路由
const handleTabClick = async (tab: any) => {
	await navigationService.pushPageQuery(tab.url);
};
</script>
