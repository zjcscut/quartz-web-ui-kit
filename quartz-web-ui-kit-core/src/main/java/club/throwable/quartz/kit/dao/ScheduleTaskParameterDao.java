package club.throwable.quartz.kit.dao;

import club.throwable.quartz.kit.domain.ScheduleTaskParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:10
 */
@RequiredArgsConstructor
public class ScheduleTaskParameterDao {

    private final JdbcTemplate jdbcTemplate;

    private static final ResultSetConverter<ScheduleTaskParameter> CONVERTER = rs -> {
        ScheduleTaskParameter parameter = new ScheduleTaskParameter();
        parameter.setId(rs.getLong("id"));
        parameter.setTaskId(rs.getString("task_id"));
        parameter.setParameterValue(rs.getString("parameter_value"));
        return parameter;
    };

    private static final ResultSetExtractor<ScheduleTaskParameter> SINGLE = rs -> {
        if (rs.next()) {
            return CONVERTER.convert(rs);
        }
        return null;
    };

    public int insertSelective(ScheduleTaskParameter parameter) {
        StringBuilder sql = new StringBuilder("INSERT INTO schedule_task_parameter(");
        Cursor cursor = new Cursor();
        if (null != parameter.getId()) {
            cursor.add();
            sql.append("id,");
        }
        if (null != parameter.getTaskId()) {
            cursor.add();
            sql.append("task_id,");
        }
        if (null != parameter.getParameterValue()) {
            cursor.add();
            sql.append("parameter_value,");
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
        if (null != parameter.getId()) {
            return jdbcTemplate.update(realSql.toString(), ps -> setPreparedStatementParameters(ps, parameter));
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int count = jdbcTemplate.update(p -> {
                PreparedStatement ps = p.prepareStatement(realSql.toString(), Statement.RETURN_GENERATED_KEYS);
                setPreparedStatementParameters(ps, parameter);
                return ps;
            }, keyHolder);
            parameter.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return count;
        }
    }

    private void setPreparedStatementParameters(PreparedStatement ps, ScheduleTaskParameter parameter) throws SQLException {
        int index = 1;
        if (null != ps) {
            if (null != parameter.getId()) {
                ps.setLong(index++, parameter.getId());
            }
            if (null != parameter.getTaskId()) {
                ps.setString(index++, parameter.getTaskId());
            }
            if (null != parameter.getParameterValue()) {
                ps.setString(index, parameter.getParameterValue());
            }
        }
    }

    public ScheduleTaskParameter selectByTaskId(String taskId) {
        String sql = "SELECT * FROM schedule_task_parameter WHERE task_id = ?";
        return jdbcTemplate.query(sql, p -> p.setString(1, taskId), SINGLE);
    }

    public int deleteByTaskId(String taskId) {
        String sql = "DELETE FROM schedule_task_parameter WHERE task_id = ?";
        return jdbcTemplate.update(sql, p -> p.setString(1, taskId));
    }

    public int updateByPrimaryKeySelective(ScheduleTaskParameter parameter){
        return jdbcTemplate.update("UPDATE schedule_task_parameter SET parameter_value = ? WHERE id = ?", p-> {
            p.setString(1, parameter.getParameterValue());
            p.setLong(2, parameter.getId());
        });
    }
}
