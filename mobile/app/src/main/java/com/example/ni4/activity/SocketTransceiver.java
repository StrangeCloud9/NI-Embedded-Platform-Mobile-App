package com.example.ni4.activity;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Socket收发器 通过Socket发送数据，并使用新线程监听Socket接收到的数据
 *
 * @author jzj1993
 * @since 2015-2-22
 */
public abstract class SocketTransceiver implements Runnable {

    protected Socket socket;
    protected InetAddress addr;
    protected DataInputStream in;
    protected DataOutputStream out;
    private boolean runFlag;

    /**
     * 实例化
     *
     * @param socket
     *            已经建立连接的socket
     */
    public SocketTransceiver(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
    }

    /**
     * 获取连接到的Socket地址
     *
     * @return InetAddress对象
     */
    public InetAddress getInetAddress() {
        return addr;
    }

    /**
     * 开启Socket收发
     * <p>
     * 如果开启失败，会断开连接并回调{@code onDisconnect()}
     */
    public void start() {
        runFlag = true;
        new Thread(this).start();
    }

    /**
     * 断开连接(主动)
     * <p>
     * 连接断开后，会回调{@code onDisconnect()}
     */
    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送字符串
     *
     * @param s
     *            字符串
     * @return 发送成功返回true
     */
    public boolean send(String s) {
        if (out != null) {
            try {
                out.writeUTF(s);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 监听Socket接收的数据(新线程中运行)
     */
    @Override
    public void run() {
        try {            Log.d("TAG", "onReceive");

            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            runFlag = false;
        }
        while (runFlag) {
            try {
                Log.d("TAG", "onFlag");
                String result = null;
                Integer flag = 0;
                //result = testInputDataStream();
                //this.onReceive(addr,result+"\n",0);

                BufferedInputStream reader = new BufferedInputStream(in);
                InputStreamReader rs = new InputStreamReader(reader);
                char[] cr = new char[100];
                rs.read(cr);
                flag = convertFlag(cr);
               result = convertInputDataString(cr);
                //this.onReceive(addr, s+"℃"+"\n");
                this.onReceive(addr,result,flag);
            } catch ( Exception e) {
                // 连接被断开(被动)
                runFlag = false;
            }
        }
        // 断开连接
        try {
            in.close();
            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onDisconnect(addr);
    }

    /**
     * 接收到数据
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr
     *            连接到的Socket地址
     * @param s
     *            收到的字符串
     */
    public abstract void onReceive(InetAddress addr, String s,Integer flag);

    /**
     * 连接断开
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param addr
     *            连接到的Socket地址
     */
    public abstract void onDisconnect(InetAddress addr);
    public static String convertStreamTOString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try
        {
            while((line = reader.readLine())!=null)
            {
                sb.append(line);
            }
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String testInputDataStream(){
        String result = "";
        try {
            BufferedInputStream reader = new BufferedInputStream(in);
            InputStreamReader rs = new InputStreamReader(reader);
            char[] cr = new char[100];
            rs.read(cr);
            result = new String(cr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String convertInputDataString(char[] cr){
        String s = "";
        try {
            double result = 0;
            int j = 0;
            String rrrr = "";
            while (cr[j]!=0){
                rrrr = rrrr + cr[j];
                j++;
            }
            System.out.println("完整数据："+rrrr);
            String substr = rrrr.substring(5,rrrr.length()-1);
            System.out.println("完整数据sub："+substr);
            String[] subsubstrs = substr.split("E" );
            float r1 = Float.valueOf(subsubstrs[0]);
            char r2 = subsubstrs[1].charAt(0);
            char r3 = subsubstrs[1].charAt(1);
            System.out.println("数值部分："+r1 + " 计数部分："+ r2 + r3);
            if(r3!='0'){
                if(r2 == '+'){
                    int r4 = r3-'0';
                    while (r4 > 0){
                        r1 = r1 * 10;
                        r4--;
                    }
                }else if(r2 == '-'){
                    int r4 = r3-'0';
                    while (r4 > 0){
                        r1 = r1 / 10;
                        r4--;
                    }
                }
            }
            System.out.println("最终数值："+ r1);
            /*
            int i = 6;
            int num=0;
            boolean flag = false;
            while(cr[i]!='E')
            {
                if(flag == false) {
                    if (cr[i] != '.') {
                        char[] chararray = {cr[i]};
                        result = result * 10 + Integer.parseInt(new String(chararray));
                    } else {
                        flag = true;
                    }
                }
                else
                {
                    char[] chararray = {cr[i]};
                    result = result * 10 + Integer.parseInt(new String(chararray));
                    num+=1;
                }
                i++;
            }
            i++;
            if(cr[i]!=0){num++;}
            while(num>1)
            {
                result = result /10;
                num--;
            }*/

            result = r1;
            char[] cr2 = new char[5];
            for(int i = 0;i<5;++i){
                cr2[i] = cr[i];
            }
            s = new String(cr2);
            Log.e("变量",s);
            switch (s){
                case "wendu":
                    System.out.println("温度："+ result);
                    result=result*34.1+3.2411;
                    break;
                case "shidu":
                    System.out.println("空气："+ result);
                    result = result*1023/500;
                    break;
                case "guang":
                    System.out.println("光照："+ result);
                    result = result*(-4)+25;
                    break;
            }
            s = Double.toString(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    public Integer convertFlag(char[] cr){
        String s = "";
        Integer result = 0;
        try {
            char[] cr2 = new char[5];
            for(int i = 0;i<5;++i){
                cr2[i] = cr[i];
            }
            s = new String(cr2);
            Log.e("wendu",s);
            switch (s){
                case "wendu":result = 0;break;
                case "shidu":result = 1;break;
                case "guang":result = 2;break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}

