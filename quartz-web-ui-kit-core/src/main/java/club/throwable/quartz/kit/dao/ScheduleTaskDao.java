package club.throwable.quartz.kit.dao;

import club.throwable.quartz.kit.common.ScheduleTaskStatus;
import club.throwable.quartz.kit.domain.ScheduleTask;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:10
 */
@RequiredArgsConstructor
public class ScheduleTaskDao {

    private final JdbcTemplate jdbcTemplate;

    private static final ResultSetConverter<ScheduleTask> CONVERTER = rs -> {
        ScheduleTask task = new ScheduleTask();
        task.setId(rs.getLong("id"));
        task.setCreator(rs.getString("creator"));
        task.setEditor(rs.getString("editor"));
        Optional.ofNullable(rs.getTimestamp("create_time")).ifPresent(
                x -> task.setCreateTime(new Date(x.getTime()))
        );
        Optional.ofNullable(rs.getTimestamp("edit_time")).ifPresent(
                x -> task.setEditTime(new Date(x.getTime()))
        );
        task.setVersion(rs.getLong("version"));
        task.setDeleted(rs.getInt("deleted"));
        task.setTaskId(rs.getString("task_id"));
        task.setTaskClass(rs.getString("task_class"));
        task.setTaskType(rs.getString("task_type"));
        task.setTaskGroup(rs.getString("task_group"));
        task.setTaskExpression(rs.getString("task_expression"));
        task.setTaskDescription(rs.getString("task_description"));
        task.setTaskStatus(rs.getInt("task_status"));
        return task;
    };

    private static final ResultSetExtractor<List<ScheduleTask>> MULTI = rs -> {
        List<ScheduleTask> tasks = new ArrayList<>();
        while (rs.next()) {
            tasks.add(CONVERTER.convert(rs));
        }
        return tasks;
    };

    private static final ResultSetExtractor<ScheduleTask> SINGLE = rs -> {
        if (rs.next()) {
            return CONVERTER.convert(rs);
        }
        return null;
    };

    public List<ScheduleTask> selectTasksByStatus(String taskGroup, ScheduleTaskStatus status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM schedule_task WHERE deleted = 0 AND task_group = ?");
        Optional.ofNullable(status).ifPresent(scheduleTaskStatus -> sql.append(" AND task_group = ?"));
        Optional.ofNullable(status).ifPresent(scheduleTaskStatus -> sql.append(" AND task_status = ?"));
        return jdbcTemplate.query(sql.toString(), p -> {
            int index = 1;
            if (null != taskGroup) {
                p.setString(index++, taskGroup);
            }
            if (null != status) {
                p.setInt(index, status.getStatus());
            }
        }, MULTI);
    }

    public ScheduleTask selectByTaskId(String taskId) {
        String sql = "SELECT * FROM schedule_task WHERE deleted = 0 AND task_id = ?";
        return jdbcTemplate.query(sql, p -> p.setString(1, taskId), SINGLE);
    }

    public int deleteByTaskId(String taskId) {
        String sql = "DELETE FROM schedule_task WHERE task_id = ?";
        return jdbcTemplate.update(sql, p -> p.setString(1, taskId));
    }

    public int insertSelective(ScheduleTask task) {
        StringBuilder sql = new StringBuilder("INSERT INTO schedule_task(");
        Cursor cursor = new Cursor();
        if (null != task.getId()) {
            cursor.add();
            sql.append("id,");
        }
        if (null != task.getCreator()) {
            cursor.add();
            sql.append("creator,");
        }
        if (null != task.getEditor()) {
            cursor.add();
            sql.append("editor,");
        }
        if (null != task.getCreateTime()) {
            cursor.add();
            sql.append("create_time,");
        }
        if (null != task.getEditTime()) {
            cursor.add();
            sql.append("edit_time,");
        }
        if (null != task.getVersion()) {
            cursor.add();
            sql.append("version,");
        }
        if (null != task.getDeleted()) {
            cursor.add();
            sql.append("deleted,");
        }
        if (null != task.getTaskId()) {
            cursor.add();
            sql.append("task_id,");
        }
        if (null != task.getTaskClass()) {
            cursor.add();
            sql.append("task_class,");
        }
        if (null != task.getTaskType()) {
            cursor.add();
            sql.append("task_type,");
        }
        if (null != task.getTaskGroup()) {
            cursor.add();
            sql.append("task_group,");
        }
        if (null != task.getTaskExpression()) {
            cursor.add();
            sql.append("task_expression,");
        }
        if (null != task.getTaskStatus()) {
            cursor.add();
            sql.append("task_status,");
        }
        if (null != task.getTaskDescription()) {
            cursor.add();
            sql.append("task_description,");
        }
        StringBuilder realSql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")));
        realSql.append(") VALUES (");
        int idx = cursor.idx();
        for (int i = 0; i < idx; i++) {
            if (i != idx - 1) {
                realSql.append("?,");
            } else {
                realSql.append("?");
            }
        }
        realSql.append(")");
        if (null != task.getId()) {
            return jdbcTemplate.update(realSql.toString(), ps -> setPreparedStatementParameters(ps, task));
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int count = jdbcTemplate.update(p -> {
                PreparedStatement ps = p.prepareStatement(realSql.toString(), Statement.RETURN_GENERATED_KEYS);
                setPreparedStatementParameters(ps, task);
                return ps;
            }, keyHolder);
            task.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return count;
        }
    }

