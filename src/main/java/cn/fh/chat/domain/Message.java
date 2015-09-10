package cn.fh.chat.domain;

import cn.fh.chat.domain.Header;

/**
 * Created by whf on 9/10/15.
 */
public class Message {
    private Header header;

    private String content;


    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
