<template>
  <div class="login">
    <video
      class="login-video"
      src="@/assets/video/login.mp4"
      autoplay
      loop
      muted
      @loadeddata="onVideoReady"
      v-show="videoReady"
    ></video>
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
    >
      <div class="login-title">任后台管理系统</div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          placeholder="账号"
        >
          <template #prefix>
            <img class="el-input__icon input-icon" src="@/assets/images/login/user.png" />
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          :type="isShowPassword ? 'text' : 'password'"
          autocomplete="new-password"
          placeholder="密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix>
            <img class="el-input__icon input-icon" src="@/assets/images/login/password.png" />
          </template>
          <template #suffix>
            <img
              @click="isShowPassword = !isShowPassword"
              v-if="!isShowPassword"
              class="el-input__icon input-icon input-icon-eyes"
              src="@/assets/images/login/eyes-hidden.png"
            />
            <img
              @click="isShowPassword = !isShowPassword"
              v-if="isShowPassword"
              class="el-input__icon input-icon input-icon-eyes"
              src="@/assets/images/login/eyes-show.png"
            />
          </template>
        </el-input>
      </el-form-item>
      <el-checkbox
        v-model="loginForm.rememberMe"
        style="color: #fff !important; margin: 0px 0px 25px 0px"
        >记住密码</el-checkbox
      >
      <el-form-item style="width: 100%">
        <el-button
          :loading="isLoading"
          size="default"
          type="primary"
          class="login-btn"
          @click.prevent="handleLogin"
        >
          <span v-if="!isLoading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <div class="el-login-footer">
      <span>Copyright © 2025-2026 ren.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts" name="login">
import router from "@/router";
import { useAuthStore } from "@/stores/authStore";
import "@/assets/css/views/login.scss"
import Cookies from 'js-cookie';

/*============================通用参数开始============================*/
//表单
const loginForm = reactive({
	//用户名
	username: "",
	//密码
	password: "",
	//是否记住密码
	rememberMe: false,
});
//表单验证规则
const loginRules = reactive({
	username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
	password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
});
//是否登陆中
const isLoading = ref(false);
// 视频是否加载完成
const videoReady = ref(false);
// 密码是否显示
const isShowPassword = ref(false);
//状态管理
const authStore = useAuthStore();
//加密
const crypto = useCrypto();

/*============================通用参数结束============================*/

/*============================页面方法开始============================*/
// 视频加载完成
const onVideoReady = async () => {
	videoReady.value = true;
};
// 获取cookie中的数据
const getCookieData = () => {
	//获取cookie中的用户名
	const username = Cookies.get("username");
	//获取cookie中的密码
	const password = Cookies.get("password");
	//获取cookie中的记住密码
	const rememberMe = Cookies.get("rememberMe");
	//如果用户名存在，赋值到表单中
	if (username) {
		loginForm.username = username;
	}
	//如果密码存在，解密后赋值到表单中
	if (password) {
		loginForm.password = crypto.decrypt(password);
	}
	//如果记住密码存在，就赋值到表单中
	if (rememberMe) {
		loginForm.rememberMe = rememberMe === "true";
	}
}
// 存储cookie数据
const setCookieData = () => {
	//如果记住密码，就存储到cookie
	if (loginForm.rememberMe) {
		//用户名存储到cookie，有效期30天
		Cookies.set("username", loginForm.username, { expires: 30 });
		//密码加密后存储到cookie，有效期30天
		Cookies.set("password", crypto.encrypt(loginForm.password), {
			expires: 30,
		});
		//记住密码存储到cookie，有效期30天
		Cookies.set("rememberMe", loginForm.rememberMe.toString(), {
			expires: 30,
		});
	} else {
		Cookies.remove("username");
		Cookies.remove("password");
		Cookies.remove("rememberMe");
	}
}
//登录
const handleLogin = async () => {
	try {
		isLoading.value = true;

		//如果记住密码，就存储到cookie
		setCookieData();

		//登录
		const result = await authStore.login(loginForm.username, loginForm.password);
		if (result.code === 200) {
			//重定向到首页（不允许回退）
			router.replace("/");
		} else {
			ElMessage.error(result.msg);
		}
	} finally {
		isLoading.value = false;
	}
};
/*============================页面方法结束============================*/

/*============================生命周期钩子开始============================*/
// 组件加载完成后执行
// 初始化表格数据
onMounted(async () => {
	//获取cookie中的数据
	getCookieData();
});
/*============================生命周期钩子结束============================*/
</script>
