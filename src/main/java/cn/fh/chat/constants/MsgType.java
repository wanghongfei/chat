package cn.fh.chat.constants;

import javax.net.ssl.SSLEngineResult;

/**
 * Created by whf on 9/10/15.
 */
public enum MsgType {
    PRIVATE(0),
    ROOM(1),
    HANDSHAKE(2),
    QUERY_ONLINE(3),
    JOIN_ROOM(4),
    EXIT_ROOM(5);


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

            case 3:
                return QUERY_ONLINE;

            case 4:
                return JOIN_ROOM;

            case 5:
                return EXIT_ROOM;

            default:
                return null;
        }
    }
}
