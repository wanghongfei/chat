package cn.fh.chat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by whf on 9/13/15.
 */
public class DateUtils {
    private DateUtils() {}

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 得到当前时间格式化后的字符串
     * @return
     */
    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        return sdf.format(new Date());
    }
}
