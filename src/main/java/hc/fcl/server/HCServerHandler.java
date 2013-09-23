package hc.fcl.server;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

/*	
 * Used SimpleChannelInboundHandler<Object> 
 * instead of its superclass ChannelInboundHandlerAdapter 
 * to handle only messages which is instance of "Http Requests"
 */
public class HCServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
		
	
	
	private Statistics statistics;
	private TrafficCounter trafficCounter;
	
	HCServerHandler (Statistics statistics, TrafficCounter trafficCounter) {
		this.statistics = statistics;
		this.trafficCounter = trafficCounter;
	}
	@Override
	public void channelRead0 (ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
			statistics.addCurrentOpenConnections();
			statistics.addClientIP(ctx);
			handleHttpRequest(ctx, msg);
			catchTraffic(msg.getUri());
			statistics.substractCurrentOpenConnections();
			
	}
	
	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		FullHttpResponse res;
		
		
		if (!req.getDecoderResult().isSuccess()) {
			System.out.print("/badrequest ");
			res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			sendHttpResponse(ctx, req, res);
		}
		else {
			String url = req.getUri();
			if (req.getMethod() == GET && url != null) {
				if ("/".equals(url)) {
					System.out.print("/index ");
					ByteBuf INDEX = 
							Unpooled.copiedBuffer(
			                "<html><head><title>Index</title></head>" + 
			                        "<body>" + 
			                        "<img src='http://hamstercoders.com/img/logo.png' width='192' height='22' alt='HamsterCoders' />" + 
			                        "<h3>You can go to pages:</h3><br/>" +
			                        " 1) <a href='/hello'>/hello</a> <br/>  2) /redirect?url=<url> </br>  3) <a href='/status'>/status</a>" +
			                        "</body>" + 
			                        "</html>", CharsetUtil.US_ASCII);
					res = new DefaultFullHttpResponse (HTTP_1_1, OK, INDEX.duplicate());
					res.headers().set(CONTENT_TYPE, "text/html");
					res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
					sendHttpResponse(ctx, req, res);
					
				}
				else if ("/status".equals(url)) {
					System.out.print("/status ");
					res = new DefaultFullHttpResponse(HTTP_1_1, OK, new PageStatus(statistics).getHTML());
					res.headers().set(CONTENT_TYPE, "text/html");
					res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
					sendHttpResponse(ctx, req,res);
				}
				else if ("/hello".equals(url)) {
					System.out.print("/hello ");
					HelloThread(ctx, req);
					
				}
				else if (url.startsWith("/redirect")) {
					System.out.print("/redirect ");
					QueryStringDecoder dec = new QueryStringDecoder(url);
					String redirectUrl = dec.parameters().get("url").get(0);
					
					res = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY);
					redirect (ctx, redirectUrl, res);
					
					
				}
				else if ("/connections".equals(url)) {
					System.out.print("/connections ");
					res = new DefaultFullHttpResponse(HTTP_1_1, OK, new PageStatus(statistics).getConnectionsHTML());
					res.headers().set(CONTENT_TYPE, "text/html");
					res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
					sendHttpResponse(ctx, req,res);
				}
				
				else {
					System.out.print("/notfound ");
					res = new DefaultFullHttpResponse (HTTP_1_1, NOT_FOUND);
					sendHttpResponse(ctx, req, res);
				}
			}
			else {
				System.out.print("/forbidden ");
				res = new DefaultFullHttpResponse (HTTP_1_1, FORBIDDEN);
				sendHttpResponse(ctx, req, res);
			}
		}
	}

/*
 * Hello World 	is cased into scheduled threads
 *  
 */

	private void HelloThread(final ChannelHandlerContext ctx, final FullHttpRequest req) {
		ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);
    	executor.schedule(new Runnable() {
            public void run() {
		         		ByteBuf HELLO = 
            				Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
						"<html><head><title>Hello World</title></head>"
						+ "<h1>Hello World!</h1></body></html>", CharsetUtil.US_ASCII));
		
						DefaultFullHttpResponse res = new DefaultFullHttpResponse (HTTP_1_1, OK, HELLO.duplicate());
						res.headers().set(CONTENT_TYPE, "text/html");
						res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
						sendHttpResponse(ctx, req, res);		
				    }
        }, 10, TimeUnit.SECONDS);
	}
	
    	

	private void catchTraffic(String uri) {
		trafficCounter.stop();
		long receivedBytes = trafficCounter.cumulativeReadBytes();
		long sentBytes = trafficCounter.cumulativeWrittenBytes();
		long speed = trafficCounter.lastWriteThroughput();
		trafficCounter.resetCumulativeTime();
		statistics.addLog(new Connections((String) statistics.getClientIP(), uri, new Date(), receivedBytes, sentBytes, speed));
	}
	 
	
	private void redirect(ChannelHandlerContext ctx, String redirectUrl, FullHttpResponse res) {
		if (redirectUrl.length() < 7) {
			redirectUrl = "http://" + redirectUrl;
		}
		if (!redirectUrl.substring(0, 7).equals("http://")) {
            redirectUrl = "http://" + redirectUrl;
        }
		statistics.addRedirects(redirectUrl);
		
		res.headers().set(LOCATION, redirectUrl);
		ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
		
		
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
		
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
