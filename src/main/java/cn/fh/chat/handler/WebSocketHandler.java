package cn.fh.chat.handler;

import cn.fh.chat.constants.ErrorCode;
import cn.fh.chat.constants.MsgType;
import cn.fh.chat.data.DataRepo;
import cn.fh.chat.domain.Header;
import cn.fh.chat.domain.Member;
import cn.fh.chat.domain.Message;
import cn.fh.chat.domain.ServerResponse;
import cn.fh.chat.exception.InvalidContentException;
import cn.fh.chat.exception.NotExistException;
import cn.fh.chat.utils.CodingUtils;
import cn.fh.chat.utils.DateUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 事件处理类，封装了业务逻辑
 * Created by whf on 9/9/15.
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private WebSocketServerHandshaker handshaker;
    private DataRepo repo;

    public WebSocketHandler(DataRepo repo) {
        this.repo = repo;
    }

    public WebSocketHandler() {

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭连接
        ctx.channel().closeFuture();

        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("message received");

        if (msg instanceof FullHttpRequest) {
            logger.info("HTTP message ");
            doHttpRequest(ctx, (FullHttpRequest) msg);
            return;
        }

        if (msg instanceof WebSocketFrame) {
            logger.info("WebSocket message");
            doWebSocket(ctx, (WebSocketFrame) msg);
            return;
        }

    }

    /**
     * 处理HTTP数据
     * @param ctx
     * @param req
     * @throws Exception
     */
    private void doHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 不是握手请求
        if (false == "websocket".equals(req.headers().get("Upgrade"))) {
            if (logger.isDebugEnabled()) {
                logger.debug("not handshake request, return error");
            }

            sendHttpResponse(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("starting handshake");
        }

        // 握手
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/chat", null, false
        );

        handshaker = factory.newHandshaker(req);
        // 握手失败
        if (null == handshaker) {
            if (logger.isDebugEnabled()) {
                logger.debug("handshake failed");
            }

            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        handshaker.handshake(ctx.channel(), req);
        if (logger.isDebugEnabled()) {
            logger.debug("handshake succeeded");
        }

    }

    /**
     * 处理websocket数据
     * @param ctx
     * @param frame
     */
    private void doWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 是否是关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());

            return;
        }

        // 是否是ping
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain())
            );

            return;
        }

        // 是文本消息
        if (frame instanceof TextWebSocketFrame) {
            // 取出数据
            String request = ((TextWebSocketFrame) frame).text();

            if (logger.isDebugEnabled()) {
                logger.debug("recv: {}", request);
            }

            // decode
            Message msg = CodingUtils.decode(request);
            // 消息内容格式不正确
            if (null == msg) {
                if (logger.isDebugEnabled()) {
                    logger.debug("decoding failed");
                }

                sendWebSocketResponse(ctx, ErrorCode.BAD_CONTENT, null);
                return;
            }


            // 分发信息
            try {
                dispatchMessage(ctx, msg);
            } catch (InvalidContentException e) {
                e.printStackTrace();
                sendWebSocketResponse(ctx, ErrorCode.FAILED, null);

            } catch (NotExistException e) {
                e.printStackTrace();
                sendWebSocketResponse(ctx, ErrorCode.FAILED, null);

            } finally {
            }

            return;
        }

        ctx.channel().writeAndFlush(new TextWebSocketFrame("unsupported"));
    }

    /**
     * 发送websocket信息
     * @param ctx
     * @param code
     * @param data 包含的数据，可为null
     */
    private void sendWebSocketResponse(ChannelHandlerContext ctx, ErrorCode code, Object data) {
        TextWebSocketFrame frame = null;

        if (logger.isDebugEnabled()) {
            logger.debug("sending: {}, {}", code.getCode(), data);
        }

        if (null != data) {
            frame = new TextWebSocketFrame(
                    JSON.toJSONString(
                            new ServerResponse(data)
                    )
            );
        } else {
            frame = new TextWebSocketFrame(
                    JSON.toJSONString(
                            new ServerResponse(code)
                    )
            );
        }

        ctx.channel().writeAndFlush(frame);
    }

    /**
     * 发送HTTP错误响应信息，并关闭连接
     * @param ctx
     * @param status
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        ctx.writeAndFlush(resp)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 分发信息
     */
    private void dispatchMessage(ChannelHandlerContext ctx, Message msg)
            throws InvalidContentException, NotExistException {

        if (false == validateMessage(msg)) {
            throw new InvalidContentException("message is invalid");
        }

        MsgType type = MsgType.toType(msg.getHeader().getType());
        switch (type) {
            case HANDSHAKE:
                sendToken(ctx, 30, msg);
                break;

            case PRIVATE:
                sendPrivateMessage(ctx, msg);
                break;

            case ROOM:
                sendRoomMessage(ctx, msg);
                break;

            case QUERY_ONLINE:
                sendOnlineUser(ctx);
                break;
        }
    }

    private void sendOnlineUser(ChannelHandlerContext ctx) {
        Header header = new Header();
        header.setServerTime(DateUtils.now());
        // TODO

        List<Member> mList = repo.onlineUserList();
        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, mList);
    }

    /**
     * 生成当前用户的memberId和token
     * @param ctx
     * @param count
     */
    private void sendToken(ChannelHandlerContext ctx, int count, Message msg) {
        // 生成token
        String token = RandomStringUtils.randomAlphabetic(count);
        //String token = "token";
        // 生成memberId
        Integer memId = DataRepo.memIdSequence.getAndIncrement();

        Member mem = null;
        try {
            mem = Member.fromJson(msg.getContent());
        } catch (Exception ex) {
            ex.printStackTrace();
            sendWebSocketResponse(ctx, ErrorCode.BAD_CONTENT, null);

            return;
        }

        mem.setId(memId);
        mem.setToken(token);
        repo.putToken(memId, mem);
        repo.putCtx(token, ctx);

        // 把结果转换成json字符串返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("memberId", memId);
/*        JSONObject jsonObject = new JSONObject(map);

        String json = jsonObject.toJSONString();
        if (logger.isDebugEnabled()) {
            logger.debug("handshake: {}", json);
        }*/

        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, map);
    }

    private void sendRoomMessage(ChannelHandlerContext ctx, Message msg) throws NotExistException {
        Set<String> tokenSet = repo.getRoomTokens(msg.getHeader().getTargetRoomId());
        // 聊天室不存在
        if (null == tokenSet) {
            throw new NotExistException();
        }

        // 向该聊天室中所有人发送信息
        for (String token : tokenSet) {
            ChannelHandlerContext targetCtx = repo.getCtx(token);
            targetCtx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
        }

        // 返回发送成功
        ctx.writeAndFlush(new TextWebSocketFrame("ok"));
    }

    private void sendPrivateMessage(ChannelHandlerContext ctx, Message msg) throws NotExistException{
        Member member = repo.getToken(msg.getHeader().getTargetMemId());
        // 用户不存在
        if (null == member || null == member.getToken()) {
            throw new NotExistException();
        }

        ChannelHandlerContext targetCtx = repo.getCtx(member.getToken());
        // 用户不在线
        if (null == targetCtx) {
            throw new NotExistException();
        }

        msg.getHeader().setToken(null);

        // 转发信息给目标用户
        targetCtx.channel().writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(msg))
        );

        // 返回发送成功
        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, null);

    }

    /**
     * 验证消息内容是否合法
     * @param msg
     * @return
     */
    private boolean validateMessage(Message msg) {
        Header header = msg.getHeader();
        if (null == header) {
            return false;
        }

        Integer type = header.getType();
        if (null == type) {
            return false;
        }

        MsgType t = MsgType.toType(type);
        if (null == t) {
            return false;
        }

        if (MsgType.HANDSHAKE == t || MsgType.QUERY_ONLINE == t) {
            return true;
        }

        if (null == header.getClientTime() || null == header.getToken() || null == header.getMemId()) {
            return false;
        }



        switch (t) {
            case PRIVATE:
                if (null == header.getTargetMemId()) {
                    return false;
                }
                break;

            case ROOM:
                if (null == header.getTargetRoomId()) {
                    return false;
                }
                break;
        }

        return true;
    }
}
