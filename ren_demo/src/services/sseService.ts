import { useAuthStore } from '@/stores/authStore';
import { XStream } from 'ant-design-x-vue';

// 默认配置接口
interface SSEDefaultConfig {
  RETRY_DELAY: number;      // 重试延迟(毫秒)
  MAX_RETRY_COUNT: number;  // 最大重试次数
}
// 默认配置常量
const DEFAULT_CONFIG: SSEDefaultConfig = {
  RETRY_DELAY: 3000,        // 重试延迟(毫秒)
  MAX_RETRY_COUNT: 5,       // 最大重试次数
};

// 连接状态枚举
enum ConnectionStatus {
  CONNECTING = 'connecting',        // 连接中
  CONNECTED = 'connected',          // 已连接
  DISCONNECTED = 'disconnected',    // 已断开
  RETRYING = 'retrying',            // 重试中
  ERROR = 'error',                  // 错误
}

// 连接信息接口
interface ConnectionInfo {
  controller: AbortController;      // 控制器用于中断连接
  retryCount: number;               // 当前重试次数
  status: ConnectionStatus;         // 连接状态
  disconnect: () => void;           // 断开函数
  params?: Record<string, any>;     // 新增：请求参数
}

/**
 * SSE 服务类
 * 用于管理服务器发送事件(Server-Sent Events)连接
 * 功能包括：自动重连、Token刷新、多连接管理
 */
class SSEService {
  // Pinia 认证存储实例
  private authStore = useAuthStore();
  // 当前最大重试次数
  private maxRetryCount = DEFAULT_CONFIG.MAX_RETRY_COUNT;
  // 连接映射表：URL -> 连接信息
  private connections = new Map<string, ConnectionInfo>();
  
  /**
   * 创建 SSE 连接
   * @param url - 接口地址（不需要包含基础URL）
   * @param onMessage - 消息回调函数
   * @param onError - 错误回调函数
   * @param onStatusChange - 状态变化回调（可选）
   * @param params - 请求参数（可选）
   * @returns 断开连接的函数
   */
  async connect<T>(
    url: string,
    onMessage: (data: T) => void,
    onError?: (error: Error) => void,
    onStatusChange?: (status: ConnectionStatus) => void,
    params?: Record<string, any>  // 新增参数
  ): Promise<string> {
    // 生成带参数的唯一key
    const connectionKey = this.generateConnectionKey(url, params);
    
    // 如果已存在相同URL和参数的连接，先断开旧连接
    if (this.connections.has(connectionKey)) {
      this.disconnect(connectionKey);
    }
    // 创建新的中止控制器
    const abortController = new AbortController();
    // 创建新的连接信息对象
    const connectionInfo: ConnectionInfo = {
      controller: abortController,
      retryCount: 0,
      status: ConnectionStatus.CONNECTING,
      disconnect: () => this.disconnect(connectionKey),
      params, // 保存参数
    };
    // 将创建的连接信息存储到映射表
    this.connections.set(connectionKey, connectionInfo);
    // 如果调用connect方法的地方设置了onStatusChange回调，则调用回调方法，并将回调方法参数值设置为ConnectionStatus.CONNECTING
    onStatusChange?.(ConnectionStatus.CONNECTING);
    // 开始连接
    await this.createConnection(connectionKey, url, onMessage, onError, onStatusChange, params);
    // 返回一个唯一的连接键，页面拿到之后可以主动对连接进行管理
    return connectionKey;
  }

  /**
   * 生成连接的唯一键
   */
  private generateConnectionKey(url: string, params?: Record<string, any>): string {
    if (!params) return url;
    // 将参数序列化，确保相同参数生成相同key
    const paramStr = JSON.stringify(params, Object.keys(params).sort());
    return `${url}?${paramStr}`;
  }

