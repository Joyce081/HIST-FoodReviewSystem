//vue所在客户端的id
let mainClientId = null;

//self表示的其实是Service Worker，而self.clients 提供了访问当前 Service Worker 控制的所有客户端（包含了所有标签页的客户端，如VUE，测试页面，搜索页面等），addEventListener方法表示给当前服务器添加一个监听方法（也就是被当前service worker控制的所有客户端发送的请求，都能被这里监听到）
//监听install事件，install事件表示SW安装完成后立即执行
self.addEventListener('install', event => {
  // 强制跳过等待期：新版本 Service Worker 安装后立即激活（无需等待旧页面关闭）
  event.waitUntil(self.skipWaiting());
});

// 当Service Worker激活时立即接管控制权
// 监听active事件，active事件表示SW激活完成后立即执行
self.addEventListener('activate', event => {
  // Service Worker激活完成后立即执行
  event.waitUntil(
		(async () => {
			// 立即接管所有已打开的客户端页面（包括未被当前 SW 控制的页面）
      await self.clients.claim().then(() => {
				console.log("Service Worker 已接管所有客户端"); // 调试日志
			});

      // 从缓存读取 mainClientId
      const cache = await caches.open('client-id-cache');
      const cachedResponse = await cache.match('mainClientId');
      if (cachedResponse) {
        mainClientId = await cachedResponse.text();
        console.log("从缓存恢复 mainClientId:", mainClientId);
      }
    })()
  );
});

//监听message消息，主要用于获取vue在创建的时候发送的一个消息，SW获取到这个消息后，就知道了发送这个消息的客户端的id，也就能正确获取到vue所在客户端的id，用于后面正确发送消息
self.addEventListener("message", async (event) => {
	// 只接受 Vue 主页面的注册
  if (event.data.type === "REGISTER_AS_MAIN_CLIENT" && event.data.isMain) {
    mainClientId = event.source.id;
		// 存储到 Cache API
		console.log("将mainClientId存储到缓存中:", mainClientId);
    const cache = await caches.open('client-id-cache');
    const response = new Response(mainClientId);
    await cache.put('mainClientId', response);
  }
});

// 监听所有网络消息
// 这里接收到了网络请求之后，建立一个通道，重新发送一个message消息，并把一个端口交给对方，用于对方给我们发送响应消息（双向通信）
// 对方监听到message端口后，处理后，那我们给他提供的端口给我们发送响应消息，我们再把消息返回给请求者，整个流程即可完成

// 问题一：为什么不直接由vue监听fetch，然后监听到后，进行处理之后返回给调用者？
// 回答：message是一个浏览器事件，符合 Vue 事件监听机制，fetch是函数调用，vue无法直接监听，vue作为一个前端框架，他不是一个服务器，她不能直接监听fetch，只能监听message

// 问题二：既然vue无法直接监听，但是当引入了SW之后，SW提供给VUE一个客户端之后为什么还是不能直接用SW提供给VUE的客户端进行直接监听（例如serviceWorkerHandler.ts中的navigator.serviceWorker）
// 回答：因为SW是运行在一个独立线程内的，在VUE的主线程并不能访问其内部内容，navigator.serviceWorker只是SW提供给当前客户端的一个api，只能用来管理 SW 的生命周期（注册、注销）或通过 postMessage 通信，​无法直接访问 SW 线程内部事件（如监听网络请求，监听网络请求一定要在SW所属线程，通过self完成）
self.addEventListener('fetch', event => {
	const requestUrl = new URL(event.request.url);
  const basePath = '/api/service-worker';

	// 检查请求是否是我们要处理的特定API路径
  if (requestUrl.pathname.startsWith(basePath)) {
		console.log("成功拦截到需要处理的请求:", requestUrl);
		// 拦截并自定义处理这个API请求
    event.respondWith(handlePiniaRequest(event, basePath, requestUrl));
  }
});

// 处理Pinia API请求的自定义函数
function handlePiniaRequest(event, basePath, requestUrl) {

	//获取除去前置路径之外的路径
	const subPath = requestUrl.pathname.slice(basePath.length);

  return new Promise(resolve => {
    // 将后续操作纳入事件生命周期管理（确保异步操作完成）
    event.waitUntil((async () => {
      try {
				//判断vue客户端id是否存在
				if (mainClientId === null) {
          // 没有活动客户端时返回503错误
          return resolve(createErrorResponse("没有活动的客户端", 503));
        }

        // 根据vue客户端id获取vue客户端
      	let targetClient = self.clients.get(mainClientId);

				//判断vue客户端是否存在
				if (targetClient === null) {
          // 没有活动客户端时返回503错误
          return resolve(createErrorResponse("没有活动的客户端", 503));
        }

				// 以下代码的逻辑为：
				// 首先SW一般会有两个端口，他们像是电话的两头，如果使用port1发送消息，那么必须使用port2接收消息，如果使用port2，发送消息，那么必须使用port1接收消息
				// 以下实例，当前SW客户端首先使用port2发送消息，然后又把port2的所有权给了对方（端口移交所有权是双向通信核心，因为不能一个人占用两个端口），表示对方后面发送消息，会继续使用port2发送，所以这里的客户端应该监听port1
        // 为每个请求创建新的消息通道（修复端口重用问题）
        const channel = new MessageChannel();

        // 设置端口1的监听器，等待客户端返回数据
        channel.port1.onmessage = e => {
          // 当收到客户端数据时，构建并返回JSON格式的响应
          resolve(new Response(JSON.stringify(e.data), {
            headers: {'Content-Type': 'application/json'}
          }));
          // 关闭端口防止内存泄漏
          channel.port1.close();
        };

        // 设置10秒超时处理（防止客户端无响应）
        const timeoutId = setTimeout(() => {
          resolve(createErrorResponse("客户端响应超时", 504));
          channel.port1.close();
        }, 10000);

        // 覆盖监听器以清除超时
        channel.port1.onmessage = e => {
          clearTimeout(timeoutId);
          resolve(new Response(JSON.stringify(e.data), {
            headers: {'Content-Type': 'application/json'}
          }));
          channel.port1.close();
        };

        // 只向选定的客户端发送消息
				if (subPath.startsWith('/getAccessToken')) {
					console.log("开始获取accessToken:", requestUrl);
					targetClient.then(client => {
						client.postMessage({
							type: 'GET_DATA',
							port: channel.port2
						}, [channel.port2]); // 转移端口所有权
					});
				}else {
					resolve(createErrorResponse("未知路径", 400));
				}

      } catch (error) {
        console.error("处理请求时出错:", error);
        resolve(createErrorResponse("服务不可用", 503));
      }
    })());
  });
}

// 创建错误响应的辅助函数
function createErrorResponse(message, status = 503) {
  return new Response(JSON.stringify({ error: message }), {
    status: status,
    headers: {'Content-Type': 'application/json'}
  });
}
