package club.throwable.quartz.kit.configuration;

import lombok.Data;

import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 12:57
 */
@Data
public class QuartzWebUiKitProperties {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    /**
     * 用于覆盖的k-v
     */
    private Map<String, String> pairs;
}