  /**
   * 创建并维护 SSE 连接（内部方法）
   */
  private async createConnection<T>(
    connectionKey: string,
    url: string,
    onMessage: (data: T) => void,
    onError?: (error: Error) => void,
    onStatusChange?: (status: ConnectionStatus) => void,
    params?: Record<string, any>
  ): Promise<void> {
    const connection = this.connections.get(connectionKey);
    if (!connection) return;
    const { controller, retryCount } = connection;
    try {
      // 获取访问令牌
      const { accessToken } = storeToRefs(this.authStore);
      // 检查 Token 是否存在
      if (!accessToken.value) {
        throw new Error('未找到访问令牌，请先登录');
      }
      // 构建完整URL（包含参数）
      const fullUrl = this.buildUrlWithParams(url, params);
      console.log(`正在建立 SSE 连接: ${fullUrl}, 重试次数: ${retryCount}`);
      // 发起 SSE 请求
      const response = await fetch(fullUrl, {
        method: 'GET',
        headers: {
          'X-Access-Token': accessToken.value ? `Bearer ${accessToken.value}` : "",
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
          'Connection': 'keep-alive',
        },
        signal: controller.signal,
      });
      // 处理响应状态
      if (!response.ok) {
        // 对于流式接口，除非是严重错误，否则不抛异常
        if (response.status >= 500) {
          throw new Error(`SSE 连接失败: ${response.status} ${response.statusText}`);
        }
        
        if (response.status === 401) {
          console.log('Token 过期，但流式接口允许继续使用，用户访问其他页面时会自动刷新');
        }
      }
      // 检查响应体是否可读
      if (!response.body) {
        throw new Error('响应体不可读，SSE 连接失败');
      }
      // 更新连接状态
      connection.status = ConnectionStatus.CONNECTED;
      connection.retryCount = 0; // 连接成功，重置重试次数
      // 如果调用createConnection方法的地方设置了onStatusChange回调，则调用回调方法，并将回调方法参数值设置为ConnectionStatus.CONNECTED
      onStatusChange?.(ConnectionStatus.CONNECTED);
      console.log(`SSE 连接成功: ${url}`);
      // 使用 XStream 解析服务器发送的事件流（不指定解析后对象类型和解析器，所以将二进制数据流按照默认格式SSEOutput和默认解析器解析，解析后为SSEOutput类型的对象）
      for await (const event of XStream({
        readableStream: response.body,
      })) {
        // 检查是否是结束信号
        const eventData = (event as any)?.data;
        if (eventData === '[DONE]') {
          // 正常收到结束信号，流结束
          console.log(`收到结束信号[${connectionKey}]: ${eventData}，连接正常结束`);
          connection.status = ConnectionStatus.DISCONNECTED;
          // 如果调用createConnection方法的地方设置了onStatusChange回调，则调用回调方法，并将回调方法参数值设置为ConnectionStatus.DISCONNECTED
          onStatusChange?.(ConnectionStatus.DISCONNECTED);
          // 清理连接资源
          this.cleanupConnection(connectionKey, 'normal');
          return;
        }
        // 如果调用createConnection方法的地方设置了onMessage回调，则调用回调方法，并将回调方法参数值设置为event（上面没有指定解析后的类型，所以event为默认类型：SSEOutput）
        onMessage(event as T);
      }

      // 如果执行到这里，说明连接被关闭但没有收到[DONE]，这可能是网络错误或服务器异常
      console.warn('连接异常关闭');
      connection.status = ConnectionStatus.DISCONNECTED;
      // 如果调用createConnection方法的地方设置了onStatusChange回调，则调用回调方法，并将回调方法参数值设置为ConnectionStatus.DISCONNECTED
      onStatusChange?.(ConnectionStatus.DISCONNECTED);
      // 清理连接资源
      this.cleanupConnection(connectionKey, 'error');
    } catch (error) {
      // 处理连接错误
      await this.handleConnectionError(error, connectionKey, url, onError, onStatusChange);
    }
  }

  /**
   * 构建带参数的URL
   */
  private buildUrlWithParams(url: string, params?: Record<string, any>): string {
    let fullUrl = `${import.meta.env.VITE_APP_BASE_API || ''}${url}`;
    
    if (params && Object.keys(params).length > 0) {
      // 使用 URLSearchParams 构建查询字符串
      const queryParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          queryParams.append(key, String(value));
        }
      });
      
      const queryString = queryParams.toString();
      if (queryString) {
        fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryString;
      }
    }
    
    return fullUrl;
  }

  /**
   * 处理连接错误（内部方法）
   */
  private async handleConnectionError(
    error: any,
    connectionKey: string,
    url: string,
    onError?: (error: Error) => void,
    onStatusChange?: (status: ConnectionStatus) => void
  ): Promise<void> {
    const connection = this.connections.get(connectionKey);
    if (!connection) return;
    
    // 检查是否为手动中断
    if (error.name === 'AbortError') {
      console.log(`连接被手动中断: ${url}`);
      this.connections.delete(connectionKey);
      return;
    }
    
    // 记录错误
    console.error(`SSE 连接错误: ${url}`, error);
    connection.status = ConnectionStatus.ERROR;
    onStatusChange?.(ConnectionStatus.ERROR);
    onError?.(error as Error);
    
    // 清理连接
    this.connections.delete(connectionKey);
  }

  /**
   * 连接正常结束，清理连接资源（内部方法）
   */
  private cleanupConnection(connectionId: string, reason: 'normal' | 'manual' | 'error' = 'normal'): void {
    const connection = this.connections.get(connectionId);
    if (connection){
      console.log(`清理连接[${connectionId}], 原因: ${reason}`);
      // 清理映射
      this.connections.delete(connectionId);
    }
  }

  /**
   * 连接未结束，主动断开指定连接，并从连接池中移除连接信息
   * @param url - 要断开的连接URL
   */
  disconnect(url: string): void {
    const connection = this.connections.get(url);
    if (connection) {
      console.log(`正在断开连接: ${url}`);
      // 中止请求
      connection.controller.abort();
      // 从连接池中移除
      this.connections.delete(url);
      console.log(`连接已断开: ${url}`);
    }
  }

  /**
   * 连接未结束，主动断开所有活跃连接，并从连接池中移除所有连接信息
   */
  disconnectAll(): void {
    console.log(`正在断开所有连接，共 ${this.connections.size} 个`);
    this.connections.forEach((connection, url) => {
      console.log(`断开连接: ${url}`);
      connection.controller.abort();
    });
    this.connections.clear();
    console.log('所有连接已断开');
  }

  /**
   * 获取当前连接状态
   * @param url - 连接URL
   * @returns 连接状态
   */
  getConnectionStatus(url: string): ConnectionStatus | null {
    return this.connections.get(url)?.status || null;
  }

  /**
   * 获取活跃连接数量
   * @returns 活跃连接数
   */
  getConnectionCount(): number {
    return this.connections.size;
  }

  /**
   * 设置最大重试次数
   * @param count - 最大重试次数
   */
  setMaxRetryCount(count: number): void {
    this.maxRetryCount = Math.max(1, count);
  }
}

// 导出单例实例
export const sseService = new SSEService();