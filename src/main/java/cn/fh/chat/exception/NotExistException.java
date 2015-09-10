package cn.fh.chat.exception;

/**
 * Created by whf on 9/10/15.
 */
public class NotExistException extends Exception {
    public NotExistException(String msg) {
        super(msg);
    }

    public NotExistException() {}
}
