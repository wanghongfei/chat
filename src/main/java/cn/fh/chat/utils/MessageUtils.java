package cn.fh.chat.utils;

import cn.fh.chat.constants.AttrKey;
import cn.fh.chat.domain.Member;
import cn.fh.chat.domain.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

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

    /**
     * 从上下文中取出对应的Member对象
     * @param ctx
     * @return
     */
    public static Member getFromCtx(ChannelHandlerContext ctx) {
        return (Member) ctx.attr(AttributeKey.valueOf(AttrKey.USER.toString())).get();
    }
}
