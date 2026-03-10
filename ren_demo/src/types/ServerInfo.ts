export interface ComputerSystemInfoBO {
	manufacturer: string;
	model: string;
	serialNumber: string;
	family: string;
	osManufacturer: string;
	version: string;
	bitness: number;
	systemUptime: number;
	processCount: number;
	computerName: string;
	computerIp: string;
	osArch: string;
}

export interface CpuInfoBO {
	cpuNum: number;
	cpuModel: string;
	used: string;
	waitUsed: string;
	userUsed: string;
	sysUsed: string;
	free: string;
}

export interface DiskInfoBO {
	dirName: string;
	sysTypeName: string;
	typeName: string;
	total: string;
	free: string;
	used: string;
	usage: string;
}

export interface JavaInfoBO {
	javaVersion: string;
	javaVersionInt: number;
	javaVendor: string;
	runtimeName: string;
	runtimeVersion: string;
	javaHome: string;
	javaSpecName: string;
	javaSpecVersion: string;
	javaSpecVendor: string;
}

export interface JvmInfoBO {
	jvmName: string;
	jvmVersion: string;
	jvmVendor: string;
	jvmInfoStr: string;
	jvmSpecName: string;
	jvmSpecVersion: string;
	jvmSpecVendor: string;
	inputArgs: string;
}

export interface MemoryInfoBO {
	total: string;
	available: string;
	used: string;
	occupancyRate: string;
	jvmTotal: string;
	jvmAvailable: string;
	jvmUsed: string;
	jvmOccupancyRate: string;
	jvmMax: string;
}

export interface NetWorkInfoBO {
	name: string;
	displayName: string;
	macaddr: string;
	iPv4addr: string;
	iPv6addr: string;
	bytesSent: number;
	bytesRecv: number;
	packetsSent: number;
	packetsRecv: number;
}

export interface ProjectInfoBO {
	projectDir: string;
	startTimeStr: string;
	runningTimeStr: string;
}
