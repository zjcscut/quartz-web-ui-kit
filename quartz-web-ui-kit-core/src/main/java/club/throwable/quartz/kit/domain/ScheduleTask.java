package club.throwable.quartz.kit.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:05
 */
@Data
public class ScheduleTask {

    private Long id;

    private String creator;

    private String editor;

    private Date createTime;

    private Date editTime;

    private Long version;

    private Integer deleted;

    private String taskId;

    private String taskClass;

    private String taskType;

    private String taskGroup;

    private String taskExpression;

    private Integer taskStatus;

    private String taskDescription;
}
