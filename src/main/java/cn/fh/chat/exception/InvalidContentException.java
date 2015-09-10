package cn.fh.chat.exception;

/**
 * 消息内容有误
 * Created by whf on 9/10/15.
 */
public class InvalidContentException extends Exception {
    public InvalidContentException(String msg) {
        super(msg);
    }
}