    private void setPreparedStatementParameters(PreparedStatement ps, ScheduleTask task) throws SQLException {
        int index = 1;
        if (null != ps) {
            if (null != task.getId()) {
                ps.setLong(index++, task.getId());
            }
            if (null != task.getCreator()) {
                ps.setString(index++, task.getCreator());
            }
            if (null != task.getEditor()) {
                ps.setString(index++, task.getEditor());
            }
            if (null != task.getCreateTime()) {
                ps.setTimestamp(index++, Timestamp.from(task.getCreateTime().toInstant()));
            }
            if (null != task.getEditTime()) {
                ps.setTimestamp(index++, Timestamp.from(task.getEditTime().toInstant()));
            }
            if (null != task.getVersion()) {
                ps.setLong(index++, task.getVersion());
            }
            if (null != task.getDeleted()) {
                ps.setLong(index++, task.getDeleted());
            }
            if (null != task.getTaskId()) {
                ps.setString(index++, task.getTaskId());
            }
            if (null != task.getTaskClass()) {
                ps.setString(index++, task.getTaskClass());
            }
            if (null != task.getTaskType()) {
                ps.setString(index++, task.getTaskType());
            }
            if (null != task.getTaskGroup()) {
                ps.setString(index++, task.getTaskGroup());
            }
            if (null != task.getTaskExpression()) {
                ps.setString(index++, task.getTaskExpression());
            }
            if (null != task.getTaskStatus()) {
                ps.setInt(index++, task.getTaskStatus());
            }
            if (null != task.getTaskDescription()) {
                ps.setString(index, task.getTaskDescription());
            }
        }
    }

    public ScheduleTask selectByTaskClassAndGroup(String taskClass, String taskGroup) {
        String sql = "SELECT * FROM schedule_task WHERE deleted = 0 AND task_class = ? AND task_group = ?";
        return jdbcTemplate.query(sql, p -> {
                    p.setString(1, taskClass);
                    p.setString(2, taskGroup);
                },
                SINGLE);
    }

    public int updateByPrimaryKeySelective(ScheduleTask task) {
        StringBuilder sql = new StringBuilder("UPDATE schedule_task SET ");
        if (null != task.getTaskStatus()) {
            sql.append("task_status = ?,");
        }
        if (null != task.getTaskDescription()) {
            sql.append("task_description = ?,");
        }
        if (null != task.getTaskExpression()) {
            sql.append("task_expression = ?,");
        }
        if (null != task.getVersion()) {
            sql.append("version = ?,");
        }
        return jdbcTemplate.update(sql.substring(0, sql.lastIndexOf(",")) + " WHERE id = ?", p -> {
            int index = 1;
            if (null != task.getTaskStatus()) {
                p.setInt(index++, task.getTaskStatus());
            }
            if (null != task.getTaskDescription()) {
                p.setString(index++, task.getTaskDescription());
            }
            if (null != task.getTaskExpression()) {
                p.setString(index++, task.getTaskExpression());
            }
            if (null != task.getVersion()) {
                p.setLong(index++, task.getVersion());
            }
            if (null != task.getId()) {
                p.setLong(index, task.getId());
            }
        });
    }
}
