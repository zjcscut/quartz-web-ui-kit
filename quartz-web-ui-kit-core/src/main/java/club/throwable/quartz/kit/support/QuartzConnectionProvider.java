package club.throwable.quartz.kit.support;

import com.zaxxer.hikari.HikariDataSource;
import org.quartz.utils.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:24
 */
public class QuartzConnectionProvider implements ConnectionProvider {

    private DataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {
        Optional.ofNullable(dataSource).ifPresent(ds -> {
            if (ds instanceof HikariDataSource) {
                ((HikariDataSource) ds).close();
            }
        });
    }

    @Override
    public void initialize() throws SQLException {
        this.dataSource = DataSourceHolder.DATA_SOURCE;
        if (null == this.dataSource) {
            throw new IllegalArgumentException("DataSource not initialize...");
        }
    }
}
