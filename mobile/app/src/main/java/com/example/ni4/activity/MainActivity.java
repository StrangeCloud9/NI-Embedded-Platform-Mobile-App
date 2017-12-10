package com.example.ni4.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ni4.R;
import com.example.ni4.service.SocketService;
import com.example.ni4.utils.MyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnClickListener {

    private Button saoyisao;
    private Button stopConnect;
    private ListView lv_data;

    private List<Map<String, Object>> list;
    private MyAdapter myAdapter;

    private MyBroadcastReceiver myBroadcastReceiver;
    private Handler handler = new Handler(Looper.getMainLooper());

    private TcpClient client = new TcpClient() {

        @Override
        public void onConnect(SocketTransceiver transceiver) {
            refreshUI(true);
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {
            refreshUI(false);
        }

        @Override
        public void onConnectFailed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "连接失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onReceive(SocketTransceiver transceiver, final String s,Integer flag) {
            Log.d("TAG", "onReceive");
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String msg = intent.getStringExtra("ServerData");
            //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            Integer flag = intent.getIntExtra("ServerFlag",0);
            Log.e("dataTest:",msg);
            Log.e("flagTest:",flag.toString());
            switch (flag){
                case 0:
                    Log.d("dataTest:",msg);
                    updateData(0,msg);
                    break;
                case 1:
                    updateData(1,msg);
                    break;
                case 2:
                    updateData(2,msg);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saoyisao = (Button) this.findViewById(R.id.saoyisao);
        saoyisao.setOnClickListener(this);
        stopConnect = (Button) this.findViewById(R.id.stopConnect);
        stopConnect.setOnClickListener(this);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Message");
        registerReceiver(myBroadcastReceiver, filter);

        // TODO Auto-generated method stub
        lv_data = (ListView) this.findViewById(R.id.listView_data);
        //给链表添加数据
        //getData();
        list = new ArrayList<Map<String, Object>>();
        //适配器，刚刚重写的！
        myAdapter = new MyAdapter(this,list);
        //设置适配器
        lv_data.setAdapter(myAdapter);
        lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map map = list.get(i);
                switch (Integer.parseInt(map.get("flag").toString())){
                    case 0:
                        Intent intent1 = new Intent(MainActivity.this,ThermometerActivity.class);
                        intent1.putExtra("Flag",0);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this,LineCharActivity.class);
                        intent2.putExtra("Flag",1);
                        startActivity(intent2);
                        break;

                    case 2:
                        Intent intent3 = new Intent(MainActivity.this,LineCharActivity.class);
                        intent3.putExtra("Flag",2);
                        startActivity(intent3);
                        break;
                }
            }
        });
    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saoyisao:
                Intent intent = new Intent(MainActivity.this,CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.stopConnect:
                Intent intent1 = new Intent(MainActivity.this,SocketService.class);
                stopService(intent1);
               // list.removeAll(list);
               // list = new ArrayList<Map<String, Object>>();
                list.clear();
               myAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 刷新界面显示
     *
     * @param isConnected
     */
    private void refreshUI(final boolean isConnected) {
        handler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
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
                //client.connect(hostIP, port);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    //填充数据
    public void getData() {
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.ic_launcher);
            map.put("bianliang", "温度");
            map.put("data", "最幽默老师");
            map.put("image_tail",  R.drawable.ic_launcher);
            map.put("flag", i);
            list.add(map);
        }
    }
    public void updateData(Integer flag,String msg) {
        boolean f1;
        f1 = true;
        for(int i = 0;i<list.size();++i){
            if(Integer.parseInt(list.get(i).get("flag").toString()) == flag){
                Map map = list.get(i);
                map.put("data", msg);
                //只用这里改变了
                f1 = false;
                Log.e("TAG","find");
            }
        }
        if(f1){
            Map<String, Object> map = new HashMap<String, Object>();
            switch (flag){
                case 0:
                    map.put("image", R.drawable.wendu);
                    map.put("bianliang", "温度");
                    map.put("data", msg);
                    map.put("image_tail",  R.drawable.arrow);
                    map.put("flag", flag);
                    break;
                case 1:
                    map.put("image", R.drawable.shidu);
                    map.put("bianliang", "空气");
                    map.put("data", msg);
                    map.put("image_tail",  R.drawable.arrow);
                    map.put("flag", flag);
                    break;
                case 2:
                    map.put("image", R.drawable.guang);
                    map.put("bianliang", "光照");
                    map.put("data", msg);
                    map.put("image_tail",  R.drawable.arrow);
                    map.put("flag", flag);
                    break;
            }
            Log.e("TAG","create");
            list.add(map);
        }
        myAdapter.notifyDataSetChanged();
    }
}
