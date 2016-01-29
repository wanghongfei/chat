package cn.fh.chat.protocol;

/**
 * Created by whf on 1/29/16.
 */
public enum MessageType {
    /**
     * 握手数据包
     */
    HANDSHAKE("00"),
    /**
     * 结束会话数据包
     */
    CLOSE("01"),
    ONE_TO_ONE("02"),
    ONE_TO_MANY("03");

    private String code;

    private MessageType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public static MessageType fromCode(String code) {
        if (null == code) {
            return null;
        }

        switch (code) {
            case "00":
                return HANDSHAKE;

            case "01":
                return CLOSE;

            case "02":
                return ONE_TO_ONE;

            case "03":
                return ONE_TO_MANY;

            default:
                return null;
        }
    }
}
