package cn.fh.chat.utils;

import cn.fh.chat.domain.Message;
import com.alibaba.fastjson.JSON;

/**
 * Created by whf on 9/10/15.
 */
public class CodingUtils {
    private CodingUtils() {}

    public static Message decode(String source) {
        Message msg = null;
        try {
            msg = JSON.parseObject(source, Message.class);
        } catch (Exception ex) {
            // 格式错误
            ex.printStackTrace();
        }

        return msg;
    }
}
