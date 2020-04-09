package club.throwable.quartz.kit.service.vo;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 22:56
 */
@Data
public class ScheduleTaskInfoVo {

    private Long id;

    private String creator;

    private String editor;

    private OffsetDateTime createTime;

    private OffsetDateTime editTime;

    private Long version;

    private Integer deleted;

    private String taskId;

    private String taskClass;

    private String taskType;

    private String taskGroup;

    private String taskExpression;

    private Integer taskStatus;

    private String taskDescription;

    private String taskParameter;
}
