package cn.fh.chat.domain;

/**
 * Created by whf on 9/10/15.
 */
public class Header {
    /**
     * 发送者的token
     */
    private String token;
    /**
     * 发送者的用户id
     */
    private String memId;
    /**
     * 发送者昵称，optional
     */
    private String memName;

    /**
     * 发送时间
     */
    private String clientTime;
    private String serverTime;

    /**
     * 目标用户id, 与targetRoomId二选一
     */
    private Integer targetMemId;
    /**
     * 目标聊天室id, 与targetMemId二选一
     */
    private Integer targetRoomId;

    private Integer type; // 0: 一对一信息; 1:聊天室信息, 2:handshake

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getClientTime() {
        return clientTime;
    }

    public void setClientTime(String clientTime) {
        this.clientTime = clientTime;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public Integer getTargetMemId() {
        return targetMemId;
    }

    public void setTargetMemId(Integer targetMemId) {
        this.targetMemId = targetMemId;
    }

    public Integer getTargetRoomId() {
        return targetRoomId;
    }

    public void setTargetRoomId(Integer targetRoomId) {
        this.targetRoomId = targetRoomId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMemName() {
        return memName;
    }


    public void setMemName(String memName) {
        this.memName = memName;
    }
}
