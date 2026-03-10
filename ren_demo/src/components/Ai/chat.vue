<script setup lang="ts" name="AXBubbleListSetup">
import {
	listConversation,
  renameConversation,
  deleteConversation,
  listConversationMessages,
  addConversationMessage
} from "@/api/components/ai/chat";
import '../../assets/css/components/ai/chat.scss'
import { h } from 'vue';
import { nanoid } from 'nanoid'
import { useTheme } from '@/composables/useTheme';
import { Flex,theme,message,Modal, Input } from 'ant-design-vue';
import { BubbleList, Conversations,Sender } from 'ant-design-x-vue';
import type { BubbleListProps, ConversationsProps, SSEOutput } from 'ant-design-x-vue';
import { UserOutlined, DeleteOutlined, EditOutlined,PlusOutlined, CopyOutlined } from '@ant-design/icons-vue';
/*===========================================全局相关开始=================================================*/
// 当前主题、主题变量
const { themeSetting } = useTheme();
const [messageApi, contextHolder] = message.useMessage();
const { token } = theme.useToken();
/*===========================================全局相关结束=================================================*/

/*===========================================对话列表开始=================================================*/
// 当前激活的对话
const activeKey = ref("");
// 响应式对话列表（根据后台对话列表监听计算得出）
const pageConversationList = ref<{key: string, label: string, disabled: boolean, isSave: number}[]>([]);
// 修改页面对话列表
const updatePageConversationList = async () => {
  // 先将未保存的对话列表拿出来
  const noSaveConversationList = pageConversationList.value.filter((item : any) => item.isSave === 0);
  // 将查询出的后台对话列表同步到页面对话列表
  const res = await listConversation();
  if (res.code === 200) {
    pageConversationList.value = res.data.map((item : any) => ({
      key: item.conversationId,
      label: item.title,
      disabled: false,
      isSave: 1
    }));
    // 将未保存的对话列表重新添加到页面对话列表
    pageConversationList.value.push(...noSaveConversationList);
  }
}
// 添加新对话
const addNewConversation = () => {
  // 先寻找是不是已经有未保存的对话
  const existingUnsaved = pageConversationList.value.find((item) => item.isSave === 0);
  if (existingUnsaved) {
    // 如果有未保存的对话，直接切换到这个对话
    activeKey.value = existingUnsaved.key;
    messageApi.warning('已经是最新的对话了');
    return;
  }
  // 先随机生成conversationId、label，等后面发送第一条消息往数据库添加的时候再进行同步修改
  const newConversationId = nanoid();
  const newConversation = {
    key: newConversationId,
    label: `新对话${pageConversationList.value.length + 1}`,
    disabled: false,
    isSave: 0
  };
  pageConversationList.value.push(newConversation);
  // 激活新对话
  activeKey.value = newConversationId;
  // 清空当前对话气泡
  pageMessageList.value = [];
};
// 处理对话列表切换
const handleActiveChange = (newKey: string) => {
  activeKey.value = newKey;
};
// 增强的菜单配置
const conversationsMenuConfig: ConversationsProps['menu'] = (conversation) => ({
  items: [
    {
      label: '重命名',
      key: 'rename',
      icon: h(EditOutlined),
    },
    {
      label: '复制对话',
      key: 'duplicate',
      icon: h(CopyOutlined),
    },
    {
      type: 'divider',
    },
    {
      label: '删除',
      key: 'delete',
      icon: h(DeleteOutlined),
      danger: true,
    },
  ],
  onClick: (menuInfo) => {
    const { key } = menuInfo;
    switch (key) {
      case 'rename':
        openRenameModal(conversation);
        break;
      case 'duplicate':
        duplicateConversation(conversation);
        break;
      case 'delete':
        if (pageConversationList.value.length > 1) {
          handleDeleteConversation(conversation);
        } else {
          messageApi.warning('至少保留一个对话');
        }
        break;
    }
  },
});
// 重命名相关状态
const renameModalVisible = ref(false);
// 待重命名的对话
const currentRenameConversation = ref<any>(null);
// 新的对话名称
const newTitle = ref('');
// 打开重命名对话框
const openRenameModal = (conversation: any) => {
  currentRenameConversation.value = conversation;
  newTitle.value = conversation.label;
  renameModalVisible.value = true;
};
// 确认重命名
const handleRenameConfirm = async () => {
  // 校验对话名称是否为空
  if (!newTitle.value.trim()) {
    messageApi.warning('对话名称不能为空');
    return;
  }
  // 校验对话名称是否与当前名称相同
  if (newTitle.value.trim() === currentRenameConversation.value.label) {
    messageApi.warning('对话名称未改变');
    return;
  }
  // 调用重命名接口
  const result = await renameConversation({
    conversationId: currentRenameConversation.value.key,
    title: newTitle.value.trim(),
  });
  if (result.code == 200) {
    // 修改页面对话列表中对应对话的名称
    const conversation = pageConversationList.value.find((item) => item.key === currentRenameConversation.value.key);
    if (conversation) {
      conversation.label = newTitle.value.trim();
      conversation.isSave = 1;
    }
    messageApi.success('重命名成功');
  } else {
    messageApi.error(result.msg || '重命名失败');
  }
  // 清空重命名相关内容
  renameModalVisible.value = false;
  newTitle.value = '';
  currentRenameConversation.value = null;
};
// 取消重命名
const handleRenameCancel = () => {
  renameModalVisible.value = false;
  newTitle.value = '';
  currentRenameConversation.value = null;
};
// 复制对话（进进页面复制，数据库不同步，等发送第一条消息的时候在进行添加同步）
const duplicateConversation = (conversation: any) => {
  if(conversation.isSave === 0) {
    messageApi.warning('不可以复制空对话');
    return;
  }
  if (conversation) {
    pageConversationList.value.push({
      key: nanoid(),
      label: `${conversation.label} (副本)`,
      disabled: false,
      isSave: 0
    });
    messageApi.success('对话已复制');
  }
};
// 删除对话
const handleDeleteConversation = async (conversation: any) => {
  //调用删除接口
  const result = await deleteConversation(conversation.key);
  if (result.code === 200) {
    // 删除页面对话列表中的对话
    const index = pageConversationList.value.findIndex((item) => item.key === conversation.key);
    if (index !== -1) {
      pageConversationList.value.splice(index, 1);
    }
    if (activeKey.value === conversation.key && pageConversationList.value.length > 0) {
      // 如果删除的是当前激活的对话，并且对话列表还有其他的对话，则切换到第一个
      activeKey.value = pageConversationList.value[0].key;
    }
    messageApi.success(`已删除对话: ${conversation.label}`);
  } else {
    messageApi.error(result.msg || '删除失败');
  }
};
/*===========================================对话列表结束=================================================*/

