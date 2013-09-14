package hc.netty.server.lessons;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

public class TestServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = Logger.getLogger(TestServerHandler.class.getName());
	
	private static final ByteBuf CONTENT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello World!", CharsetUtil.US_ASCII));
	
	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		}		
	}
	
	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		FullHttpResponse res;
		
		
		if (!req.getDecoderResult().isSuccess()) {
			res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			sendHttpResponse(ctx, req, res);
		}
		else {
			String url = req.getUri();
			if (req.getMethod() == GET && url != null) {
				if ("/hello".equals(url)) {
					res = new DefaultFullHttpResponse (HTTP_1_1, OK, CONTENT.duplicate());
					res.headers().set(CONTENT_TYPE, "text/plain");
					res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
					Thread.sleep(10000);
					sendHttpResponse(ctx, req, res);
				}
			}
		}


	}

	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		if (res.getStatus().code() !=200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			setContentLength(res, res.content().readableBytes());
		}
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.getStatus().code() !=200)
		{
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelReadComplete (ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		//ctx.close();
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
