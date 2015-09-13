package cn.fh.chat.domain;

import com.alibaba.fastjson.JSON;

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

    /**
     * 将json转换成Member对象
     * @param json
     * @return
     * @throws Exception
     */
    public static Member fromJson(String json) throws Exception {
        return JSON.parseObject(json, Member.class);
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
}
