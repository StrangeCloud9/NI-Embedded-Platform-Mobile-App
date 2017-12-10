package com.example.ni4.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ni4.R;


public class ThermometerActivity extends Activity {
    private LinearLayout alcohol;
    private LinearLayout meter;
    private TextView thermo_c;
    private TextView thermo_f;
    private MyBroadcastReceiver myBroadcastReceiver;
    public float staratemp;
    public float temp;
    private float temperatureC;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String msg = intent.getStringExtra("ServerData");
            //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            Integer flag = intent.getIntExtra("ServerFlag",0);
            if(flag == 0){
                System.out.println("温度："+msg);
                float tempperature = Float.valueOf(msg);
                setTemperatureC(tempperature);// 设置温度
                mUpdateUi();// 更新UI

            }
        }
    }

    /**
     * 获取华氏温度
     *
     */
    public float getTemperatureF() {
        float temperatureF = (temperatureC * 9 / 5) + 32;
        return getFloatOne(temperatureF);
    }

    /**
     * 保留一位小数点
     *
     */
    public float getFloatOne(float tempFloat) {
        return (float) (Math.round(tempFloat * 10)) / 10;
    }

    /**
     * 获取摄氏温度
     *
     */
    public float getTemperatureC() {
        return getFloatOne(temperatureC);
    }

    public void setTemperatureC(float temperatureC) {
        this.temperatureC = temperatureC;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBroadcastReceiver = new ThermometerActivity.MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Message");
        registerReceiver(myBroadcastReceiver, filter);
        setContentView(R.layout.activity_thermometer);
        meter = ((LinearLayout) findViewById(R.id.meter));
        alcohol = ((LinearLayout) findViewById(R.id.alcohol));
        thermo_c = (TextView) findViewById(R.id.thermo_c);
        thermo_f = (TextView) findViewById(R.id.thermo_f);
        float temperatureValue = (float) 15.0; // 得到温度
        setTemperatureC(temperatureValue);// 设置温度
        mUpdateUi();// 更新UI
    }

    /**
     * 更新刻度上水银柱的长度
     *
     */
    private void mUpdateUi() {
        Log.d("ssssss","sssssssssssssssssss");
        ScaleAnimation localScaleAnimation1 = new ScaleAnimation(1.0F, 1.0F, this.staratemp, this.temp, 1, 0.5F, 1,
                1.0F);
        localScaleAnimation1.setDuration(2000L);
        localScaleAnimation1.setFillEnabled(true);
        localScaleAnimation1.setFillAfter(true);
        this.alcohol.startAnimation(localScaleAnimation1);
        this.staratemp = this.temp;

        ScaleAnimation localScaleAnimation2 = new ScaleAnimation(1.0F, 1.0F, 1.0F, 1.0F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation2.setDuration(10L);
        localScaleAnimation2.setFillEnabled(true);
        localScaleAnimation2.setFillAfter(true);
        this.meter.startAnimation(localScaleAnimation2);

        // 把刻度表看出总共700份，如何计算缩放比例。从-20°到50°。
        // 例如，现在温度是30°的话，应该占（30+20）*10=500份 其中20是0到-20°所占有的份
        this.temp = (float) ((20.0F + getTemperatureC()) * 10) / (70.0F * 10);

        thermo_c.setText(getTemperatureC() + "");
        thermo_f.setText(getTemperatureF() + "");
        Log.d("ssssss","sssssssssssssssssss");
    }
}
