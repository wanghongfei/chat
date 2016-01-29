package cn.fh.chat.test;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Created by whf on 1/29/16.
 */
public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 8080);
        OutputStream out = socket.getOutputStream();


        Date now = new Date();
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeBytes("qwertyuiop");
        dataOut.writeInt(40);
        dataOut.writeLong(now.getTime());

        dataOut.writeBytes("00");
        dataOut.writeInt(10);
        dataOut.writeInt(20);
        dataOut.writeInt(30);
        dataOut.writeBytes("pear");

        socket.close();
    }
}

