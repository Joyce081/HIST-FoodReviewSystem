package com.ren.common.utils.sse;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ren.common.utils.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class FluxMessageUtils {

    private final Map<String, Sinks.Many<String>> sinkMap = new ConcurrentHashMap<>();
    private final Sinks.Many<String> broadcastSink = Sinks.many().multicast().onBackpressureBuffer();

    /**
     * 创建Flux
     * @param sink
     * @return reactor.core.publisher.Flux<java.lang.String>
     * @author ren
     * @date 2025/12/15
     */
    private Flux<String> createFluxInternal(Sinks.Many<String> sink) {
        Flux<String> businessStream = sink.asFlux()
                .doOnCancel(() -> {
                    // 可以根据需要添加取消时的清理逻辑
                    if (sink != broadcastSink && sink.currentSubscriberCount() == 0) {
                        // 考虑是否立即清理无订阅者的Sink
                    }
                });

        Flux<String> heartbeatStream = Flux.interval(Duration.ofSeconds(30))
                .map(seq -> "event: heartbeat\ndata: " + System.currentTimeMillis() + "\n\n");

        return Flux.merge(businessStream, heartbeatStream)
                .onErrorResume(e -> {
                    // 错误处理
                    return Flux.empty();
                });
    }

    /**
     * 根据SinkName创建Flux，如果sinkName为空，则创建全局的Flux
     * @param sinkName
     * @return reactor.core.publisher.Flux<java.lang.String>
     * @author ren
     * @date 2025/12/15
     */
    public Flux<String> createFlux(String sinkName) {
        if (StringUtils.isBlank(sinkName)) {
            throw new IllegalArgumentException("SinkName cannot be empty");
        }
        Sinks.Many<String> sink = getOrCreateSink(sinkName);
        return createFluxInternal(sink);
    }

    /**
     * 创建全局Flux
     * @return reactor.core.publisher.Flux<java.lang.String>
     * @author ren
     * @date 2025/12/15
     */
    public Flux<String> createBroadcastFlux() {
        return createFluxInternal(broadcastSink);
    }

    /**
     * 创建并返回Sink
     * @param sinkName
     * @return reactor.core.publisher.Sinks.Many<java.lang.String>
     * @author ren
     * @date 2025/12/15
     */
    public Sinks.Many<String> getOrCreateSink(String sinkName) {
        if (StringUtils.isBlank(sinkName)) {
            throw new IllegalArgumentException("SinkName cannot be empty");
        }
        return sinkMap.computeIfAbsent(sinkName,key -> Sinks.many().multicast().onBackpressureBuffer());
    }

    /**
     * 获取已经存在的Sink
     * @param sinkName
     * @return reactor.core.publisher.Sinks.Many<java.lang.String>
     * @author ren
     * @date 2025/12/15
     */
    public Sinks.Many<String> getSink(String sinkName) {
        if (StringUtils.isBlank(sinkName)) {
            throw new IllegalArgumentException("SinkName cannot be empty");
        }
        return sinkMap.get(sinkName);
    }

    /**
     * 查询指定Sink的当前活跃连接数
     * @param sinkName
     * @return int
     * @author ren
     * @date 2025/12/15
     */
    public int getSubscriberCount(String sinkName) {
        if (StringUtils.isBlank(sinkName)) {
            throw new IllegalArgumentException("SinkName cannot be empty");
        }
        Sinks.Many<String> sink = getSink(sinkName);
        return sink != null ? sink.currentSubscriberCount() : 0;
    }

    /**
     * 移除不再使用的Sink
     * @param sinkName
     * @author ren
     * @date 2025/12/15
     */
    public void removeSink(String sinkName) {
        if (StringUtils.isBlank(sinkName)) {
            throw new IllegalArgumentException("SinkName cannot be empty");
        }
        int count = getSubscriberCount(sinkName);
        if (count > 0) {
            throw new RuntimeException("Cannot remove sink with active subscribers");
        }
        Sinks.Many<String> sink = sinkMap.remove(sinkName);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    /**
     * 清理没有订阅者的空闲Sink，防止内存泄漏
     * @author ren
     * @date 2025/12/15
     */
    public void cleanupIdleSinks() {
        Iterator<Map.Entry<String, Sinks.Many<String>>> iterator = sinkMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Sinks.Many<String>> entry = iterator.next();
            if (entry.getValue().currentSubscriberCount() == 0) {
                entry.getValue().tryEmitComplete();
                iterator.remove();
            }
        }
    }

    /**
     * 发送消息
     * @param sinkName
     * @param message
     * @return boolean
     * @author ren
     * @date 2025/12/15
     */
    public boolean sendMessage(String sinkName, String message) {
        if (StringUtils.isBlank(sinkName)) {
            // 发送到广播
            Sinks.EmitResult result = broadcastSink.tryEmitNext(message);
            return result == Sinks.EmitResult.OK;
        }

        Sinks.Many<String> sink = sinkMap.get(sinkName);
        if (sink != null) {
            Sinks.EmitResult result = sink.tryEmitNext(message);
            return result == Sinks.EmitResult.OK;
        }
        return false;
    }

}
