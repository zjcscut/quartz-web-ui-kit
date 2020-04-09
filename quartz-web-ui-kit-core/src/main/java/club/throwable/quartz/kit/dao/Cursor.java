package club.throwable.quartz.kit.dao;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 19:41
 */
public final class Cursor {

    private int idx;

    public int add() {
        idx++;
        return idx;
    }

    public int idx() {
        return idx;
    }
}
