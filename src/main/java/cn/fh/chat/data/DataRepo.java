package cn.fh.chat.data;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by whf on 9/10/15.
 */
public class DataRepo {
    public static AtomicInteger memIdSequence = new AtomicInteger(0);

    public static AtomicInteger roomIdSequence = new AtomicInteger(0);

    /**
     * 保存从token到上下文的映射
     */
    private Map<String, ChannelHandlerContext> userRepo;

    /**
     * 保存从memberId到token的映射
     */
    private Map<Integer, String> tokenMap;

    /**
     * 保存聊天室id到用户集合的映射
     */
    private Map<Integer, Set<String>> roomMap;

    public DataRepo(int cap) {
        this.userRepo = new ConcurrentHashMap<>(cap);
        this.tokenMap = new ConcurrentHashMap<>(cap);
        this.roomMap = new ConcurrentHashMap<>(cap);
    }

    public DataRepo() {
        this(50);
    }

    public ChannelHandlerContext getCtx(String token) {
        return userRepo.get(token);
    }

    public void putCtx(String token, ChannelHandlerContext ctx) {
        userRepo.put(token, ctx);
    }

    public String getToken(Integer memId) {
        return tokenMap.get(memId);
    }

    public void putToken(Integer memId, String token) {
        tokenMap.put(memId, token);
    }

    public Set<String> getRoomTokens(Integer roomId) {
        return roomMap.get(roomId);
    }

    public int sizeOfOnlineUser() {
        return this.userRepo.size();
    }

}
