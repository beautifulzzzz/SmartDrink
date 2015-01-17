package com.example.tryclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class ControlActivity extends Activity implements Callback {
	
	private final String TAG = "ControlActivity";
	private final String mAddress = MainActivity.userIP;
	private final int mPort = MainActivity.userPort;
	private Socket socket = null;
	private Button btHeat,btShut,btUpdata;
	private SurfaceView mSurface; //绘图区
    private SurfaceHolder mHolder;  
	//消息句柄(线程里无法进行界面更新，所以要把消息从线程里发送出来在消息句柄里进行处理)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) 
		{
			Bundle bundle = msg.getData();
			String now = bundle.getString("msg");
			//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			if (msg.what == 0x01) 
			{
				toast_show("饮水机开始加热!");
			}
			else if (msg.what == 0x02) 
			{
				toast_show("饮水机关闭!");
			}
			else if (msg.what == 0x03) 
			{
				toast_show("饮水机实时状态更新!"+"  "+MainActivity.wen_du+"  "+MainActivity.shui_wei);
				draw(MainActivity.wen_du);
			}
			else
			{
				toast_show("出现错误!");
			}
		}
		//toast显示用
		private void toast_show(String msg) {
			Toast toast = Toast.makeText(getApplicationContext(),
				     msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		//画图像
		private void draw(int wen_du) {  
	        int y = 260 - wen_du * 2;  
	        Canvas canvas = mHolder.lockCanvas();  
	        Paint mPaint = new Paint();  
	        mPaint.setColor(Color.WHITE);  
	        canvas.drawRect(40, 50, 60, 280, mPaint);  
	        Paint paintCircle = new Paint();  
	        paintCircle.setColor(Color.RED);  
	        Paint paintLine = new Paint();  
	        paintLine.setColor(Color.BLUE);  
	        canvas.drawRect(40, y, 60, 280, paintCircle);  
	        canvas.drawCircle(50, 300, 25, paintCircle);  
	        int ydegree = 260;  
	        int tem = 0;//刻度0~100
	        while (ydegree > 55) {  
	            canvas.drawLine(60, ydegree, 67, ydegree, mPaint);  
	            if (ydegree % 20 == 0) {  
	                canvas.drawLine(60, ydegree, 72, ydegree, paintLine);  
	                canvas.drawText(tem + "", 70, ydegree + 4, mPaint);  
	                tem+=10;  
	            }  
	            ydegree = ydegree - 2;  
	        }  
	        mHolder.unlockCanvasAndPost(canvas);// 更新屏幕显示内容  
	    }  
	};
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_activity);
		btHeat = (Button)findViewById(R.id.bt_heat);
		btShut = (Button)findViewById(R.id.bt_shut);
		btUpdata = (Button)findViewById(R.id.bt_updata);
		mSurface = (SurfaceView) findViewById(R.id.surface);  
		mHolder = mSurface.getHolder();  
        mHolder.addCallback(this);  
		
		btHeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Heat";
				//启动线程 向服务器发送和接收信息
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btHeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Heat";
				//启动线程 向服务器发送和接收信息
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btShut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Shut";
				//启动线程 向服务器发送和接收信息
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btUpdata.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Updata";
				//启动线程 向服务器发送和接收信息
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
	}
	
	class MyThread extends Thread 
	{
		String orderMsg;
		MyThread(String str)
		{
			orderMsg=str;
		}
		@SuppressLint("SimpleDateFormat")
		public void run()
		{
			OutputStream out = null;
			InputStream in = null;
			DataInputStream DataIn = null;//数据传输输入输出流
	        DataOutputStream DataOut = null;
			byte data_of_get_server = 0;//从服务器返回的数据
			Message msg = new Message();//消息
			Bundle bundle = new Bundle();
			bundle.clear();
			try
			{
				socket = new Socket();
				socket.connect(new InetSocketAddress(mAddress, mPort), 8000);
		
				//输入输出流实例化
				out=socket.getOutputStream();
				in=socket.getInputStream();
	            DataIn = new DataInputStream(in);
	            DataOut=new DataOutputStream(out);
				
	            //读取服务器的返回数据
				//服务器采用单byte数据进行发送
				/*
				TCP客户端:输入命令从服务器获得数据
				PS：服务器只接受1个char,返回也是一个char,上述数据均为16进制
				*/
	            if(orderMsg.equals("Heat"))//加热命令
				{
	            	msg.what = 0x01;//消息类别
					DataOut.writeByte('0');	
					Log.i(TAG, "flush 前");
					out.flush();
					Log.i(TAG, "flush 后");
					data_of_get_server=DataIn.readByte();
					Log.i(TAG, "读取数据后");
				}
				else if(orderMsg.equals("Shut"))
				{
					msg.what = 0x02;//消息类别
					DataOut.writeByte('0');//停止加热
					out.flush();
					data_of_get_server=DataIn.readByte();
				}
				else if(orderMsg.equals("Updata"))
				{
					msg.what = 0x03;//消息类别
					DataOut.writeByte('w');//刷新温度信息
					out.flush();
					data_of_get_server=DataIn.readByte();
					MainActivity.wen_du=data_of_get_server;
					
					DataOut.writeByte('s');//刷新深度信息
					out.flush();
					data_of_get_server=DataIn.readByte();
					MainActivity.shui_wei=data_of_get_server;
				}
	            //将消息发送给UI刷新消息句柄处
	            bundle.putByte("msg",data_of_get_server);
				msg.setData(bundle);
				myHandler.sendMessage(msg);
			}
			catch(Exception e){
				e.printStackTrace();
				//Intent intent = new Intent(ControlActivity.this,MainActivity.class);
				//Log.i(TAG, "跳转前");
				//startActivity(intent);
				//将消息发送给UI刷新消息句柄处
				msg.what = 0x04;//消息类别
	            bundle.putByte("msg",data_of_get_server);
				msg.setData(bundle);
				myHandler.sendMessage(msg);
			}finally{
				try{
					if(in!=null)in.close();Log.i(TAG, "读取数据后1");
					if(out!=null)out.close();Log.i(TAG, "读取数据后2");	
					if(DataOut!=null)DataOut.close();Log.i(TAG, "读取数据后3");
					if(DataIn!=null)DataIn.close();Log.i(TAG, "读取数据后4");
					if(socket!=null)socket.close();Log.i(TAG, "读取数据后5");
				}catch(Exception e){}
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