/*===========================================对话消息相关开始=================================================*/
// 使用对象形式定义roles配置 - 为不同角色（ai/user）设置不同的气泡样式[6](@ref)
const bubbleRoleList: BubbleListProps['roles'] = {
  // 配置AI角色的气泡样式
  assistant: {
    placement: 'start', // AI消息靠左显示
    avatar: { 
      icon: h(UserOutlined), // 使用UserOutlined图标作为头像，需要使用VUE的h函数渲染
      style: { background: '#fde3cf' } // 设置头像背景色
    },
    typing: { step: 5, interval: 20 }, // 打字机效果：每次5字符，间隔20ms[1](@ref)
    style: {
      maxWidth: '600px', // 限制气泡最大宽度
    },
  },
  // 配置用户角色的气泡样式
  user: {
    placement: 'end', // 用户消息靠右显示
    avatar: { 
      icon: h(UserOutlined), // 使用UserOutlined图标作为头像，需要使用VUE的h函数渲染
      style: { background: '#87d068' } // 不同的头像背景色
    },
  },
};
type Role = "user" | "assistant";
// 页面对话消息列表
const pageMessageList = ref<{key: number, role: Role, content: string}[]>([]);
// 输入框绑定值
const inputValue = ref('');
// 修改页面对话消息列表
const updatePageMessageList = async(conversationId: string) => {
  const result = await listConversationMessages(conversationId);
  if (result.code === 200) {
    pageMessageList.value = result.data.map((item : any) => ({
      key: item.messageId,
      role: item.role,
      content: item.content,
    }));
  } else {
    messageApi.error(result.msg || '获取对话消息列表失败');
  }
}
// 添加新的消息
const handleAddConversationMessage = async (role: Role = 'user', content: string) : Promise<number> => {
  // 调用添加对话消息接口
  const result = await addConversationMessage({
    conversationId: activeKey.value,
    role: role,
    content,
  });
  if (result.code === 200) {
    const conversation = result.data.conversation;
    const message = result.data.message;
    const pageConversation = pageConversationList.value.find((item) => item.key === activeKey.value);
    if(pageConversation && pageConversation?.isSave === 0){
      // 更新页面对话列表中对应对话的标题
      pageConversation.label = conversation.title;
      pageConversation.isSave = 1;
    }
    // 成功添加对话消息后，将其添加到对话列表
    pageMessageList.value.push({key: message.messageId, role, content});
    // 返回消息Id，用于后续发送真正的AI请求
    return message.messageId;
  } else {
    messageApi.error(result.msg || '添加对话消息失败');
    return -1;
  }
};
// 更新AI消息内容（仅仅更新页面数据，数据库已经完成添加）
const updateAIMessage = (messageId: number, content: string) => {
  // 找到对应的消息项
  const message = pageMessageList.value.find((item) => item.key === messageId);
  if (message) {
    // 更新AI消息内容
    message.content = content;
  }
};
// 提交消息
const submit = async (content: string) => {
  // 将用户消息添加到对话列表
  const questionMessageId = await handleAddConversationMessage('user', content);
  // 如果添加失败，直接返回
  if (questionMessageId === -1) {
    return;
  }
  // 显示"正在思考"的AI消息
  const aiMessageId = await handleAddConversationMessage('assistant', '正在深度思考...');
  // 如果添加失败，直接返回
  if (aiMessageId === -1) {
    return;
  }
  // 清空输入框
  inputValue.value = '';
  // 发送SSE请求，获取AI回复
  // AI回复
  let aiContent = '';
  sseService.connect(
    '/main/ai/mainChat',
    (data: SSEOutput) => {
      // 实时更新AI消息显示
      aiContent += data.data;
      updateAIMessage(aiMessageId, aiContent);
    },
    undefined,
    undefined,
    {questionMessageId, aiMessageId, conversationId: activeKey.value}
  );
};
/*===========================================对话消息相关结束=================================================*/

