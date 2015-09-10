package cn.fh.chat.constants;

import javax.net.ssl.SSLEngineResult;

/**
 * Created by whf on 9/10/15.
 */
public enum MsgType {
    PRIVATE(0),
    ROOM(1),
    HANDSHAKE(2);


    private int value;

    private MsgType(int value) {
        this.value = value;
    }

    public static MsgType toType(int type) {
        switch (type) {
            case 0:
                return PRIVATE;
            case 1:
                return ROOM;
            case 2:
                return HANDSHAKE;

            default:
                return null;
        }
    }
}
