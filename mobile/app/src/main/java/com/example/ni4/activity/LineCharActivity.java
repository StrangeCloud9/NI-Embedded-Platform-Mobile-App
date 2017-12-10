package com.example.ni4.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ni4.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class LineCharActivity extends Activity {
    private GraphicalView chart;
    private Timer timer = new Timer();
    private TimerTask task;
    private Float addY;
    private String  addX;
    private MyBroadcastReceiver myBroadcastReceiver;
    private TextView bianliang;
    private TextView danwei;

    String[] xkedu = new String[5] ;//x轴数据缓冲

    Float[] ycache = new Float[5];
    //private final static int SERISE_NR = 1; //曲线数量
    private XYSeries series;//用来清空第一个再加下一个
    private XYMultipleSeriesDataset dataset1;//xy轴数据源
    private XYMultipleSeriesRenderer render;
    SimpleDateFormat shijian   =   new   SimpleDateFormat("hh:mm:ss");

    Handler handler2;
    TextView guangzhi2;
    Integer flag1;

    private String IP;
    private int port;

    private Handler handler = new Handler(Looper.getMainLooper());

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String msg = intent.getStringExtra("ServerData");
            //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            Integer flag = intent.getIntExtra("ServerFlag",0);
            if(flag == flag1) {
                addY = Float.valueOf(msg);
                Message message = new Message();
                message.what = 200;
                handler2.sendMessage(message);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_char);
        guangzhi2=(TextView)this.findViewById(R.id.guangzhi2);
        bianliang = (TextView)this.findViewById(R.id.bianliang);
        danwei = (TextView)this.findViewById(R.id.danwei);

        Intent intent = getIntent();
        flag1 = intent.getIntExtra("Flag",0);
        switch (flag1){
            case 0:
                bianliang.setText("监测对象：温度");
                danwei.setText("℃");
                break;
            case 1:
                bianliang.setText("监测对象：空气质量");
                danwei.setText("百分比");
                break;
            case 2:
                bianliang.setText("监测对象：光照");
                danwei.setText("Lux");
                break;
        }

        //开启广播
        myBroadcastReceiver = new LineCharActivity.MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Message");
        registerReceiver(myBroadcastReceiver, filter);

        //制作曲线图，貌似不好下手只能变抄边理解，阿门
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.guangzhexian);
        chart = ChartFactory.getLineChartView(this, getdemodataset(), getdemorenderer());
        linearLayout.removeAllViews();//先remove再add可以实现统计图更新
        linearLayout.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        handler2 = new Handler(){
            public void handleMessage(Message msg) {
                updatechart();
                guangzhi2.setText(String.valueOf(addY));
            }
        };
        task = new TimerTask(){
            public void run() {
                Message msg=new Message();
                msg.what=200;
                //handler2.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 2000);
    }//oncreate结束

    //更新折线图
    private void updatechart() {
        //判断当前点集中到底有多少点，因为屏幕总共只能容纳5个，所以当点数超过5时，长度永远是5
        int length=series.getItemCount();
        int a=length;
        if(length>5){
            length=5;
        }
		/*try {
			if(guangzhi2.getText().toString()!=null){
				addY = Float.valueOf(guangzhi2.getText().toString());//要不要判断再说
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}*/
        addX=shijian.format(new java.util.Date());

        //addY = (float)(Math.random()*10);
        //移除数据集中旧的点集
        dataset1.removeSeries(series);
        if(a<5)//当数据集中不够五个点的时候直接添加就好，因为初始化的时候只有一个点，所以前几次更新的时候直接添加
        {
            series.add(a+1, addY);//第一个参数代表第几个点，要与下面语句中的第一个参数对应
            render.addXTextLabel(a+1, addX);
            xkedu[a]=addX;
        }
        else //超过了五个点要去除xcache【0】换成【1】的.....
        {
            //将旧的点集中x和y的数值取出来放入backup中，造成曲线向左平移的效果
            for(int i =0 ;i<length-1;i++){
                ycache[i]=(float) series.getY(i+1);
                xkedu[i]=xkedu[i+1];
            }

            //点集先清空，为了做成新的点集而准备
            series.clear();
            //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中

            for(int k =0 ;k<length-1;k++){
                series.add(k+1, ycache[k]);
                render.addXTextLabel(k+1, xkedu[k]);
            }
            xkedu[4]=addX;
            series.add(5, addY);
            render.addXTextLabel(5, addX);
        }
        //在数据集中添加新的点集
        dataset1.addSeries(series);
        //视图更新，没有这一步，曲线不会呈现动态
        chart.invalidate();
    }

    private XYMultipleSeriesRenderer getdemorenderer() {
        // TODO Auto-generated method stub
        render = new XYMultipleSeriesRenderer();
        switch (flag1){
            case 1:
                render.setChartTitle("空气质量实时曲线");
                render.setYTitle("空气污染指数");
                render.setYAxisMax(1);//设置y轴的范围
                break;
            case 2:
                render.setChartTitle("光照度实时曲线");
                render.setYTitle("光照度");
                render.setYAxisMax(55);//设置y轴的范围
                break;
        }
        //render.setChartTitle("光照度实时曲线");
        render.setChartTitleTextSize(20);//设置整个图表标题文字的大小
        render.setAxisTitleTextSize(16);//设置轴标题文字的大小
        render.setAxesColor(Color.BLACK);
        render.setXTitle("时间");
        //render.setYTitle("光照度");

        render.setLabelsTextSize(16);//设置轴刻度文字的大小
        render.setLabelsColor(Color.BLACK);
        render.setXLabelsColor(Color.BLACK);
        render.setYLabelsColor(0, Color.BLACK);
        render.setLegendTextSize(15);//设置图例文字大小
        //render.setShowLegend(false);//显示不显示在这里设置，非常完美

        XYSeriesRenderer r = new XYSeriesRenderer();//设置颜色和点类型
        r.setColor(Color.RED);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        r.setChartValuesSpacing(3);

        render.addSeriesRenderer(r);
        render.setYLabelsAlign(Paint.Align.RIGHT);//刻度值相对于刻度的位置
        render.setShowGrid(true);//显示网格
        //render.setYAxisMax(30);//设置y轴的范围
        render.setYAxisMin(-2);
        render.setYLabels(7);//分七等份


        render.setInScroll(true);
        render.setLabelsTextSize(14);
        render.setLabelsColor(Color.BLACK);
        //render.getSeriesRendererAt(0).setDisplayChartValues(true); //显示折线上点的数值
        render.setPanEnabled(false,false);//禁止报表的拖动
        render.setPointSize(5f);//设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
        render.setMargins(new int[]{20,30,90,10}); //设置图形四周的留白
        render.setMarginsColor(Color.WHITE);
        render.setXLabels(0);// 取消X坐标的数字zjk,只有自己定义横坐标是才设为此值



        return render;
    }

    private XYMultipleSeriesDataset getdemodataset() {
        // TODO Auto-generated method stub
        dataset1=new XYMultipleSeriesDataset();//xy轴数据源
        switch (flag1){
            case 1:
                series = new XYSeries("空气 ");//这个事是显示多条用的，显不显示在上面render设置
                break;
            case 2:
                series = new XYSeries("光照度 ");//这个事是显示多条用的，显不显示在上面render设置
                break;
        }
        //series = new XYSeries("光照度 ");//这个事是显示多条用的，显不显示在上面render设置
        //这里相当于初始化，初始化中无需添加数据，因为如果这里添加第一个数据的话，
        //很容易使第一个数据和定时器中更新的第二个数据的时间间隔不为两秒，所以下面语句屏蔽
        //这里可以一次更新五个数据，这样的话相当于开始的时候就把五个数据全部加进去了，但是数据的时间是不准确或者间隔不为二的
        //for(int i=0;i<5;i++)
        //series.add(1, Math.random()*10);//横坐标date数据类型，纵坐标随即数等待更新


        dataset1.addSeries(series);
        return dataset1;
    }

    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }
}
