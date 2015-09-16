package cn.fh.chat.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by whf on 9/13/15.
 */
public class Member {
    private Integer id;

    private String nickname;

    private Integer gender;

    private Integer age;

    private String location;

    private String token;

    @JSONField(serialize = false)
    private ChannelHandlerContext ctx;

    @JSONField(serialize = false)
    /**
     * 记录当前用户所在的聊天室
     */
    private List<Integer> roomList;

    public Member() {}

    public Member(Integer id, String nickname, Integer gender, Integer age, String location, String token) {
        this.id = id;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.location = location;
        this.token = token;
    }

    public Member(Integer id, String nickname, Integer gender, Integer age, String location, String token, ChannelHandlerContext ctx) {
        this(id, nickname, gender, age, location, token);

        this.ctx = ctx;
    }

    public static Member clone(Member other) {
        return new Member(other.id, other.nickname, other.age, other.age, other.location, other.token, other.ctx);
    }

    /**
     * 将json转换成Member对象
     * @param json
     * @return
     * @throws Exception
     */
    public static Member fromJson(String json) throws Exception {
        return JSON.parseObject(json, Member.class);
    }

    public static Member fromJson(String json, ChannelHandlerContext ctx) throws Exception {
        Member m = JSON.parseObject(json, Member.class);
        m.setCtx(ctx);

        return m;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (id != null ? !id.equals(member.id) : member.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public List<Integer> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Integer> roomList) {
        this.roomList = roomList;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
