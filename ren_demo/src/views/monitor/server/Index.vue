<template>
	<div v-loading="loading">
		<el-row :gutter="20">
			<el-col :span="12">
				<el-card class="box-card">
					<template #header>
						<div class="card-header">
							<el-icon :size="20" style="margin-right: 10px"><i-ep-cpu /></el-icon>
							<span>CPU</span>
						</div>
					</template>
					<el-descriptions :column="1" :border="true">
						<el-descriptions-item label="核心数" label-class-name="labelClass"
							><span class="content-text">{{
								cpuInfo.cpuNum
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="用户使用率" label-class-name="labelClass"
							><span class="content-text">{{
								cpuInfo.sysUsed
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="系统使用率" label-class-name="labelClass"
							><span class="content-text">{{
								cpuInfo.sysUsed
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="当前空闲率" label-class-name="labelClass"
							><span class="content-text">{{
								cpuInfo.free
							}}</span></el-descriptions-item
						>
					</el-descriptions>
				</el-card>
			</el-col>
			<el-col :span="12">
				<el-card class="box-card">
					<template #header>
						<div class="card-header">
							<el-icon :size="20" style="margin-right: 10px"
								><i-ep-tickets
							/></el-icon>
							<span>内存</span>
						</div>
					</template>
					<el-descriptions :column="2" :border="true">
						<el-descriptions-item label="服务器总内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.total
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="服务器已用内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.used
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="服务器剩余内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.available
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="服务器内存使用率" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.occupancyRate
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="JVM总内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.jvmTotal
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="JVM已用内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.jvmUsed
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="JVM剩余内存" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.jvmAvailable
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="JVM内存使用率" label-class-name="labelClass"
							><span class="content-text">{{
								memoryInfo.jvmOccupancyRate
							}}</span></el-descriptions-item
						>
					</el-descriptions>
				</el-card>
			</el-col>
		</el-row>
		<el-row>
			<el-col :span="24">
				<el-card class="box-card">
					<template #header>
						<div class="card-header">
							<el-icon :size="20" style="margin-right: 10px"
								><i-ep-monitor
							/></el-icon>
							<span>服务器信息</span>
						</div>
					</template>
					<el-descriptions :column="2" :border="true">
						<el-descriptions-item label="服务器名称" label-class-name="labelClass"
							><span class="content-text">{{
								computerSystemInfo.computerName
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="操作系统" label-class-name="labelClass"
							><span class="content-text">{{
								computerSystemInfo.family
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="服务器IP" label-class-name="labelClass"
							><span class="content-text">{{
								computerSystemInfo.computerIp
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="系统架构" label-class-name="labelClass"
							><span class="content-text">{{
								computerSystemInfo.osArch
							}}</span></el-descriptions-item
						>
					</el-descriptions>
				</el-card>
			</el-col>
		</el-row>
		<el-row>
			<el-col :span="24">
				<el-card class="box-card">
					<template #header>
						<div class="card-header">
							<el-icon :size="20" style="margin-right: 10px"
								><i-ep-coffeecup
							/></el-icon>
							<span>JAVA虚拟机信息</span>
						</div>
					</template>
					<el-descriptions :column="2" :border="true" class="custom-descriptions">
						<el-descriptions-item label="JAVA名称" label-class-name="labelClass"
							><span class="content-text">{{
								jvmInfo.jvmName
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="JAVA版本" label-class-name="labelClass"
							><span class="content-text">{{
								javaInfo.javaVersion
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="启动时间" label-class-name="labelClass"
							><span class="content-text">{{
								projectInfo.startTimeStr
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="运行时长" label-class-name="labelClass"
							><span class="content-text">{{
								projectInfo.runningTimeStr
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="安装路径" label-class-name="labelClass"
							><span class="content-text">{{
								javaInfo.javaHome
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="项目路径" label-class-name="labelClass"
							><span class="content-text">{{
								projectInfo.projectDir
							}}</span></el-descriptions-item
						>
						<el-descriptions-item label="运行参数" label-class-name="labelClass"
							><span class="long-content">{{
								jvmInfo.inputArgs
							}}</span></el-descriptions-item
						>
					</el-descriptions>
				</el-card>
			</el-col>
		</el-row>
		<el-row>
			<el-col :span="24">
				<el-card class="box-card">
					<template #header>
						<div class="card-header">
							<el-icon :size="20" style="margin-right: 10px"><i-ep-folder /></el-icon>
							<span>磁盘状态</span>
						</div>
					</template>
					<el-table :data="diskInfoList" :style="{width: '100%'}">
						<el-table-column prop="dirName" label="盘符路径" />
						<el-table-column prop="sysTypeName" label="文件系统" />
						<el-table-column prop="typeName" label="盘符类型" />
						<el-table-column prop="total" label="总大小" />
						<el-table-column prop="free" label="可用大小" />
						<el-table-column prop="used" label="已用大小" />
						<el-table-column prop="usage" label="已用百分比" />
					</el-table>
				</el-card>
			</el-col>
		</el-row>
	</div>
</template>

<script setup lang="ts" name="sysUserOnline">
import { getSeverInfo } from "@/api/monitor/server/index";
import type {
	ComputerSystemInfoBO,
	CpuInfoBO,
	DiskInfoBO,
	JavaInfoBO,
	JvmInfoBO,
	MemoryInfoBO,
	ProjectInfoBO,
} from "@/types/ServerInfo";
import "@/assets/css/views/monitor/serverIndex.scss";
/*============================通用参数开始============================*/
// Cpu相关信息
const cpuInfo = ref<CpuInfoBO>({
	cpuNum: 0,
	cpuModel: "",
	used: "",
	waitUsed: "",
	userUsed: "",
	sysUsed: "",
	free: "",
});
// 内存信息
const memoryInfo = ref<MemoryInfoBO>({
	total: "",
	available: "",
	used: "",
	occupancyRate: "",
	jvmTotal: "",
	jvmAvailable: "",
	jvmUsed: "",
	jvmOccupancyRate: "",
	jvmMax: "",
});
// 服务器信息
const computerSystemInfo = ref<ComputerSystemInfoBO>({
	manufacturer: "",
	model: "",
	serialNumber: "",
	family: "",
	osManufacturer: "",
	version: "",
	bitness: 0,
	systemUptime: 0,
	processCount: 0,
	computerName: "",
	computerIp: "",
	osArch: "",
});
// Java虚拟机信息
const jvmInfo = ref<JvmInfoBO>({
	jvmName: "",
	jvmVersion: "",
	jvmVendor: "",
	jvmInfoStr: "",
	jvmSpecName: "",
	jvmSpecVersion: "",
	jvmSpecVendor: "",
	inputArgs: "",
});
// Java信息
const javaInfo = ref<JavaInfoBO>({
	javaVersion: "",
	javaVersionInt: 0,
	javaVendor: "",
	runtimeName: "",
	runtimeVersion: "",
	javaHome: "",
	javaSpecName: "",
	javaSpecVersion: "",
	javaSpecVendor: "",
});
// 项目信息
const projectInfo = ref<ProjectInfoBO>({
	projectDir: "",
	startTimeStr: "",
	runningTimeStr: "",
});
// 磁盘信息
const diskInfoList = ref<DiskInfoBO[]>([]);
//加载中
const loading = ref(true);
/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
//获取服务监控信息
const handleGetSeverInfo = async () => {
	const result = await getSeverInfo();
	if (result.code == 200) {
		cpuInfo.value = result.cpuInfo;
		memoryInfo.value = result.memoryInfo;
		computerSystemInfo.value = result.computerSystemInfo;
		jvmInfo.value = result.jvmInfo;
		javaInfo.value = result.javaInfo;
		projectInfo.value = result.projectInfo;
		diskInfoList.value = result.diskInfoList;
		loading.value = false;
	} else {
		ElMessage.error(result.msg);
	}
};
/*============================页面方法结束============================*/

/*============================生命周期钩子开始============================*/
// 组件加载完成后执行
// 初始格数据
onMounted(async () => {
	handleGetSeverInfo();
});
/*============================生命周期钩子结束============================*/
</script>
