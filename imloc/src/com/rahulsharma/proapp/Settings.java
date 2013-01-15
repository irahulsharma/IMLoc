package com.rahulsharma.proapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		final EditText et1 = (EditText) findViewById(R.id.editText1);
		final EditText et2 = (EditText) findViewById(R.id.editText2);
		
		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);
		
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int m = 0;
				try{
					m = Integer.parseInt(et1.getText().toString());
				}catch(NumberFormatException e){
					Log.e("numberformat", "Could not parse the string.");
				}
				Intent intent = new Intent();
				intent.putExtra("num", m);
				intent.putExtra("url", et2.getText().toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		
		b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}	
	
}
