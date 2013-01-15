package com.rahulsharma.proapp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Main extends Activity implements Runnable{
    
	ImageView iv;
	List<KeyPoint> kp = new ArrayList<KeyPoint>();
	Bitmap bm;
	Mat img;
	FeatureDetector fd;
	int size;
	String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        iv = (ImageView) findViewById(R.id.imageView1);
        
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Getting Your Location...Please Wait!");
        pd.setIndeterminate(true);
        pd.setCancelable(true);
        
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.iit_kharagpur_main_building);
        iv.setImageBitmap(bm);
        
        Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);				
			}
		});
        
        Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new OnClickListener() {
			
        Handler transThreadHandler = new Handler();
        JSONObject jsonobject = null;

			@Override
			public void onClick(View v) {
				if(bm != null){
					pd.show();
					//Log.v("#1", "Working till now!" + url);
					//Log.v("#2", "Working till now!" + size);
					new Thread(new Runnable(){
				        public void run(){
				        	img = Utils.bitmapToMat(bm);
				        	fd = FeatureDetector.create(FeatureDetector.SURF);
				        	fd.detect(img, kp);
				        	//Log.v("#3", "Working till now!");
				        	JSONObject obj = new JSONObject();
				        	Log.v("#4", "Working till now!" + size);
				        	
				        	try {
				        		for(int i=0; i<size; i++){
				        			KeyPoint keypoint = kp.get(i);
				        			JSONObject object = new JSONObject();
				        			object.put("x", keypoint.pt.x);
				        			object.put("y", keypoint.pt.y);
				        			object.put("size", keypoint.size);
				        			object.put("angle", keypoint.angle);
				        			object.put("response", keypoint.response);
				        			object.put("octave", keypoint.octave);
				        			object.put("class_id", keypoint.class_id);
				        			obj.put("keypoint", object);
				        		}
				        	} catch (JSONException e) {
				        		// TODO Auto-generated catch block
				        		e.printStackTrace();
				        	}
				        	//Log.v("#5", "Working till now!" + url);
				        	HttpClient client = new DefaultHttpClient();
				        	HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				        	StringBuilder builder = new StringBuilder();
				        	try{
				        		HttpPost post = new HttpPost(url);
				        		StringEntity se = new StringEntity( "JSON: " + obj.toString());  
				        		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				        		post.setEntity(se);
				        		HttpResponse response = client.execute(post);
				        		HttpEntity entity = response.getEntity();
				        		StatusLine statusline = response.getStatusLine();
				        		int statuscode = statusline.getStatusCode();
				        		if(statuscode == 200){
				        			InputStream instream = entity.getContent();                    
				        			BufferedReader reader = new BufferedReader(
				        					new InputStreamReader(instream));
				        			String line;
				        			while ((line = reader.readLine()) != null) {
				        				builder.append(line);
				        			}	
				        			jsonobject = new JSONObject(builder.toString());
				        		}
				        		else{
				        			Log.e("TAG", "Failed to download file!");
				        		}
				        	}catch(Exception e){
				        		e.printStackTrace();
				        	}
				        transThreadHandler.post(new Runnable(){
				        	public void run(){
				        		//KeyPoint key = kp.get(0);
				        		//double valx = key.pt.x;
				        		//double valy = key.pt.y;
				        		//Log.v("keypoint", "X coordinate" + valx);
				        		//Log.v("keypoint", "Y coordinate" + valy);
				        		double latitude = 0, longitude = 0;
								try{
									latitude = jsonobject.getDouble("latitude");
									longitude = jsonobject.getDouble("longitude");
								}catch (Exception e) {
									e.printStackTrace();
								}
								Intent map_intent = new Intent(Main.this, Osmmap.class);
								map_intent.putExtra("latitude", latitude);
								map_intent.putExtra("longitude", longitude);
								startActivity(map_intent);
				        		pd.dismiss();
				        	}
				        });
				    }
				}).start();
			
				}
				else{
					AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
					builder.setMessage("You must take a photo!");
					builder.setCancelable(true);
					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();							
						}
					});
					builder.show();
				}
									
			}
		});
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    		case(0):{
    			Bitmap btm = (Bitmap) data.getExtras().get("data");
    			bm = btm.copy(Bitmap.Config.ARGB_8888, false);
    			iv.setImageBitmap(bm);
    		}
    		case(1):{
    			if(resultCode == Activity.RESULT_OK){
    				url = data.getExtras().getString("url");
        			size = data.getExtras().getInt("num");
    			}
    		}
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mymenu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.settingsitem){
    		startActivityForResult(new Intent(this, Settings.class), 1);
    	}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}