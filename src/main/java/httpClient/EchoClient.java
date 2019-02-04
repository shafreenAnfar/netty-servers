package httpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // got it from netty-in-action
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpRequestEncoder());
                            ch.pipeline().addLast(new HttpResponseDecoder());
                            ch.pipeline().addLast("aggegator",
                                             new HttpObjectAggregator(512 * 1024));
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/my-webapp/echo");
            httpRequest.headers().set("User-Agent", "shafreen");
            httpRequest.headers().set("Host", "localhost:8080");
//            httpRequest.headers().set("Transfer-Encoding", "chunked");
            httpRequest.headers().set("Content-Length", "shafreen".getBytes().length);
            ChannelFuture future = f.channel().write(httpRequest);
//            future.sync();

            f.channel().writeAndFlush(new DefaultLastHttpContent(Unpooled.wrappedBuffer("shafreen".getBytes())));
//            f.channel().writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer("aaaa".getBytes())));

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        final String host = "localhost";
        final int port = Integer.parseInt("9090");
        new EchoClient(host, port).start();
    }
}