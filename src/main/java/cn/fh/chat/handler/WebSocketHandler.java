package cn.fh.chat.handler;

import cn.fh.chat.constants.AttrKey;
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
import cn.fh.chat.utils.CollectionUtils;
import cn.fh.chat.utils.DateUtils;
import cn.fh.chat.utils.MessageUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.doclint.HtmlTag;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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


    /**
     * 连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接
        ctx.close().addListener(f -> {
            // 从在线用户中去掉
            Member m = (Member) ctx.attr(AttributeKey.valueOf(AttrKey.USER.toString())).get();
            repo.remove(m.getId());

            // 从聊天室中去掉
            List<Integer> roomList = m.getRoomList();
            if (null != roomList) {
                roomList.forEach(id -> repo.exitRoom(id, m));
            }


            if (logger.isDebugEnabled()) {
                logger.debug("用户{}:{}断开连接", m.getId(), m.getNickname());
            }
        });
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭连接
        ctx.channel().closeFuture();

        super.exceptionCaught(ctx, cause);
    }

    /**
     * 消息处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
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

            case JOIN_ROOM:
                joinRoom(ctx, msg);
                break;

            case EXIT_ROOM:
                exitRoom(ctx, msg);
                break;
        }
    }

    /**
     * 退出聊天室
     * @param ctx
     * @param msg
     */
    protected void exitRoom(ChannelHandlerContext ctx, Message msg) {
        Member m = (Member) ctx.attr(AttributeKey.valueOf(AttrKey.USER.toString()));
        Integer roomId = msg.getHeader().getTargetRoomId();

        // 更新聊天室状态
        repo.exitRoom(roomId, m);
        // 更新用户状态
        List<Integer> roomList = m.getRoomList();
        if (null != roomList) {
            CollectionUtils.removeFrom(roomList, num -> num.equals(roomId));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("用户{}已退出聊天室{}", m, roomId);
        }

        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, null);
    }

    /**
     * 将当前用户加入到聊天室
     * @param ctx
     * @param msg
     */
    protected void joinRoom(ChannelHandlerContext ctx, Message msg) {
        Member m = (Member) ctx.attr(AttributeKey.valueOf(AttrKey.USER.toString())).get();
        Integer roomId = msg.getHeader().getTargetRoomId();

        // 在房间中记录这个用户的加入
        repo.joinRoom(roomId, m);
        // 在用户中记录自己加入了这个房间
        List<Integer> roomList = m.getRoomList();
        if (null == roomId) {
            roomList = new ArrayList<>(3);
            roomList.add(roomId);
        }
        m.setRoomList(roomList);


        if (logger.isDebugEnabled()) {
            logger.debug("用户{}已加入聊天室{}", m, roomId);
        }
        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, null);
    }

    /**
     * 返回所有在线用户信息
     * @param ctx
     */
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
            mem = Member.fromJson(msg.getContent(), ctx);
        } catch (Exception ex) {
            //ex.printStackTrace();
            sendWebSocketResponse(ctx, ErrorCode.BAD_CONTENT, null);

            return;
        }

        mem.setId(memId);
        mem.setToken(token);
        repo.putMember(memId, mem);

        AttributeKey attr = AttributeKey.valueOf(AttrKey.USER.toString());
        ctx.attr(attr).set(mem);

        // 把结果转换成json字符串返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("memberId", memId);

        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, map);
    }

    /**
     * 向聊天室发送信息
     * @param ctx
     * @param msg
     * @throws NotExistException
     */
    private void sendRoomMessage(ChannelHandlerContext ctx, Message msg) throws NotExistException {
        List<Member> memList = repo.getMemberInRoom(msg.getHeader().getTargetRoomId());
        // 聊天室不存在
        if (null == memList) {
            throw new NotExistException();
        }

        MessageUtils.eraseSensitiveInfo(msg);

        // 向该聊天室中所有人发送信息
        Collection<Channel> channelCo = memList.stream()
                .map(mem -> mem.getCtx().channel())
                .collect(Collectors.toSet());
        sendInChannelGroup(ctx, channelCo, msg);

    }

    /**
     * 向个人发送信息
     * @param ctx
     * @param msg
     * @throws NotExistException
     */
    private void sendPrivateMessage(ChannelHandlerContext ctx, Message msg) throws NotExistException{
        // 取出目标用户实体
        Integer target = msg.getHeader().getTargetMemId();
        Member member = repo.getMember(target);

        // 用户不存在
        if (null == member || null == member.getToken()) {
            throw new NotExistException();
        }

        // 取出目标用户的上下文
        ChannelHandlerContext targetCtx = member.getCtx();
        member.setToken(null);
        msg.getHeader().setToken(null);

        // 转发信息给目标用户
        targetCtx.channel().writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(msg))
        );

        // 返回发送成功
        sendWebSocketResponse(ctx, ErrorCode.SUCCESS, null);

    }

    /**
     * 通过ChannelGroup批量发送信息
     * @param ctx
     * @param channelCo
     * @param msg
     */
    private void sendInChannelGroup(ChannelHandlerContext ctx, Collection<Channel> channelCo, Message msg) {
        ChannelGroup g = new DefaultChannelGroup(ctx.executor());
        g.addAll(channelCo);

        g.writeAndFlush( new TextWebSocketFrame(JSON.toJSONString(msg)) )
                .addListener(f -> sendWebSocketResponse(ctx, ErrorCode.SUCCESS, null));

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

            case JOIN_ROOM:
                break;
        }

        return true;
    }
}
