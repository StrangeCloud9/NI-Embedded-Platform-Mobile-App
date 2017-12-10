package com.example.ni4.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.ni4.activity.SocketTransceiver;
import com.example.ni4.activity.TcpClient;

/**
 * Created by 蟹老板 on 2017/11/13.
 */

public class SocketService extends Service {

    private String IP;
    private int port;

    private Handler handler = new Handler(Looper.getMainLooper());

    private TcpClient client = new TcpClient() {

        @Override
        public void onConnect(SocketTransceiver transceiver) {
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {

        }

        @Override
        public void onConnectFailed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SocketService.this, "连接失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onReceive(SocketTransceiver transceiver, final String s, final Integer flag) {
            Log.d("TAG", "onReceive");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("hello hello","12345678945612313464546");
                    Intent intent = new Intent("Message");
                    intent.putExtra("ServerData",s);
                    intent.putExtra("ServerFlag",flag);
                    sendBroadcast(intent);
                }
            });
        }
    };

    public SocketService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IP = intent.getStringExtra("IP");
        port = Integer.parseInt(intent.getStringExtra("port"));
        Log.d("IP111",IP);
        Log.d("Port111",String.valueOf(port));
        connect();
        return  super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        client.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 设置IP和端口地址,连接或断开
     */
    private void connect() {
        if (client.isConnected()) {
            // 断开连接
            client.disconnect();
        } else {
            try {
                client.connect(IP, port);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
