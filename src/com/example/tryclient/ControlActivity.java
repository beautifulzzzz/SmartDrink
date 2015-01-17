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
	private SurfaceView mSurface; //��ͼ��
    private SurfaceHolder mHolder;  
	//��Ϣ���(�߳����޷����н�����£�����Ҫ����Ϣ���߳��﷢�ͳ�������Ϣ�������д���)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) 
		{
			Bundle bundle = msg.getData();
			String now = bundle.getString("msg");
			//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
			if (msg.what == 0x01) 
			{
				toast_show("��ˮ����ʼ����!");
			}
			else if (msg.what == 0x02) 
			{
				toast_show("��ˮ���ر�!");
			}
			else if (msg.what == 0x03) 
			{
				toast_show("��ˮ��ʵʱ״̬����!"+"  "+MainActivity.wen_du+"  "+MainActivity.shui_wei);
				draw(MainActivity.wen_du);
			}
			else
			{
				toast_show("���ִ���!");
			}
		}
		//toast��ʾ��
		private void toast_show(String msg) {
			Toast toast = Toast.makeText(getApplicationContext(),
				     msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		//��ͼ��
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
	        int tem = 0;//�̶�0~100
	        while (ydegree > 55) {  
	            canvas.drawLine(60, ydegree, 67, ydegree, mPaint);  
	            if (ydegree % 20 == 0) {  
	                canvas.drawLine(60, ydegree, 72, ydegree, paintLine);  
	                canvas.drawText(tem + "", 70, ydegree + 4, mPaint);  
	                tem+=10;  
	            }  
	            ydegree = ydegree - 2;  
	        }  
	        mHolder.unlockCanvasAndPost(canvas);// ������Ļ��ʾ����  
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
				//�����߳� ����������ͺͽ�����Ϣ
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btHeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Heat";
				//�����߳� ����������ͺͽ�����Ϣ
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btShut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Shut";
				//�����߳� ����������ͺͽ�����Ϣ
				Log.i(TAG, "Start thread");
				new MyThread(orderMsg).start();
			}
		});
		
		btUpdata.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String orderMsg="Updata";
				//�����߳� ����������ͺͽ�����Ϣ
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
			DataInputStream DataIn = null;//���ݴ������������
	        DataOutputStream DataOut = null;
			byte data_of_get_server = 0;//�ӷ��������ص�����
			Message msg = new Message();//��Ϣ
			Bundle bundle = new Bundle();
			bundle.clear();
			try
			{
				socket = new Socket();
				socket.connect(new InetSocketAddress(mAddress, mPort), 8000);
		
				//���������ʵ����
				out=socket.getOutputStream();
				in=socket.getInputStream();
	            DataIn = new DataInputStream(in);
	            DataOut=new DataOutputStream(out);
				
	            //��ȡ�������ķ�������
				//���������õ�byte���ݽ��з���
				/*
				TCP�ͻ���:��������ӷ������������
				PS��������ֻ����1��char,����Ҳ��һ��char,�������ݾ�Ϊ16����
				*/
	            if(orderMsg.equals("Heat"))//��������
				{
	            	msg.what = 0x01;//��Ϣ���
					DataOut.writeByte('0');	
					Log.i(TAG, "flush ǰ");
					out.flush();
					Log.i(TAG, "flush ��");
					data_of_get_server=DataIn.readByte();
					Log.i(TAG, "��ȡ���ݺ�");
				}
				else if(orderMsg.equals("Shut"))
				{
					msg.what = 0x02;//��Ϣ���
					DataOut.writeByte('0');//ֹͣ����
					out.flush();
					data_of_get_server=DataIn.readByte();
				}
				else if(orderMsg.equals("Updata"))
				{
					msg.what = 0x03;//��Ϣ���
					DataOut.writeByte('w');//ˢ���¶���Ϣ
					out.flush();
					data_of_get_server=DataIn.readByte();
					MainActivity.wen_du=data_of_get_server;
					
					DataOut.writeByte('s');//ˢ�������Ϣ
					out.flush();
					data_of_get_server=DataIn.readByte();
					MainActivity.shui_wei=data_of_get_server;
				}
	            //����Ϣ���͸�UIˢ����Ϣ�����
	            bundle.putByte("msg",data_of_get_server);
				msg.setData(bundle);
				myHandler.sendMessage(msg);
			}
			catch(Exception e){
				e.printStackTrace();
				//Intent intent = new Intent(ControlActivity.this,MainActivity.class);
				//Log.i(TAG, "��תǰ");
				//startActivity(intent);
				//����Ϣ���͸�UIˢ����Ϣ�����
				msg.what = 0x04;//��Ϣ���
	            bundle.putByte("msg",data_of_get_server);
				msg.setData(bundle);
				myHandler.sendMessage(msg);
			}finally{
				try{
					if(in!=null)in.close();Log.i(TAG, "��ȡ���ݺ�1");
					if(out!=null)out.close();Log.i(TAG, "��ȡ���ݺ�2");	
					if(DataOut!=null)DataOut.close();Log.i(TAG, "��ȡ���ݺ�3");
					if(DataIn!=null)DataIn.close();Log.i(TAG, "��ȡ���ݺ�4");
					if(socket!=null)socket.close();Log.i(TAG, "��ȡ���ݺ�5");
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
