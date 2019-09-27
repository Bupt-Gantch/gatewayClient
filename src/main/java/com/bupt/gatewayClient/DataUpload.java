package com.bupt.gatewayClient;


import java.util.Random;

public class DataUpload {

    public static byte[] getHeartBeatBytes(){
        byte[] bytes = new byte[]{0x04, 0x00, 0x1E, 0x00};
        return getSendContent(30, bytes);
    }

    public static byte[] getVersionRes(String shortAddress, byte endpoint){
//        70 14 b6 6a 01 00 00 01 0a 40 42 0a 55 55 06 e2 07 01 04 00 00 f4;
        byte[] bytes= new byte[22];
        int index  = 0;

        bytes[index++] = 0x70;
        bytes[index++] = 0x14;
        System.arraycopy(toBytes(shortAddress), 0, bytes, index, toBytes(shortAddress).length);
        index=index+toBytes(shortAddress).length;
        bytes[index++] = endpoint;
        bytes[index++] = 0x0a;
        bytes[index++] = 0x40;
        bytes[index++] = 0x42;
        bytes[index++] = 0x0a;
        bytes[index++] = 0x55;
        bytes[index++] = 0x55;
        bytes[index++] = 0x06;
        bytes[index++] = (byte)0xe2;
        bytes[index++] = 0x07;
        bytes[index++] = 0x01;
        bytes[index++] = 0x04;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = (byte)0xf4;

        return getSendContent(11, bytes);

    }

    public static byte[] getInfraredUpload(String shortAddress, byte endpoint) {
        // 01 2b 0c c4 01 04 01 63 01 00 00 03 41 43 a9 01 00 8d 15 00 10 46 4e 42 35 36 2d 5a 49 52 30 34 46 42 31 2e 32 01 00 00 00 00 00 00 00
        // 01 2b b6 6a 01 04 01 63 01 00 00 03 dd a8 92 02 00 8d 15 00 10 46 4e 42 35 36 2d 5a 49 52 30 34 46 42 31 2e 32 01 00 00 00 00 00 00 00
//        byte[] bytes = new byte[44]{0x70, 0x1A, (byte) 0x95, 0x65, 0x01, 0x00, 0x00, 0x01, 0x0A, 0x40, 0x42, 0x10, 0x55, 0x55, 0x0C, (byte) 0xE2, 0x07, 0x01, 0x04, 0x00, 0x00, (byte) 0x83, 0x00, 0x01, 0x5B, 0x02, 0x00, (byte) 0xDB};
        byte[] bytes = new byte[44];
        int index = 0;

        bytes[index++] = 0x01;
        bytes[index++] = 0x2b;
        System.arraycopy(toBytes(shortAddress), 0, bytes, index, toBytes(shortAddress).length);
        index=index+toBytes(shortAddress).length;
        bytes[index++] = endpoint;
        bytes[index++] = 0x04; // profile ID
        bytes[index++] = 0x01;
        bytes[index++] = 0x63; // device ID
        bytes[index++] = 0x01;
        bytes[index++] = 0x00; // state
        bytes[index++] = 0x00; // 设备名长度
        bytes[index++] = 0x03; // 在线状态
        bytes[index++] = 0x00; // IEEE
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x15;
        bytes[index++] = 0x00;
        bytes[index++] = 0x10; // sn 号长度 46 4e 42 35 36 2d 5a 49 52 30 34 46 42 31 2e 32
        String snid = "464e4235362d5a495230344642312e32";
        System.arraycopy(toBytes(snid), 0, bytes, index, toBytes(snid).length);
        index=index+toBytes(snid).length;
        bytes[index++] = 0x01;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;

        return getSendContent(11, bytes);
    }

    public static byte[] getLearnRes(String shortAddress, byte endpoint) {

        // 70 1a 0c c4 01 00 00 01 0a 40 42 10 55 55 0c e2 07 01 04 00 00 83 00 01 5b 02 00 db
        byte[] bytes = new byte[26];
        int index = 0;

        bytes[index ++] = 0x70;
        bytes[index ++] = (byte)0x1a;
        System.arraycopy(toBytes(shortAddress), 0, bytes, index, toBytes(shortAddress).length);
        index=index+toBytes(shortAddress).length;
        bytes[index++] = endpoint;
        bytes[index ++] = 0x00;
        bytes[index ++] = 0x00;
        bytes[index ++] = 0x01;
        bytes[index ++] = 0x0a;
        bytes[index ++] = 0x40;
        bytes[index ++] = 0x42;
        bytes[index ++] = 0x10;
        bytes[index ++] = 0x55;
        bytes[index ++] = 0x55;
        bytes[index ++] = 0x0c;
        bytes[index++] = (byte)0xe2;
        bytes[index++] = 0x07;
        bytes[index++] = 0x01;
        bytes[index++] = 0x04;
        bytes[index++] = 0x00;
        bytes[index++] = 0x00;
        bytes[index++] = (byte)0x83;
        bytes[index++] = 0x00;
        bytes[index++] = 0x01;
        bytes[index++] = 0x5b;
        bytes[index++] = 0x02;
        bytes[index++] = 0x00;
        byte[] countValue = new byte[13];
        System.arraycopy(bytes, index-9, countValue, 0, 9);
        bytes[index] = count_bytes(countValue);

        return getSendContent(11, bytes);

    }

    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
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



    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
//			sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }


    public static byte count_bytes(byte[] bytes) {
        if (null == bytes || bytes.length <= 0) {
            return 0x00;
        }
        byte result = 0x00;
        for (byte B : bytes) {
            result += B;
        }
        return (byte) result;
    }

}
