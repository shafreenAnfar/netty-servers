package reverseProxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class RPServer {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup parent = new NioEventLoopGroup(4);
    private final EventLoopGroup childe = new NioEventLoopGroup(8);

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parent, childe)
                .channel(NioServerSocketChannel.class)
                .childHandler(new RPServerInit());
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();

        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        parent.shutdownGracefully();
    }

    public static void main(String[] args) {

        int port = Integer.parseInt("9090");
        final RPServer endpoint = new RPServer();

        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });

        System.out.println("Server started on 9090 ...");
        future.channel().closeFuture().syncUninterruptibly();
    }
}


