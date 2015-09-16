package cn.fh.chat.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Created by whf on 9/16/15.
 */
public class CollectionUtils {
    private CollectionUtils() {}

    /**
     * 从集合中删除元素，条件由Predicate指明
     * @param collection
     * @param predicate
     * @param <T>
     * @return 返回被删除的元素
     */
    public static <T> T removeFrom(Iterable<T> collection, Predicate<T> predicate) {
        Iterator<T> it = collection.iterator();

        while (it.hasNext()) {
            T o = it.next();

            if (predicate.test(o)) {
                it.remove();
                return o;
            }
        }

        return null;
    }

}
