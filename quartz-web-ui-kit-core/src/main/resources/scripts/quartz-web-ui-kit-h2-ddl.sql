CREATE TABLE schedule_task
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator          VARCHAR(16),
    editor           VARCHAR(16),
    create_time      TIMESTAMP,
    edit_time        TIMESTAMP,
    version          BIGINT  NOT NULL DEFAULT 1,
    deleted          TINYINT NOT NULL DEFAULT 0,
    task_id          VARCHAR(64),
    task_class       VARCHAR(256),
    task_type        VARCHAR(16),
    task_group       VARCHAR(32),
    task_expression  VARCHAR(256),
    task_description VARCHAR(256),
    task_status      TINYINT
);

CREATE TABLE schedule_task_parameter
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id         VARCHAR(64),
    parameter_value VARCHAR(1024)
);