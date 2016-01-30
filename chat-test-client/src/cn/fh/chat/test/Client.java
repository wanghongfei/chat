package cn.fh.chat.test;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;
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
        String str = "qwertyu你";
        //byte[] buf = str.getBytes();
        //System.out.println(new String(buf));
        out.write(str.getBytes());
        //dataOut.writeBytes("qwertyu111");
        //dataOut
        dataOut.writeInt(40);
        dataOut.writeLong(now.getTime());

        dataOut.writeBytes("00");
        dataOut.writeInt(10);
        dataOut.writeInt(20);
        dataOut.writeInt(30);
        dataOut.writeBytes("pear");

        dataOut.flush();

        // 得到返回数据
        System.out.println("waiting");
        InputStream in = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len = in.read(buf);
        System.out.println("len = " + len);
        // 打印
        for (int ix = 0 ; ix < len ; ++ix) {
            System.out.println( byte2Hex(Byte.toUnsignedInt(buf[ix])));
            System.out.println( Integer.toHexString(Byte.toUnsignedInt(buf[ix])) );
        }

        socket.close();

    }

    public static String byte2Hex(int b) {
        StringBuffer sb = new StringBuffer("0x");

        int heightDigit = b >> 4;
        char hex = hex(heightDigit);
        sb.append(hex);

        int lowDigit = (b << 28) >>> 28;
        //System.out.println("left 28 = " + Integer.toBinaryString( b << 28) );
        //System.out.println("right 28 = " + Integer.toBinaryString(b << 28 >>> 28));
        //System.out.println(Integer.toBinaryString(lowDigit));
        hex = hex(lowDigit);
        sb.append(hex);

        return sb.toString();
    }

    public static char hex(int num) {
        if (num < 10) {
            return (char) ('0' + num);
        } else {
            return (char) ('A' + (num - 10));
        }

    }

}

