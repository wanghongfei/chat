package cn.fh.chat.constants;

/**
 * 错误代码
 * Created by whf on 9/10/15.
 */
public enum ErrorCode {
    SUCCESS(0, "success"),
    FAILED(-1, "failed"),
    NOT_EXIST(1, "not exist"),
    BAD_CONTENT(2, "bad content");

    private int code;
    private String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
