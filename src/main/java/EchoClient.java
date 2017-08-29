import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {

        // got it from netty-in-action
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .remoteAddress(new InetSocketAddress(host, port))
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new EchoClientHandler());
//                        }
//                    });
//            ChannelFuture f = b.connect().sync();
//            f.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully().sync();
//        }

        // got it from the Netty doc.
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            SSLContext sslContext = SSLContext.getDefault();
            final SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);

            SSLParameters sslParameters = new SSLParameters();
            List list = new ArrayList();
            list.add(new SNIHostName("getshoutout.com"));
            sslParameters.setServerNames(list);

            sslEngine.setSSLParameters(sslParameters);

            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new SslHandler(sslEngine));
                    ch.pipeline().addLast(new EchoClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(this.host, this.port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 2) {
//            System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
//            return;
//        }
        // Parse options.
        final String host = "getshoutout.com";
        final int port = Integer.parseInt("443");
        new EchoClient(host, port).start();
    }
}