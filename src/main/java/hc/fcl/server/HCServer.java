package hc.fcl.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Logger;



public class HCServer {
	private int port;
	private static final Logger logger = Logger.getLogger(HCServer.class.getName());
	Statistics sts = new Statistics();
	
	public HCServer (int port) {
		this.port=port;
	}
	
	public void run () throws Exception {
		EventLoopGroup BossGroup = new NioEventLoopGroup();
		EventLoopGroup WorkerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(BossGroup, WorkerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new HCServerInitializer(sts));
						
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
		new HCServer(port).run();
	}

}
