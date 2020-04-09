package club.throwable.quartz.kit.utils;

import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 22:55
 */
public enum BeanCopierUtils {

    // 单例
    X;

    private static final Map<String, BeanCopier> CACHE = new ConcurrentHashMap<>();

    public void copy(Object source, Object target) {
        String key = String.format("%s-%s", source.getClass().getName(), target.getClass().getName());
        BeanCopier copier;
        if (CACHE.containsKey(key)) {
            copier = CACHE.get(key);
        } else {
            copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            CACHE.put(key, copier);
        }
        copier.copy(source, target, null);
    }
}
