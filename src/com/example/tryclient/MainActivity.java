package com.example.tryclient;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity 
{
	private final String TAG = "MainActivity";
	private EditText et01;
	private EditText et02;
	private Button btOK;
	private Button btCancel;
	public static String userIP = "192.168.1.130";			//IP�Ͷ˿ں�
	public static int userPort = 8000;
	public static int wen_du;			//��ǰ�¶�
	public static int shui_wei;			//��ǰˮλ
	public static int state;			//��ǰ״̬0�رգ�1��ˮ��2����
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et01 = (EditText)findViewById(R.id.et_01);
		et02 = (EditText)findViewById(R.id.et_02);
		btOK = (Button)findViewById(R.id.bt_OK);
		btCancel = (Button)findViewById(R.id.bt_Cancel);
		
			
		btOK.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				//userIP = et01.getText().toString();
				//userPort = Integer.parseInt(et02.getText().toString());
				//�������ƽ���
				Intent intent = new Intent(MainActivity.this,ControlActivity.class);
				Log.i(TAG, "��תǰ");
				startActivity(intent);
			}
		});
		btCancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				et01.setText("");
				et02.setText("");
			}
		});
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
