<template>
  <div class="avatar-container">
    <!-- 头像展示 -->
    <div class="avatar-preview" @click="openDialog">
      <img :src="avatarUrl" alt="用户头像" />
      <div class="edit-overlay">
        <el-icon><i-ep-edit /></el-icon>
      </div>
    </div>

    <!-- 裁剪对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑头像" width="800px">
      <div class="cropper-wrapper">
        <vue-cropper
          ref="cropper"
          :img="cropperImg"
          :auto-crop="true"
          :auto-crop-width="200"
          :auto-crop-height="200"
          :fixed-box="true"
          @realTime="handlePreview"
        />
        <div class="preview">
          <div class="preview-title">预览</div>
          <img :src="previewUrl" class="preview-img" />
        </div>
      </div>

      <div class="controls">
        <el-upload action="#" :show-file-list="false" :before-upload="handleFile">
          <el-button type="primary" size="small">选择图片</el-button>
        </el-upload>

        <div class="tools">
          <el-button size="small" @click="changeScale(0.1)" title="放大">
            <el-icon><i-ep-zoomin /></el-icon>
          </el-button>
          <el-button size="small" @click="changeScale(-0.1)" title="缩小">
            <el-icon><i-ep-zoomout /></el-icon>
          </el-button>
          <el-button size="small" @click="rotateLeft" title="左旋转">
            <el-icon><i-ep-refreshleft /></el-icon>
          </el-button>
          <el-button size="small" @click="rotateRight" title="右旋转">
            <el-icon><i-ep-refreshright /></el-icon>
          </el-button>
        </div>

        <el-button type="success" size="small" @click="uploadAvatarHandle" :loading="uploading">
          确认上传
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { VueCropper } from 'vue-cropper'; // 正确导入方式
import 'vue-cropper/dist/index.css'; // 导入样式
import '@/assets/css/views/system/userAvatar.scss';
import { useAuthStore } from '@/stores/authStore';
import { uploadAvatar, updateUserAvatar } from '@/api/system/user/profile'; // 引入上传头像接口

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const userStore = useAuthStore();
const loginUser = computed(() => userStore.loginUser);
// 组件状态
const dialogVisible = ref(false);
const cropper = ref<any>(null);
const cropperImg = ref(loginUser.value?.user.avatar || defaultAvatar);
const previewUrl = ref('');
const uploading = ref(false);
// 计算头像URL
const avatarUrl = computed(() => loginUser.value?.user.avatar || defaultAvatar);

// 打开对话框
const openDialog = () => {
  dialogVisible.value = true;
};
// 处理预览
const handlePreview = (data: any) => {
  previewUrl.value = data.url;
};
// 处理文件选择
const handleFile = (file: File) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件');
    return false;
  }
  const reader = new FileReader();
  reader.onload = (e) => {
    cropperImg.value = e.target?.result as string;
  };
  reader.readAsDataURL(file);
  return false;
};
// 缩放
const changeScale = (scale: number) => {
  cropper.value?.changeScale(scale);
};
// 左旋转
const rotateLeft = () => {
  cropper.value?.rotateLeft()
};
// 右旋转
const rotateRight = () => {
  cropper.value?.rotateRight();
};
// 上传头像
const uploadAvatarHandle = () => {
  if (!cropper.value) return;
  uploading.value = true;

  cropper.value.getCropBlob(async (blob: Blob) => {
    try {
      // 创建FormData对象
      const formData = new FormData();
      // 将Blob转换为File对象（后端需要MultipartFile）
      const file = new File([blob], `avatar_${Date.now()}.png`, {
        type: "image/png"
      });
      // 添加文件参数
      formData.append("file", file);
      // 添加belong参数（指定上传目录）
      formData.append("belong", "avatar"); // 可根据需要修改目录名
      // 调用后端上传接口
      const response = await uploadAvatar(formData);
      // 处理响应
      if (response && response.code === 200) {
        // 调用更新用户信息的API保存头像URL到数据库
				const result = await updateUserAvatar(response.url)
				if (result.code === 200) {
					// 上传成功
					userStore.accessToken = result.accessToken;
					if (result.refreshToken) {
						localStorage.setItem("refreshToken", result.refreshToken);
					}
					//重新调用一下获取用户信息方法
					userStore.getUserInfo();
					ElMessage.success("头像更新成功");
        	dialogVisible.value = false;
				} else {
					ElMessage.error(result.msg)
				}
      } else {
        throw new Error(response.msg || "上传失败");
      }
    } catch (error) {
      console.error("上传失败", error);
      ElMessage.error((error as Error).message || "头像上传失败");
    } finally {
      uploading.value = false;
    }
  });
};
</script>
