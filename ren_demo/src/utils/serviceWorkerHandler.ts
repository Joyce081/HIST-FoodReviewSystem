import { useAuthStore } from "@/stores/authStore";

// 明确定义消息事件类型
interface ServiceWorkerMessageEvent extends MessageEvent {
  data: {
    type: string;
    port?: MessagePort;
  };
  ports: MessagePort[]; // 更准确的类型定义
}

//监听器处理函数
export function initServiceWorkerComm(): () => void {
  // 环境检查
  if (!('serviceWorker' in navigator)) return () => {};
  if (typeof window === 'undefined') return () => {}; // SSR兼容

  // 创建兼容 EventListener 的通用处理函数
  const handler: EventListener = (rawEvent: Event) => {
    // 将通用事件断言为我们需要的具体类型
    const event = rawEvent as unknown as ServiceWorkerMessageEvent;
    // 确保这是我们想要处理的事件类型
    if (event.data?.type === 'GET_DATA' && event.ports?.length > 0) {
      (async () => {
				//我们这里不仅要获取到对方的消息，还要给他返回内容，所以我们要获取到对刚移交给我们的端口，我们使用这个端口发送消息，对方就能接收到了
        const port = event.ports[0];
        try {
          // 获取pinia数据
          const authStore = useAuthStore();
          // 获取accessToken
          const accessToken = await authStore.getAccessToken();
          // 发送结构化响应
          port.postMessage({
            success: true,
            accessToken: accessToken,
            timestamp: Date.now()
          });
        } catch (error) {
          console.error('[SW Handler] Data fetch error:', error);
          // 发送结构化错误响应
          port.postMessage({
            success: false,
            error: error instanceof Error ? error.message : 'Unknown error',
            timestamp: Date.now()
          });
        }
      })();
    }
  };

  // 添加事件监听
	// navigator.SW表示当前vue窗口在SW下的客户端（注意：这里和sw.js中的self不同，sw.js中的self能监听到所有被SW控制的客户端，而这个只能监听到当前vue窗口的客户端）
	// 注意navigator.serviceWorker只是SW提供给当前客户端的一个api，只能用来管理 SW 的生命周期（注册、注销）或通过 postMessage 通信，​无法直接访问 SW 线程内部事件（如监听网络请求，监听网络请求一定要在SW所属线程，通过self完成）
  navigator.serviceWorker.addEventListener('message', handler);
  // 返回清理函数
  return () => {
    // 删除事件监听器
    navigator.serviceWorker.removeEventListener('message', handler);
  };
}
