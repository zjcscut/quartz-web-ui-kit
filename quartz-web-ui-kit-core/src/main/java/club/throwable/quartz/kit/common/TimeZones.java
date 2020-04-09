package club.throwable.quartz.kit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 12:58
 */
@RequiredArgsConstructor
@Getter
public enum TimeZones {


    // 时区属性
    DEFAULT(ZoneId.systemDefault(), "default timezone", OffsetDateTime.now().getOffset(), TimeZone.getDefault()),

    CHINA(ZoneId.of("Asia/Shanghai"), "中国时区", ZoneOffset.of("+08:00"), TimeZone.getTimeZone("Asia/Shanghai")),

    ;

    private final ZoneId zoneId;
    private final String description;
    private final ZoneOffset offset;
    private final TimeZone timeZone;
}
