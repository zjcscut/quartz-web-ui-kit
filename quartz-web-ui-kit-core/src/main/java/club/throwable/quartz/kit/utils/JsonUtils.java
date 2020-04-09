package club.throwable.quartz.kit.utils;

import club.throwable.quartz.kit.common.TimeZones;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 16:30
 */
public enum JsonUtils {

    // 单例
    X;

    static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.setTimeZone(TimeZones.DEFAULT.getTimeZone());
    }

    public String format(Object target) {
        try {
            return OBJECT_MAPPER.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T parse(String target, Class<T> klass) {
        try {
            return OBJECT_MAPPER.readValue(target, klass);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T parse(String target, TypeReference<T> reference) {
        try {
            return OBJECT_MAPPER.readValue(target, reference);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
