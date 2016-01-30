package cn.fh.chat.handler;

import cn.fh.chat.protocol.BinHeader;
import cn.fh.chat.protocol.BinProtocol;
import cn.fh.chat.protocol.MessageType;
import io.netty.channel.ChannelFutureListener;
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

        String body = "OK";
        BinHeader header = new BinHeader(
                msg.getHeader().getSid(),
                MessageType.RESPONSE_OK,
                -1,
                -1,
                -1,
                BinHeader.HEADER_LENGTH + body.getBytes().length
        );

        BinProtocol resp = new BinProtocol(header, body);
        ctx.channel().writeAndFlush(resp).addListener( future -> {
            System.out.println("sent");
            ctx.close();
        });

    }
}
