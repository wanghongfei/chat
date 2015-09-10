package cn.fh.chat.domain;

/**
 * Created by whf on 9/10/15.
 */
public class Header {
    private String token;
    private String memId;
    private String memName;

    private String clientTime;
    private String serverTime;

    private Integer targetMemId;
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
