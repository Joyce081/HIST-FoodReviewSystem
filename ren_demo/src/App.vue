<template>
	<div id="app">
		<!-- 这里是所有页面的公共布局 -->
		<router-view />
	</div>
</template>

<script setup lang="ts">
	/*============================================Service Worker相关，主要给Swagger使用================================================*/
	import { initServiceWorkerComm } from '@/utils/serviceWorkerHandler';

	// 初始化Service Worker通信
	const cleanup = initServiceWorkerComm();

	// 组件卸载时清理（可选）
	onBeforeUnmount(() => {
		// 如果handler返回了清理函数
		cleanup?.();
	});
</script>