/*===========================================钩子函数相关开始==============================================*/
onMounted(async () => {
  // 更新页面对话列表
  await updatePageConversationList();
  // 添加一个新对话（仅仅页面添加，先不添加到数据库，发送第一条消息时才同步到数据库）
  addNewConversation();
});
/*===========================================钩子函数相关结束==============================================*/

/*===========================================监听计算相关开始==============================================*/
// 计算主题配置
const currentTheme = computed(() => {
  return {
    algorithm: themeSetting.theme.value === 'theme_black' ? theme.darkAlgorithm : theme.defaultAlgorithm
  };
});
// 对话列表样式
const conversationsStyle = computed(() => ({
  width: '256px',
  background: token.value.colorBgContainer,
  borderRadius: token.value.borderRadius,
}));
//监听activeKey变化
watch(activeKey, async (newId, oldId) => {
  if (newId !== oldId) {
    // 切换到新的对话时，清空当前对话气泡
    pageMessageList.value = [];
    // 更新页面对话消息列表
    await updatePageMessageList(newId);
  }
});
/*===========================================监听计算相关结束==============================================*/
</script>

<template>
  <contextHolder />
  <AConfigProvider :theme="currentTheme">
    <!-- 重命名对话框 -->
    <Modal
      v-model:open="renameModalVisible"
      title="重命名对话"
      @ok="handleRenameConfirm"
      @cancel="handleRenameCancel"
      :ok-text="'确定'"
      :cancel-text="'取消'"
      :style="{top: '300px'}"
    >
      <Input
        v-model:value="newTitle"
        placeholder="请输入新的对话名称"
        @pressEnter="handleRenameConfirm"
        :maxlength="50"
        show-count
      />
    </Modal>

    <Flex class="outer-container" gap="small">
      <!-- 左侧对话列表区域 -->
      <Flex vertical gap="small" :style="conversationsStyle">
        <!-- 顶部工具栏 -->
        <Flex justify="space-between" align="center" :style="{ padding: '8px' }">
          <span :style="{ fontWeight: 'bold' }">对话列表</span>
          <AButton 
            type="primary" 
            size="small"
            @click="addNewConversation"
            :icon="h(PlusOutlined)"
          />
        </Flex>
        
        <!-- 对话列表 -->
        <Conversations
          :active-key="activeKey"
          :on-active-change="handleActiveChange"
          :menu="conversationsMenuConfig"
          :items="pageConversationList"
          :style="{ 
            flex: 1, 
            overflow: 'auto',
            borderRadius: token.borderRadius
          }"
        />
      </Flex>
      
      <!-- 右侧主区域（内层 Flex）：方向为 column，确保上下排列 -->
      <Flex class="main-chat-area" vertical gap="small" flex="1" justify="space-between">
        <BubbleList
          ref="listRef"
          :style="{ paddingInline: '16px' }"
          :roles="bubbleRoleList"
          :items="pageMessageList"
        />
        
        <!-- 底部发送框 -->
        <Sender
          v-model:value="inputValue"
          submit-type="shiftEnter"
          placeholder="Press Shift + Enter to send message"
          :on-submit="submit"
        />
      </Flex>
    </Flex>
  </AConfigProvider>
</template>