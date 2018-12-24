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
        byte A3 = bytes[2];
        if(A3 == 80){
            System.out.println(Arrays.toString(bytes));

            String name = getRandomStr(); // TODO 每次启动会变化
            String password = "xxxx";
            byte[] msg = new byte[name.length()+ password.length()+1];
            System.arraycopy(name.getBytes(),0, msg, 0, name.length());
            msg[name.length()] = 0x20;
            System.arraycopy(password.getBytes(), 0 , bytes, name.length()+1,password.length());
            channelHandlerContext.channel().writeAndFlush(getSendContent(81, msg));
            System.out.println(String.format("gateway_%s started!", name));
        }
        if (GatewayClient.count < 500 ){
            System.out.println(String.format("########  number %d gateway  ########", GatewayClient.count));
            new GatewayClient("smart.gantch.cn", 8090).start();
        }
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
}
