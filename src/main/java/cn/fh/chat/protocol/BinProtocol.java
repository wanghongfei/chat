package cn.fh.chat.protocol;

import java.util.Date;

/**
 * Created by whf on 1/29/16.
 */
public class BinProtocol {
    private BinHeader header;

    private String body;

    /**
     * 返回数据包的总长度
     * @return
     */
    public int getLength() {
        return body.getBytes().length + BinHeader.HEADER_LENGTH;
    }

    public BinHeader getHeader() {
        return header;
    }

    public void setHeader(BinHeader header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BinProtocol{");
        sb.append("header=").append(header);
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
