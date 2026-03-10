<template>
	<el-form size="small">
		<el-form-item>
			<el-radio v-model="radioValue" label="日，允许的通配符[, - * ? / L W]" :value="1" />
		</el-form-item>

		<el-form-item>
			<el-radio v-model="radioValue" label="不指定" :value="2" />
		</el-form-item>

		<el-form-item>
			<el-radio v-model="radioValue" label="周期从" :value="3" />
			<el-input-number v-model="cycle01" :min="1" :max="30" /> -
			<el-input-number v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 2" :max="31" /> 日
		</el-form-item>

		<el-form-item>
			<el-radio v-model="radioValue" label="从" :value="4" />
			<el-input-number v-model="average01" :min="1" :max="30" /> 号开始，每
			<el-input-number v-model="average02" :min="1" :max="31 - average01 || 1" />
			日执行一次
		</el-form-item>

		<el-form-item>
			<el-radio v-model="radioValue" label="每月" :value="5" />
			<el-input-number v-model="workday" :min="1" :max="31" /> 号最近的那个工作日
		</el-form-item>

		<el-form-item>
			<el-radio v-model="radioValue" label="本月最后一天" :value="6" />
		</el-form-item>

		<el-form-item>
			<el-row :gutter="10" style="width: 100%">
				<el-col :span="2">
					<el-radio v-model="radioValue" label="指定" :value="7" />
				</el-col>
				<el-col :span="22">
					<el-select
						clearable
						v-model="checkboxList"
						placeholder="可多选"
						multiple
						style="width: 100%"
					>
						<el-option v-for="item in 31" :key="item" :value="item">{{ item }}</el-option>
					</el-select>
				</el-col>
			</el-row>
		</el-form-item>
	</el-form>
</template>

<script setup lang="ts" name="crontab-day">
interface CronItem {
	day?: string;
	week?: string;
}
interface CronDayProps {
	check: (value: number, min: number, max: number, ...args: any[]) => number;
	cron: CronItem;
}

const props = defineProps<CronDayProps>();

const emit = defineEmits(["update"]);

const radioValue = ref(1);
const workday = ref(1);
const cycle01 = ref(1);
const cycle02 = ref(2);
const average01 = ref(1);
const average02 = ref(1);
const checkboxList = ref<number[]>([]);

// 计算两个周期值
const cycleTotal = computed(() => {
	const cycle01Val = props.check(cycle01.value, 1, 30);
	const cycle02Val = props.check(cycle02.value, cycle01Val ? cycle01Val + 1 : 2, 31, 31);
	return `${cycle01Val}-${cycle02Val}`;
});

// 计算平均用到的值
const averageTotal = computed(() => {
	const average01Val = props.check(average01.value, 1, 30);
	const average02Val = props.check(average02.value, 1, 31 - average01Val || 0);
	return `${average01Val}/${average02Val}`;
});

// 计算工作日格式
const workdayCheck = computed(() => {
	return props.check(workday.value, 1, 31);
});

// 计算勾选的checkbox值合集
const checkboxString = computed(() => {
	return checkboxList.value.length > 0 ? checkboxList.value.join(",") : "*";
});

// 单选按钮值变化时
const radioChange = () => {
	if (radioValue.value !== 2 && props.cron.week !== "?") {
		emit("update", "week", "?");
	}

	switch (radioValue.value) {
		case 1:
			emit("update", "day", "*");
			break;
		case 2:
			emit("update", "day", "?");
			break;
		case 3:
			emit("update", "day", cycleTotal.value);
			break;
		case 4:
			emit("update", "day", averageTotal.value);
			break;
		case 5:
			emit("update", "day", `${workdayCheck.value}W`);
			break;
		case 6:
			emit("update", "day", "L");
			break;
		case 7:
			emit("update", "day", checkboxString.value);
			break;
	}
};

// 监听相关数据变化
watch(radioValue, radioChange);
watch([cycle01, cycle02], () => {
	if (radioValue.value === 3) {
		emit("update", "day", cycleTotal.value);
	}
});
watch([average01, average02], () => {
	if (radioValue.value === 4) {
		emit("update", "day", averageTotal.value);
	}
});
watch(workday, () => {
	if (radioValue.value === 5) {
		emit("update", "day", `${workdayCheck.value}W`);
	}
});
watch(
	checkboxList,
	() => {
		if (radioValue.value === 7) {
			emit("update", "day", checkboxString.value);
		}
	},
	{ deep: true }
);
</script>
