package club.throwable.quartz.kit.configuration;

import lombok.Builder;
import lombok.Getter;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 13:33
 */
@Builder
@Getter
public class QuartzDataSourceProperties {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
