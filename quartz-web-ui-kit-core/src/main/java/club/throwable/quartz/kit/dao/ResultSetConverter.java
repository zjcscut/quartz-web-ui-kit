package club.throwable.quartz.kit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 17:09
 */
@FunctionalInterface
public interface ResultSetConverter<T> {

    T convert(ResultSet rs) throws SQLException;
}
