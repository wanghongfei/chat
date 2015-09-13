package cn.fh.chat.data;

import cn.fh.chat.domain.Member;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private Map<Integer, Member> tokenMap;

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

    public Member getToken(Integer memId) {
        Member m = tokenMap.get(memId);

        return Member.clone(m);
    }

    public void putToken(Integer memId, Member member) {
        tokenMap.put(memId, member);
    }

    public Set<String> getRoomTokens(Integer roomId) {
        return roomMap.get(roomId);
    }

    public int sizeOfOnlineUser() {
        return this.userRepo.size();
    }

    public List<Member> onlineUserList() {
        Set<Map.Entry<Integer, Member>> entrySet = tokenMap.entrySet();

        return entrySet.stream()
                .map( entry -> {
                    Member m = entry.getValue();
                    m.setToken(null);
                    return m;
                }).collect(Collectors.toList());
    }

}
