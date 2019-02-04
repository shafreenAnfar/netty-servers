package httpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.ssl.SslHandler;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

public class EchoSslClient {

    private final String host;
    private final int port;

    public EchoSslClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
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
                    ch.pipeline().addLast(new SslHandler(sslEngine));
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpRequestDecoder());
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
        final String host = "localhost";
        final int port = Integer.parseInt("8080");
        new EchoSslClient(host, port).start();
    }
}