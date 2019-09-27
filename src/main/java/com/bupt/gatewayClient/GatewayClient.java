package com.bupt.gatewayClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

public class GatewayClient {
    private final String host;
    private final int port;
    public static int count = 0;

    public GatewayClient(String host, int port){
        this.host = host;
        this.port = port;
        this.count ++;
    }


    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception{
                            ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder());
                            ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                            ch.pipeline().addLast(new OutBoundHandler());
                            ch.pipeline().addLast(new IdleStateHandler(0,0,300), new ClientChannelHandler());
                        }
                    });
            // 有数据立即发送
            b.option(ChannelOption.TCP_NODELAY, true);
            // 保持连接
            b.option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }

    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public static void main(String args[]) throws Exception{
            new GatewayClient("localhost", 8090).start();
    }
}
