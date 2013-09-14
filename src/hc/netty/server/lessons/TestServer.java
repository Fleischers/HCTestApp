package hc.netty.server.lessons;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.logging.Logger;



public class TestServer {
	private int port;
	private static final Logger logger = Logger.getLogger(TestServer.class.getName());
	
	public TestServer (int port) {
		this.port=port;
	}
	
	public void run () throws Exception {
		EventLoopGroup BossGroup = new NioEventLoopGroup();
		EventLoopGroup WorkerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(BossGroup, WorkerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new HttpServerCodec());
					pipeline.addLast(new HttpObjectAggregator(65536));
					pipeline.addLast(new TestServerHandler());
				}
			});
						
			ChannelFuture f = b.bind(port).sync();
			logger.info("Server is listening at http://localhost:"+port+"/");
			f.channel().closeFuture().sync();
			
		}
		finally {
			WorkerGroup.shutdownGracefully();
			BossGroup.shutdownGracefully();
		}
		
	}
	
	public static void main (String[] args) throws Exception {
		int port;
		if (args.length>0)
		{
			port = Integer.parseInt(args[0]);
		}
		else port = 8080;
		new TestServer(port).run();
	}

}
