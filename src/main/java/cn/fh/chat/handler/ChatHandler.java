package cn.fh.chat.handler;

import cn.fh.chat.protocol.BinProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by whf on 1/29/16.
 */
public class ChatHandler extends SimpleChannelInboundHandler<BinProtocol> {
    private static Logger log = LoggerFactory.getLogger(ChatHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, BinProtocol msg) throws Exception {
        log.info("message = {}", msg);

        ctx.channel().close();
    }
}
