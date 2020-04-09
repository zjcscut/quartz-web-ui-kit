package club.throwable.quartz.kit.service.vo;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 22:50
 */
@Data
public class AddScheduleTaskVo {

    private String taskDescription;
    private String taskExpression;
    private String taskClass;
    private Integer taskStatus;
    private String taskParameter;
}
