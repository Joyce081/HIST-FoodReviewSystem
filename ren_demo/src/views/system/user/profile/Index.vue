<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div>
            <div class="text-center">
							<userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <el-icon><i-ep-user /></el-icon>用户名称
                <div class="pull-right">{{ user.username }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><i-ep-phone /></el-icon>手机号码
                <div class="pull-right">{{ user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><i-ep-message /></el-icon>用户邮箱
                <div class="pull-right">{{ user.email }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><i-ep-officebuilding /></el-icon>所属部门
                <div class="pull-right" v-if="user.dept">{{ user.dept.deptName }} / {{ allPostName }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><i-ep-userfilled /></el-icon>所属角色
                <div class="pull-right">{{ allRoleName }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><i-ep-calendar /></el-icon>创建日期
                <div class="pull-right">{{ formatTime(user.createTime) }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
							<div class="user-info-form">
                <el-form :model="userForm" label-width="100px" :rules="userFormRules" ref="userFormRef">
                  <el-form-item label="用户昵称" prop="nickname">
                    <el-input v-model="userForm.nickname" />
                  </el-form-item>
                  <el-form-item label="手机号码" prop="phonenumber">
                    <el-input v-model="userForm.phonenumber" />
                  </el-form-item>
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="userForm.email" />
                  </el-form-item>
                  <el-form-item label="性别" prop="sex">
                    <el-radio-group v-model="userForm.sex">
                      <el-radio label="男" :value="0" />
                      <el-radio label="女" :value="1" />
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleUpdateUserInfo(userFormRef)">保存</el-button>
                    <el-button @click="handleResetUserInfo">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
							<div class="password-form">
                <el-form :model="passwordForm" label-width="100px" :rules="passwordFormRules" ref="passwordFormRef">
                  <el-form-item label="旧密码" prop="oldPassword">
                    <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleUpdatePassword(passwordFormRef)">提交</el-button>
                    <el-button @click="handleResetPassword">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { updatePassword,updateUserProfile } from "@/api/system/user/profile";
import "@/assets/css/views/system/userProfile.scss"
import type { FormItemRule, FormInstance } from 'element-plus'; // 添加类型导入
import {useAuthStore} from "@/stores/authStore";
import type {User} from '@/types/User'
// 虽然已经全局导入，但是由于当前页面这个方法是要在模板中用，所以还是需要手动导入，自动导入的内容只能在js中使用
import { formatTime } from '@/utils/dateUtils'
import userAvatar from './userAvatar.vue';

const activeTab = ref('userinfo')
const authStore = useAuthStore();
const {loginUser,roleNames,postNames} = storeToRefs(authStore);
const user = computed(() => loginUser.value?.user || {} as User);
const allRoleName = computed(() => roleNames.value.join('/'));
const allPostName = computed(() => postNames.value.join('/'));
//添加角色表单对象
const userFormRef = ref<FormInstance>();
const passwordFormRef = ref<FormInstance>();
// 用户信息表单
const userForm = ref({
	nickname: '',
	phonenumber: '',
	email: '',
	sex: 0
});
// 监听user变化，更新表单
watch(user, (newUser) => {
	userForm.value = {
		nickname: newUser.nickname || '',
		phonenumber: newUser.phonenumber || '',
		email: newUser.email || '',
		sex: newUser.sex || 0
	};
}, { immediate: true });
//修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
})

// 表单验证规则
const userFormRules = ref<Record<string, FormItemRule[]>>({
  nickname: [
		{ required: true, message: "用户昵称不能为空", trigger: "blur" }
	],
	email: [
		{ required: true, message: "邮箱地址不能为空", trigger: "blur" },
		{
			type: "email",
			message: "请输入正确的邮箱地址",
			trigger: ["blur", "change"]
		}
	],
	phonenumber: [
		{ required: true, message: "手机号码不能为空", trigger: "blur" },
		{
			pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
			message: "请输入正确的手机号码",
			trigger: "blur"
		}
	],
});

// 处理更新用户信息
const handleUpdateUserInfo = async (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	await formEl.validate(async (valid) => {
		if (valid) {
			try {
				const result = await updateUserProfile(userForm.value)
				if (result.code === 200) {
					ElMessage.success('用户信息更新成功')
					authStore.accessToken = result.accessToken;
					if (result.refreshToken) {
						localStorage.setItem("refreshToken", result.refreshToken);
					}
					//重新调用一下获取用户信息方法
					authStore.getUserInfo();
				} else {
					ElMessage.error(result.msg)
				}
			} catch (error) {
				console.error('更新用户信息失败:', error)
				ElMessage.error('更新用户信息失败')
			}
		}
	});
}

// 处理重置用户信息
const handleResetUserInfo = () => {
  // 直接使用计算属性的值
  userForm.value = {
    nickname: user.value.nickname,
    phonenumber: user.value.phonenumber,
    email: user.value.email,
    sex: user.value.sex
  }
}

// 密码确认验证
const equalToPassword = (rule: any, value: string, callback: (error?: Error) => void) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 密码表单验证规则
const passwordFormRules = ref<Record<string, FormItemRule[]>>({
  oldPassword: [
		{ required: true, message: "旧密码不能为空", trigger: "blur" }
	],
	newPassword: [
		{ required: true, message: "新密码不能为空", trigger: "blur" },
		{ min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
		{ pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
	],
	confirmPassword: [
		{ required: true, message: "确认密码不能为空", trigger: "blur" },
		{ required: true, validator: equalToPassword, trigger: "blur" }
	]
})

// 处理更新密码
const handleUpdatePassword = async (formEl: FormInstance | undefined) => {
	if (!formEl) return;
	await formEl.validate(async (valid) => {
		if (valid) {
			try {
				const result = await updatePassword(passwordForm.value)
				if (result.code === 200) {
					ElMessage.success('密码更新成功')
					passwordForm.value = {
						oldPassword: '',
						newPassword: '',
						confirmPassword: ''
					}
					authStore.accessToken = result.accessToken;
					if (result.refreshToken) {
						localStorage.setItem("refreshToken", result.refreshToken);
					}
					//重新调用一下获取用户信息方法
					authStore.getUserInfo();
				} else {
					ElMessage.error(result.msg)
				}
			} catch (error) {
				console.error('更新密码失败:', error)
				ElMessage.error('更新密码失败')
			}
		}
	});
}

// 处理重置密码
const handleResetPassword = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}
</script>
