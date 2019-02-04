package httpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port= port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new HttpRequestDecoder());
//                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpServerCodec());
//                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new MockClientHandler());
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, true);

//                    .childOption(ChannelOption., false);
            ChannelFuture f = b.bind("localhost", 9000).sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
    public static void main(String[] args) throws Exception {
//        if (args.length!=1) {
//            System.err.println("Usage: " + EchoServer.class.getSimpleName() +" <port>");
//        }
        int port = Integer.parseInt("8080");
        new EchoServer(port).start();
    }
}

