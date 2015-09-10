package cn.fh.chat;

import cn.fh.chat.data.DataRepo;
import cn.fh.chat.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器启动类
 * Created by whf on 9/9/15.
 */
public class Boot {
    public static Logger logger = LoggerFactory.getLogger(Boot.class);

    public static int DEFAULT_PORT = 8080;

    public static String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        logger.info("starting server");
        long startTime = System.currentTimeMillis();

        int port = getPort(args);

        DataRepo repo = new DataRepo();

        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup slaveGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(masterGroup, slaveGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pip = ch.pipeline();

                            pip.addLast("http-codec", new HttpServerCodec());
                            pip.addLast("aggregator", new HttpObjectAggregator(65536));
                            pip.addLast("http-chunked", new ChunkedWriteHandler());
                            pip.addLast("handler", new WebSocketHandler(repo));
                        }
                    });

            ChannelFuture f = boot.bind(8080).sync();
            logger.info("server is listening at {}:{}/{}", "localhost", port, "chat");
            logger.info("time consumed:{}ms", System.currentTimeMillis() - startTime);

            f.channel().closeFuture().sync();


        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            logger.info("stopping server");

            masterGroup.shutdownGracefully();
            slaveGroup.shutdownGracefully();

            logger.info("server stopped");
        }
    }

    public static int getPort(String[] args) {
        if (args.length > 0) {

            int port;
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                logger.info("invalid port, using default {}", DEFAULT_PORT);
                return DEFAULT_PORT;
            }

            return port;
        }


        logger.info("using default port {}", DEFAULT_PORT);
        return DEFAULT_PORT;
    }
}
