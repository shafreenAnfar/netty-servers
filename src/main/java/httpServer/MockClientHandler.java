package httpServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.ByteBuffer;

public class MockClientHandler extends ChannelInboundHandlerAdapter {

    static int count;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            System.out.println(" Server received: " + msg);
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.headers().set("Content-Type", "application/json");
//            httpResponse.headers().set("Content-Length", "{\"hello\": \"world\"}".getBytes().length);
            httpResponse.headers().set("Transfer-Encoding", "chunked");
            ctx.channel().writeAndFlush(httpResponse);
        } else {

//            File file = new File("/home/shaf/Documents/idea-projects/netty-in-action/response.txt");
//            FileInputStream in = null;
//            try {
//                in = new FileInputStream(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            FileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length());

            HttpContent httpContent = new DefaultLastHttpContent(Unpooled.wrappedBuffer(ByteBuffer.wrap("{\"hello\": \"world\"}".getBytes())));
            ctx.channel().writeAndFlush(httpContent);
        }
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        if (msg instanceof HttpRequest) {
//            HttpRequest httpRequest = (HttpRequest) msg;
//            System.out.println(httpRequest.toString());
//        } else {
//            HttpContent content = (HttpContent) msg;
//            ByteBuffer buffer = content.content().nioBuffer();
////            buffer.flip();
//            while (buffer.hasRemaining()) {
//                System.out.print((char) buffer.get());
//            }
//            content.release();
//            System.out.println(++count);
//            ctx.channel().read();
////            ctx.pipeline().fireChannelRead()
//        }
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("channelReadComplete");
//        ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }
}