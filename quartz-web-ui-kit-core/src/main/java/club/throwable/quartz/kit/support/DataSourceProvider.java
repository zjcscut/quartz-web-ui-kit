package club.throwable.quartz.kit.support;

import club.throwable.quartz.kit.configuration.QuartzDataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:25
 */
public enum DataSourceProvider {

    // 单例
    X;

    public DataSource provideDataSource(QuartzDataSourceProperties properties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getUrl());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setDriverClassName(properties.getDriverClassName());
        hikariConfig.setPassword(properties.getPassword());
        return new HikariDataSource(hikariConfig);
    }
}
