package cn.fh.chat.codec;

import cn.fh.chat.protocol.BinProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by whf on 1/30/16.
 */
public class BinProtocolEncoder extends MessageToByteEncoder<BinProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BinProtocol msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getHeader().getSid().getBytes());
        out.writeInt(msg.getLength());
        out.writeLong(msg.getHeader().getSentTime().getTime());
        out.writeBytes(msg.getHeader().getType().code().getBytes());
        out.writeInt(msg.getHeader().getTargetUserId());
        out.writeInt(msg.getHeader().getTargetRoomId());
        out.writeInt(msg.getHeader().getMemId());

        out.writeBytes(msg.getBody().getBytes());
    }
}
