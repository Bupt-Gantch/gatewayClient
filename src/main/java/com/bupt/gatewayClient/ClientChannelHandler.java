package com.bupt.gatewayClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.Random;

public class ClientChannelHandler extends SimpleChannelInboundHandler<byte[]> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) throws Exception {
        System.out.println("channel received:" + GatewayClient.bytesToHexString(bytes));
        byte A3 = bytes[2];
        if(A3 == 80){
//            String name = getRandomStr();
            String name = "5153054"; // 智慧管廊
            String password = "xxxx";
            byte[] msg = new byte[name.length()+ password.length()+1];
            System.arraycopy(name.getBytes(),0, msg, 0, name.length());
            msg[name.length()] = 0x20;
            System.arraycopy(password.getBytes(), 0 , bytes, name.length()+1,password.length());
            channelHandlerContext.channel().writeAndFlush(getSendContent(81, msg));
            System.out.println(String.format("gateway_%s started!", name));

        } else if (A3 == 31) {
            byte[] data = getHeartBeatBytes();
            channelHandlerContext.channel().writeAndFlush(getSendContent(30, data));
            System.out.println("heart beat message");

        } else if (bytes[7] == 0x81){  // 服务端消息
            byte[] data = getInfraredResponse();
            channelHandlerContext.channel().writeAndFlush(getSendContent(11, data));
            System.out.println("infrared learn replyd");

        }

        // 模拟网关客户端的数量
//        if (GatewayClient.count < 500 ){
//            System.out.println(String.format("########  number %d gateway  ########", GatewayClient.count));
//            new GatewayClient("smart.gantch.cn", 8090).start();
//        }
    }

    public static byte[] getSendContent(int type, byte[] message) {
        int messageLength = message.length;
        int contentLength = message.length + 6;

        byte message_low = (byte) (messageLength & 0x00ff); // �����Ϣ��λ�ֽ�
        byte message_high = (byte) ((messageLength >> 8) & 0xff);// �����Ϣ��λ�ֽ�

        byte type_low = (byte) (type & 0x00ff); // ������͵�λ�ֽ�
        byte type_high = (byte) ((type >> 8) & 0xff);// ������͸�λ�ֽ�

        byte content_low = (byte) (contentLength & 0x00ff); // ������ݳ��ȵ�λ�ֽ�
        byte content_high = (byte) ((contentLength >> 8) & 0xff);// ������ݳ��ȸ�λ�ֽ�

        byte[] headMessage = new byte[6];
        headMessage[0] = content_low;
        headMessage[1] = content_high;
        headMessage[2] = type_low;
        headMessage[3] = type_high;
        headMessage[4] = message_low;
        headMessage[5] = message_high;

        byte[] sendContent = new byte[contentLength];
        System.arraycopy(headMessage, 0, sendContent, 0, 6);
        System.arraycopy(message, 0, sendContent, 6, messageLength);
        return sendContent;
    }

    public String getRandomStr(){
        String rand = "99";
        int n = new Random().nextInt(9999);
        if (n < 1000)
            n += 1000;
        rand += n;
        return rand;
    }

    public byte[] getHeartBeatBytes(){
        byte[] bytes = new byte[]{0x04, 0x00, 0x1E, 0x00};
        return bytes;
     }


    public byte[] getInfraredResponse() {
        byte[] bytes = new byte[]{0x70, 0x1A, (byte) 0x94, 0x56, 0x01, 0x00, 0x00, 0x01, 0x0A, 0x40, 0x42, 0x10, 0x55, 0x55, 0x0C, (byte) 0xE2, 0x07, 0x01, 0x04, 0x00, 0x00, (byte) 0x83, 0x00, 0x01, 0x5B, 0x02, 0x00, (byte) 0xDB};
        return bytes;
    }
}
