package hc.fcl.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class HCServerInitializer extends ChannelInitializer<SocketChannel> {

	private Statistics sts;
    private TrafficCounter trafficCounter;
    
    public HCServerInitializer(Statistics statistics) {
        this.sts = statistics;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(ch.eventLoop());
        trafficCounter = globalTrafficShapingHandler.trafficCounter();
        trafficCounter.start();
        pipeline.addLast(globalTrafficShapingHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HCServerHandler(sts, trafficCounter));
    }
}
