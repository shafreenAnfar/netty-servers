package reverseProxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;

/**
 * some code by shaf on 6/25/17.
 */
public class TargetHandler extends ChannelInboundHandlerAdapter {

    ContentQueue contentQueue;
    Channel sourceChannel;

    public TargetHandler(ContentQueue contentQueue, Channel channel) {
        this.contentQueue = contentQueue;
        this.sourceChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        sourceChannel.writeAndFlush(msg);
//        ReferenceCountUtil.release(msg);
    }
}
