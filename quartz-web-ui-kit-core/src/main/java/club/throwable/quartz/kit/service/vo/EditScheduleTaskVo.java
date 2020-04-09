package club.throwable.quartz.kit.service.vo;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 22:51
 */
@Data
public class EditScheduleTaskVo {

    private String taskId;
    private String taskDescription;
    private String taskExpression;
    private Integer taskStatus;
    private String taskParameter;
}
