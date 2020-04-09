package club.throwable.quartz.kit.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 13:38
 */
@Data
@Builder
public class ScheduleTaskInfo {

    private String taskId;

    private String taskClass;

    private String taskType;

    private String taskGroup;

    private String taskExpression;

    private String taskDescription;

    private long cost;

    private long start;

    private long end;

    @JsonIgnore
    private Throwable throwable;
}
