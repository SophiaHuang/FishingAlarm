package com.fusi.fishingalarm.client;

import android.content.Context;
import android.util.Log;

import com.fusi.fishingalarm.UILApplication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by user90 on 2016/6/14.
 * 接收服务器那边的数据
 */
public class ClientReceiveThread extends Thread{

    private String TAG="ClientReceiveThread";

    private Context context;
    private  Socket s;
    public Socket getS() {return s;}
    public ClientReceiveThread(Context context,Socket s){
        this.context=context;
        this.s=s;
    }

    @Override
    public void run() {
        super.run();
        while(true){
            DataInputStream in = null;
            try {
                in = new DataInputStream(s.getInputStream());
                StringBuffer receive = new StringBuffer();
                byte[] receiveBuffer = new byte[in.available()];//att
                Log.i(TAG, "sendOrder: in.available():" + in.available());
                while (in.read(receiveBuffer) > 0) {
                    for (int i = 0; i < receiveBuffer.length; i++) {
                        String hex = Integer.toHexString(receiveBuffer[i] & 0xFF);  //这个可以把16进制接收上来的数据，以16进制形式在String中表现
                        if (hex.length() == 1) {
                            hex = '0' + hex;
                        } else {
                        }
                        Log.i(TAG, "sendOrder: hex:" + hex);
                        receive.append(hex);
                        Log.i(TAG, "sendOrder: receiveall:" + receive.toString());
                        Log.i(TAG, "sendOrder: receiveBuffer command:" + receiveBuffer[1]);
                        Log.i(TAG, "sendOrder:receiveBuffer data :" + receiveBuffer[4]);
                        if (receiveBuffer[1] == 0x01) {//查询主报警器状态,comman=0x01
                            if (receiveBuffer[4] == 0x01) {
                                //登录成功
                                if (!UILApplication.ISLOGIN) {
//                                    mHandler.sendEmptyMessage(SUCESS);
                                }
                            } else {
                                UILApplication.ISLOGIN = false;
//                                errerCount++;
//                                resetSocket();
                            }
                        } else if (receiveBuffer[1] == 0x02) {//查询主报警器+从报警器个数
//                                报警器数量
                            UILApplication.ALARM_COUNT = receiveBuffer[4];

                        } else if (receiveBuffer[1] == 0x03) {
//                                报警器状态查询
                            //do sth
                        } else if (receiveBuffer[1] == 0x0A) {
//                                夜间模式查询
                            if (receiveBuffer[4] == 0x01) {
                                //夜间模式
                                UILApplication.hasDayMode = true;
                            } else {
                                UILApplication.hasDayMode = false;
                            }
                        }
                    }
//                i_hex=Integer.valueOf(Receive_date[4],16);
                }


              /*  m=(SocketMessage) in.readObject();
                if(m.getType().equals(SocketMessageType.COM_MES)){//如果是聊天内容
                    //把从服务器获得的消息通过广播发送
                    Intent intent = new Intent("org.yhn.yq.mes");
                    String[] message=new String[]{
                            m.getSender()+"",
                            m.getSenderNick(),
                            m.getSenderAvatar()+"",
                            m.getContent(),
                            m.getSendTime()};
                    intent.putExtra("message", message);
                    context.sendBroadcast(intent);
                }else if(m.getType().equals(SocketMessageType.RET_ONLINE_FRIENDS)){//如果是好友列表
                }*/
            } catch (Exception e) {
                //e.printStackTrace();
                try {
                    if(s!=null){
                        s.close();
                    }
                } catch (IOException e1) {
                    //e1.printStackTrace();
                }
            }
        }
    }
}
