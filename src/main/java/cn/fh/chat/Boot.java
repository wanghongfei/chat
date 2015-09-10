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
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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

    public static void main(String[] args) {
        logger.info("starting server");
        long startTime = System.currentTimeMillis();

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
            logger.info("server is listening at {}:{}/{}", "localhost", 8080, "chat");
            logger.info("time:{}ms", System.currentTimeMillis() - startTime);

            f.channel().closeFuture().sync();


        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            logger.info("stoping server");

            masterGroup.shutdownGracefully();
            slaveGroup.shutdownGracefully();

            logger.info("server stopped");
        }
    }
}
