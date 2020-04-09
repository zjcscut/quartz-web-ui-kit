package club.throwable.quartz.kit.support;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author throwable
 * @version v1.0
 * @description MDC工具,添加调用轨迹
 * @since 2020/4/6 13:48
 */
public enum MappedDiagnosticContextAssistant {

    X;

    public void processInMappedDiagnosticContext(Runnable runnable) {
        String uuid = UUID.randomUUID().toString();
        MDC.put("TRACE_ID", uuid);
        try {
            runnable.run();
        } finally {
            MDC.remove("TRACE_ID");
        }
    }
}
