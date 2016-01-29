package cn.fh.chat.protocol;

import java.util.Date;

/**
 * Created by whf on 1/29/16.
 */
public class BinHeader {
    private String sid;

    private Integer length;

    private Date sentTime;

    private MessageType type;

    private Integer targetUserId;

    private Integer targetRoomId;

    private Integer memId;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Integer getTargetRoomId() {
        return targetRoomId;
    }

    public void setTargetRoomId(Integer targetRoomId) {
        this.targetRoomId = targetRoomId;
    }

    public Integer getMemId() {
        return memId;
    }

    public void setMemId(Integer memId) {
        this.memId = memId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BinHeader{");
        sb.append("sid='").append(sid).append('\'');
        sb.append(", length=").append(length);
        sb.append(", sentTime=").append(sentTime);
        sb.append(", type=").append(type);
        sb.append(", targetUserId=").append(targetUserId);
        sb.append(", targetRoomId=").append(targetRoomId);
        sb.append(", memId=").append(memId);
        sb.append('}');
        return sb.toString();
    }
}
