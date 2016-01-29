package cn.fh.chat.codec;

import cn.fh.chat.protocol.BinHeader;
import cn.fh.chat.protocol.BinProtocol;
import cn.fh.chat.protocol.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 解码器
 * Created by whf on 1/29/16.
 */
public class BinProtocolDecoder extends ByteToMessageDecoder {
    private static Logger log = LoggerFactory.getLogger(BinProtocolDecoder.class);

    public static int HEADER_LENGTH = 36;
    public static int SID_LENGTH = 10;
    public static int TYPE_LENGTH = 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("message received");

        // 读取请求头
        if (in.readableBytes() >= HEADER_LENGTH) {
            // 读取sid
            String sid = readAsStr(in, SID_LENGTH);
            // 数据名总长度
            int length = in.readInt();
            long sentTime = in.readLong();
            String type = readAsStr(in, TYPE_LENGTH);
            int targetUserId = in.readInt();
            int targetRoomId = in.readInt();
            int memId = in.readInt();


            // 读取请求体
            int bodyLength = length - HEADER_LENGTH;
            String body = null;
            if (in.readableBytes() >= bodyLength) {
                body = readAsStr(in, bodyLength);

            } else {
                // 数据包不完整
                // 重置read index
                in.resetReaderIndex();
                log.info("{} bytes for body are not enough", bodyLength);
                return;
            }

            // 封装成对象
            BinHeader header = new BinHeader();
            header.setSid(sid);
            header.setLength(length);
            header.setType(MessageType.fromCode(type));
            header.setTargetUserId(targetUserId);
            header.setTargetRoomId(targetRoomId);
            header.setMemId(memId);
            header.setSentTime(new Date(sentTime));

            BinProtocol msg = new BinProtocol();
            msg.setHeader(header);
            msg.setBody(body);

            out.add(msg);

            log.info("decoding succeeded");
            log.debug(msg.toString());

            return;
        }

        log.info("{} bytes are not engouth", in.readableBytes());
    }

    private String readAsStr(ByteBuf in, int length) {
        StringBuilder sid = new StringBuilder(length);

        sid.append(in.readBytes(length));

        return sid.toString();
    }
}
