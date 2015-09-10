package cn.fh.chat.domain;

import cn.fh.chat.constants.ErrorCode;
import com.sun.xml.internal.ws.server.ServerRtException;

/**
 * 服务端返回数据包
 * Created by whf on 9/10/15.
 */
public class ServerResponse {
    private int code;
    private String message;

    private Object data;

    /**
     * 默认为操作成功
     */
    public ServerResponse() {
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getMessage();
    }

    /**
     * 手动指定错误代码
     * @param ec
     */
    public ServerResponse(ErrorCode ec) {
        this.code = ec.getCode();
        this.message = ec.getMessage();
    }

    /**
     * 操作成功且带返回数据
     * @param data
     */
    public ServerResponse(Object data) {
        this();

        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
