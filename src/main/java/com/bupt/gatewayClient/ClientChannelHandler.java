package com.bupt.gatewayClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;

public class ClientChannelHandler extends SimpleChannelInboundHandler<byte[]> {

    class HeartBeatThread implements Callable<Boolean> {

        private String name;
        private ChannelHandlerContext ctx;

        HeartBeatThread(ChannelHandlerContext ctx){
            this.ctx = ctx;
            this.name = ctx.name();
        }

        @Override
        public Boolean call() {
            try {
                while(!ctx.isRemoved()) {
                    byte[] data = DataUpload.getHeartBeatBytes();
                    ctx.channel().writeAndFlush(DataUpload.getSendContent(30, data));
                    System.out.println("send heartbeat message");
                    Thread.sleep(10000L);
                }
            } catch (InterruptedException e){
                System.err.println("Thread " +  name + " interrupted.");
                return false;
            }
            return true;
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        System.out.println("网关已连接上服务器");
        //startHeartBeatThread(ctx);
    }

    private void startHeartBeatThread(ChannelHandlerContext ctx) {

        HeartBeatThread thread = new HeartBeatThread(ctx);
        thread.call();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) throws Exception {
        
        System.out.println("收到服务器消息:" + GatewayClient.bytesToHexString(bytes));
        byte[] body = new byte[bytes.length-6];
        System.arraycopy(bytes, 6, body, 0, bytes.length-6);
        
        byte B3 = bytes[2];
        if(B3 == 80){ // 0x50 登录认证
            String name = "999999";   // 网关名
            String password = "xxxx";  // 网关密码
            byte[] msg = new byte[name.length()+ password.length()+1];
            System.arraycopy(name.getBytes(),0, msg, 0, name.length());
            msg[name.length()] = 0x20;
            System.arraycopy(password.getBytes(), 0 , bytes, name.length()+1,password.length());
            channelHandlerContext.channel().writeAndFlush(DataUpload.getSendContent(81, msg));
            System.out.println(String.format("网关 %s 向服务器发送登录认证消息", name));

        } else if (B3 == 31) {
//            byte[] data = DataUpload.getHeartBeatBytes();
//            channelHandlerContext.channel().writeAndFlush(DataUpload.getSendContent(30, data));
            System.out.println("heart beat message");

        } else if (B3 == 12) { //  0x0C 服务器发送指令

            System.out.println("网关收到服务器下发的指令");

            if (body[7] == 0x81) { // 获取所有设备指令 08 00 FF FF FF FF FE 81
                byte[] resBytes = DataUpload.getInfraredUpload("0909", (byte)1);
                channelHandlerContext.channel().writeAndFlush(resBytes);
            }

            if (body[7] == 0xa7) {
                String shortAddress = DataUpload.byte2HexStr(Arrays.copyOfRange(bytes, 9, 10));
                byte endpoint = body[11];
                switch (bytes[8]) {
                    case 0x08: // 获取版本 15 00 ff ff ff ff fe a7 0c 26 87 01 03 06 00 55 55 02 80 00 82
                        byte[] resBytes = DataUpload.getVersionRes(shortAddress, endpoint);
                        channelHandlerContext.channel().writeAndFlush(resBytes);
                        break;

                    case 0x15:  // 1e 00 ff ff ff ff fe a7 15 26 87 01 03 0f 00 55 55 0b e2 07 01 04 00 00 83 00 05 2c 00 ad
                        resBytes = DataUpload.getLearnRes(shortAddress, endpoint);
                        channelHandlerContext.channel().writeAndFlush(resBytes);
                        break;

                    default:
                        System.out.println("unknown message type");
                }

            }
        } else {
            System.out.println("忽略该消息");
        }

        // 模拟网关客户端的数量
//        if (GatewayClient.count < 500 ){
//            System.out.println(String.format("########  number %d gateway  ########", GatewayClient.count));
//            new GatewayClient("smart.gantch.cn", 8090).start();
//        }


    }

    public String getRandomStr(){
        String rand = "99";
        int n = new Random().nextInt(9999);
        if (n < 1000)
            n += 1000;
        rand += n;
        return rand;
    }

}
