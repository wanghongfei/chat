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

    public DataRepo(int cap) {
        this.memMap = new ConcurrentHashMap<>(cap);
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
        return memMap.get(id);
    }

    public void remove(Integer id) {
        memMap.remove(id);
    }

}
