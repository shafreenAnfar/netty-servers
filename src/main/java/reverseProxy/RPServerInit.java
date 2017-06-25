package reverseProxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class RPServerInit extends ChannelInitializer<Channel> {

    public RPServerInit() {}

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new SourceHandler(new ContentQueue()));
    }
}