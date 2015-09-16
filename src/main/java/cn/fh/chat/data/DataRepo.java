package cn.fh.chat.data;

import cn.fh.chat.domain.Member;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 保存当前在线的用户信息
 * Created by whf on 9/10/15.
 */
public class DataRepo {
    public static AtomicInteger memIdSequence = new AtomicInteger(0);

    public static AtomicInteger roomIdSequence = new AtomicInteger(0);

    /**
     * 保存从member id到Member对象的映射
     */
    private Map<Integer, Member> memMap;

    /**
     * 保存聊天室状态
     */
    private Map<Integer, List<Member>> roomMap;

    public DataRepo(int cap) {
        this.memMap = new ConcurrentHashMap<>(cap);

        // 新建5个聊天室
        roomMap = new ConcurrentHashMap<>(10);
        for (int ix = 0 ; ix < 5 ; ++ix) {
            roomMap.put(roomIdSequence.getAndIncrement(), new ArrayList<Member>(10));
        }

    }

    public DataRepo() {
        this(50);
    }


    public List<Member> onlineUserList() {
        Set<Map.Entry<Integer, Member>> entrySet = memMap.entrySet();

        return entrySet.stream()
                .map( entry -> {
                    Member m = entry.getValue();
                    m.setToken(null);
                    return m;
                }).collect(Collectors.toList());
    }

    public void putMember(Integer id, Member m) {
        memMap.put(id, m);
    }

    public Member getMember(Integer id) {
        Member m = memMap.get(id);

        return Member.clone(m);
    }

    public void remove(Integer id) {
        memMap.remove(id);
    }

    public List<Member> getMemberInRoom(Integer roomId) {
        return roomMap.get(roomId);
    }

    public void joinRoom(Integer roomId, Member m) {
        roomMap.get(roomId).add(m);
    }

    public void exitRoom(Integer roomId, Member m) {
        List<Member> list = roomMap.get(roomId);

        Iterator<Member> it = list.iterator();
        while (it.hasNext()) {
            Member obj = it.next();
            if (obj.equals(m)) {
                it.remove();
            }
        }
    }
}
