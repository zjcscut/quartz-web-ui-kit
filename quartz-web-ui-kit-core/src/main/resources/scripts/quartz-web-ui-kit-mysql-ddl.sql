CREATE TABLE `schedule_task`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `creator`          VARCHAR(16)     NOT NULL DEFAULT 'admin' COMMENT '创建人',
    `editor`           VARCHAR(16)     NOT NULL DEFAULT 'admin' COMMENT '修改人',
    `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `edit_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`          BIGINT          NOT NULL DEFAULT 1 COMMENT '版本号',
    `deleted`          TINYINT         NOT NULL DEFAULT 0 COMMENT '软删除标识',
    `task_id`          VARCHAR(64)     NOT NULL COMMENT '任务标识',
    `task_class`       VARCHAR(256)    NOT NULL COMMENT '任务类',
    `task_type`        VARCHAR(16)     NOT NULL COMMENT '任务类型,CRON,SIMPLE',
    `task_group`       VARCHAR(32)     NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
    `task_expression`  VARCHAR(256)    NOT NULL COMMENT '任务表达式',
    `task_description` VARCHAR(256) COMMENT '任务描述',
    `task_status`      TINYINT         NOT NULL DEFAULT 0 COMMENT '任务状态',
    UNIQUE uniq_task_class_task_group (`task_class`, `task_group`),
    UNIQUE uniq_task_id (`task_id`)
) COMMENT '调度任务';

CREATE TABLE `schedule_task_parameter`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `task_id`         VARCHAR(64)     NOT NULL COMMENT '任务标识',
    `parameter_value` VARCHAR(1024)   NOT NULL COMMENT '参数值',
    UNIQUE uniq_task_id (`task_id`)
) COMMENT '调度任务参数';