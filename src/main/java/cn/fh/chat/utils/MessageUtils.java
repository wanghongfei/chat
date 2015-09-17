package cn.fh.chat.utils;

import cn.fh.chat.domain.Message;

/**
 * Created by whf on 9/17/15.
 */
public class MessageUtils {
    private MessageUtils() {}

    /**
     * 去掉Message对象中不应该被用户看到的数据
     * @param msg
     */
    public static void eraseSensitiveInfo(Message msg) {
        msg.getHeader().setToken(null);
    }
}
