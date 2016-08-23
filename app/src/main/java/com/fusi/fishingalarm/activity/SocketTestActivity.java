package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.broadcast.NetworkChangeReceiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by user90 on 2016/6/3.
 */
public class SocketTestActivity extends Activity {

    private String TAG = "SocketTestActivity";
    EditText host, port, content;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public static final int SOCKET_CONNECT = 1;
    private static final int SOCKET_MESSAGE = 2;
    private static final int SOCKET_FAIL = 3;

    TextView receive;
    Thread thread;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SOCKET_MESSAGE:
                    Log.i(TAG, "run: inString:reveive:" + msg.obj);
                    receive.setText(msg.obj.toString());
                case SOCKET_CONNECT:
                    Toast.makeText(getBaseContext(), "目前处于连接状态", Toast.LENGTH_SHORT).show();
                    break;
                case SOCKET_FAIL:
                    Toast.makeText(getBaseContext(), "断开", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);
        host = (EditText) findViewById(R.id.host);
        port = (EditText) findViewById(R.id.port);
        content = (EditText) findViewById(R.id.content);
        receive = (TextView) findViewById(R.id.receive);
    }

    public void contact(View view) {
        Log.i(TAG, "contact: ");
        //        客户端启动线程创建网络连接/读取来自服务器端数据
        //        连接服务器
    }

    public void send(View view) {
        try {
            String tempContent = content.getText().toString();
            Log.i(TAG, "send: tempContent:" + tempContent);
//            if (!TextUtils.isEmpty(tempContent)) {
            if (socket.isConnected()) {
                Log.i(TAG, "send: out:" + out);
//                    byte[] buf = {(byte) 0x88, (byte) 0x18, (byte) 0xff, (byte) 0xf3, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}; //发送的数据要用BYTE的数组下发
                byte[] buf = new byte[5];
                buf[0] = (byte) 0x05;
                buf[1] = (byte) 0x01;
                buf[2] = (byte) 0x01;
                buf[3] = (byte) 0x01;
                out.write(buf);


            } else {
                Log.i(TAG, " no send: out:" + out);
            }


//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验和
     *
     * @param msg 需要计算校验和的byte数组
     * @return 计算出的校验和数组
     */
    public byte[] SumCheck(byte[] msg) {
        byte mSum = 0x00;
//        byte[] mByte = new byte[length];

//        /** 逐Byte添加位数和 */
//        for (byte byteMsg : msg) {
//            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
//            mSum += mNum;
//        } /** end of for (byte byteMsg : msg) */
//
//        /** 位数和转化为Byte数组 */
//        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
//            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
//        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */
//        String srt2 = null;
//        try {
//            srt2 = new String(mByte, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Log.i(TAG, "SumCheck: mByte:" + srt2);
//        因为每一次都要checksum
        msg[2]++;
        for (int i = 0; i < msg.length - 1; i++) {
            mSum = (byte) (mSum + msg[i]);
        }
        msg[msg.length - 1] = (byte) (mSum ^ 0xff);
        return msg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initSocket() {
        try {

            socket = new Socket(UILApplication.HOST, UILApplication.PORT);
            socket.setSoTimeout(UILApplication.ALARM_QUERY_TIME);
            socket.setKeepAlive(true);
            in = new DataInputStream(socket.getInputStream()); // 获得DataInputStream对象
            out = new DataOutputStream(socket.getOutputStream());
//            Log.i(TAG, "initSocket: reveive:" + in.readUTF());
//            out.writeUTF("上线了");
           /* if (socket.isConnected()) {
                Log.i(TAG, "run: isConnected hehe");
                out.writeUTF("05 01 00 f0 08");
            } else {
                Log.i(TAG, "run: no isConnected");
            }*/
//            while (true) {
//                socket.sendUrgentData(0xFF);
//                mHandler.sendEmptyMessageDelayed(SOCKET_CONNECT, 3000);
//            }

        } catch (SocketException e) {
            Log.i(TAG, "initSocket: SocketException");
            mHandler.sendEmptyMessageDelayed(SOCKET_FAIL, 3000);
        } catch (SocketTimeoutException e) {
            Log.i(TAG, "initSocket: SocketTimeoutException");
            mHandler.sendEmptyMessageDelayed(SOCKET_FAIL, 3000);
        } catch (Exception e) {
            Log.i(TAG, "initSocket: IOException");
            mHandler.sendEmptyMessageDelayed(SOCKET_FAIL, 100);
        }
//        thread = new Thread(SocketTestActivity.this);
////		开启线程，监听服务器端是否有消息
//        thread.start();

    }

/*    @Override
    public void run() {
//循环执行执行，作用是一直接听服务器端是否有消息
        while (true) {
            String inString=null;
            try {
                Log.i(TAG, "run: in.readUTF():"+in.readUTF());
                inString = in.readUTF();
//                发送一个消息，要求刷新界面
                Message msg = new Message();
                msg.what = 1;
                msg.obj = inString;
                mHandler.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }*/
}
