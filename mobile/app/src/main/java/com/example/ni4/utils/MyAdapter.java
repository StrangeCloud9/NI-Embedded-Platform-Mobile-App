package com.example.ni4.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ni4.R;

import java.util.List;
import java.util.Map;


/**
 * Created by 蟹老板 on 2017/11/14.
 */

public class MyAdapter extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public MyAdapter(Context context, List<Map<String, Object>> data) {
        //传入的data，就是我们要在listview中显示的信息
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }
    //这里定义了一个类，用来表示一个item里面包含的东西，像我的就是一个imageView和三个TextView，按自己需要来
    public class Info {
        public ImageView image;
        public TextView tv_bianliang;
        public TextView tv_data;
        public ImageView image_tail;
    }
    //所有要返回的数量，Id，信息等，都在data里面，从data里面取就好
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    //跟actvity中的oncreat()差不多，目的就是给item布局中的各个控件对应好，并添加数据
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Info info = new Info();
        convertView = layoutInflater.inflate(R.layout.item_datalist, null);
        info.image = (ImageView) convertView.findViewById(R.id.imageView_headpic);
        info.tv_bianliang = (TextView) convertView
                .findViewById(R.id.textView_bianliang);
        info.tv_data = (TextView) convertView
                .findViewById(R.id.textView_data);
        info.image_tail = (ImageView) convertView
                .findViewById(R.id.imageView_tailpic);

        //设置数据
        info.image.setImageResource((Integer) data.get(position).get("image"));
        info.tv_bianliang.setText((String) data.get(position).get(
                "bianliang"));
        info.tv_data.setText((String) data.get(position).get(
                "data"));
        info.image_tail.setImageResource((Integer) data.get(position).get("image_tail"));
        return convertView;
    }

    public void updataView(int posi, ListView listView,String data_change){
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        Info info = new Info();
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
            View view = listView.getChildAt(posi - visibleFirstPosi);
            info = (Info) view.getTag();
            //info.tv_university = (TextView)view.findViewById(R.id.textView_university);
            if(info == null){
                Log.d("sssss","pass");
            }else {
                info.tv_data.setText("ssss");
            }
            data.get(posi).put("university", data);
        } else {
        }
    }
}
