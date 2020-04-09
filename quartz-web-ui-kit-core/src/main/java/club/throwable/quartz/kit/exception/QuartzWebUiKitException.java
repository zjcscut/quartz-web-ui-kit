package club.throwable.quartz.kit.exception;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 23:01
 */
public class QuartzWebUiKitException extends RuntimeException {

    public QuartzWebUiKitException(String message) {
        super(message);
    }

    public QuartzWebUiKitException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuartzWebUiKitException(Throwable cause) {
        super(cause);
    }
}
