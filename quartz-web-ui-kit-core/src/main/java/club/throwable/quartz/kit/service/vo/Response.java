package club.throwable.quartz.kit.service.vo;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 23:34
 */
@Data
public class Response<T> {

    public static final Long OK = 200L;
    public static final Long ACCEPT = 400L;
    public static final Long ERROR = 500L;

    private Long code;
    private String message;
    private T payload;

    public Response() {
        this(OK);
    }

    public Response(Long code) {
        this(code, (String) null);
    }

    public Response(T payload) {
        this(OK, null, payload);
    }

    public Response(Long code, String message) {
        this(code, message, null);
    }

    public Response(Long code, String message, T payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public static <T> Response<T> succeed() {
        return new Response<>();
    }

    public static <T> Response<T> succeed(T payload) {
        return new Response<>(payload);
    }
}
