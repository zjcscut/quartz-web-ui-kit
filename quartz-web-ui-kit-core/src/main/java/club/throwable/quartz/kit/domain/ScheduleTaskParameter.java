package club.throwable.quartz.kit.domain;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:08
 */
@Data
public class ScheduleTaskParameter {

    private Long id;

    private String taskId;

    private String parameterValue;
}
