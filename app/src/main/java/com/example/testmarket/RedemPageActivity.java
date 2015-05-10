package com.example.testmarket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.testmarket.R;

import model.Static_Info;
import controller.SamplePage_Controller;
import controller.Utils;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RedemPageActivity extends Activity {
	private String description, date, time, endDate, endTime, region, address,
			productId, redeem;
	private Button redemButton;

	private ImageView image;
	private TextView descriptionField, addressField, dateTimeField,
			endDateTimeField, regiontv, redeemed;
	private BroadcastReceiver initiate_redem_receiver;
//	ImageButton back_button;
	
	private WindowManager wmanager;
	private Display display;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.redem_page);

		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);

		// picture
		Bundle extras = getIntent().getExtras();
		image = (ImageView) findViewById(R.id.redempage_image);
		ViewGroup.LayoutParams params11 = image.getLayoutParams();
		params11.height = (size.y) / 2;
		image.setLayoutParams(params11);

		descriptionField = (TextView) findViewById(R.id.redempage_description);
		addressField = (TextView) findViewById(R.id.redempage_address);
		dateTimeField = (TextView) findViewById(R.id.redempage_dateTime);
		endDateTimeField = (TextView) findViewById(R.id.redempage_enddateTime);
		regiontv = (TextView) findViewById(R.id.redempage_region);
		redeemed = (TextView) findViewById(R.id.redemed);

		description = extras.getString("description", "");
		address = extras.getString("address", "");
		date = extras.getString("date", "");
		time = extras.getString("time", "");
		productId = extras.getString("productId", "");
		redeem = extras.getString("redeem", "false");
		endDate = extras.getString("endDate", "");
		endTime = extras.getString("endTime", "");
		region = extras.getString("region", "");

		redemButton = (Button) findViewById(R.id.redempage_redemButton);

		if (redeem.equals("true")) {
			redemButton.setVisibility(View.INVISIBLE);
			redeemed.setVisibility(View.VISIBLE);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDateandTime = sdf
				.format(Calendar.getInstance().getTime());

		String drawable_path = Static_Info.url_image + productId;
		
		try{
			Drawable drawable = getButtonDrawable(productId, drawable_path);
			image.setBackground(drawable);
		}catch(Exception e){}
		
		descriptionField.setText(description);
		addressField.setText(address);
		dateTimeField.setText(date + " " + time);
		endDateTimeField.setText(endDate + " " + endTime);
		regiontv.setText(region);
		redemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// sure dialog
				dialogBox("Redeem Sample",
						"Are Sure you will Redeem this sample ?");
			}
		});

		initiate_redem_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_sample_result(result);
			}

		};
		IntentFilter receiverfilter = new IntentFilter(
				Static_view_info.REDEM_BROADCAST);
		registerReceiver(initiate_redem_receiver, receiverfilter);
		
	/*	back_button = (ImageButton) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});*/
	}

	@Override
	protected void onStop() {
		try{
			unregisterReceiver(initiate_redem_receiver);
		}catch(Exception e){}
		super.onStop();
	}
	
	private void handle_sample_result(int result) {
		if (result == 1) {
			Toast.makeText(this, "The product is redeemed", Toast.LENGTH_SHORT)
					.show();
			redemButton.setVisibility(View.INVISIBLE);
			Static_view_info.sampleStateChanged = true;
		} else {
			Toast.makeText(this, "Redeeming failed", Toast.LENGTH_SHORT).show();
		}
	}

	private void sampleClicked() {
		new RedemPage_Controller(new ProgressDialog(RedemPageActivity.this),
				RedemPageActivity.this, description, Static_view_info.userId
						+ "").execute();
	}

	private Drawable getButtonDrawable(String filename, String image_url) {
		File cacheDir = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"Test market_place");
		if (!cacheDir.exists())
			cacheDir.mkdir();
		File f = new File(cacheDir + "/" + filename);
		if (!f.exists()) {
			try {
				int BUFFER_IO_SIZE = 8000;
				InputStream is = new BufferedInputStream(
						new URL(image_url).openStream(), BUFFER_IO_SIZE);

				f.createNewFile();
				OutputStream os = new FileOutputStream(new File(cacheDir + "/"
						+ filename));
				Utils.CopyStream(is, os);
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Drawable d = null;
			try{
				d = Drawable.createFromPath(cacheDir + "/" + filename);
			}catch(Exception e){}
			
			return d;
		} else {
			
			Drawable d = null;
			try{
				d = Drawable.createFromPath(cacheDir + "/" + filename);
			}catch(Exception e){}
			
			return d;
		}
	}

	private Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	@Override
	public void onBackPressed() {
		
	//	Intent in = new Intent(RedemPageActivity.this, HomePageActivity.class);
	//	startActivity(in);
		overridePendingTransition(R.anim.slide_right_show,
				R.anim.slide_right_hide);
		finish();
	}

	private void dialogBox(String title, String message) {
		int textSize = 16;
		TextView t = new TextView(RedemPageActivity.this);
		t.setTextSize(textSize);
		t.setTextColor(Color.BLACK);
		t.setText(title);
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setCustomTitle(t)
				.setMessage(message)
				.setPositiveButton("Yes",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// OK
								sampleClicked();

							}
						})
				.setNegativeButton("Cancel",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
		TextView m = (TextView) dialog.findViewById(android.R.id.message);
		m.setTextSize(textSize);
	}
}