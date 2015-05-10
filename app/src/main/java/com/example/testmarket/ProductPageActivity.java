package com.example.testmarket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.testmarket.R;

import model.Static_Info;
import controller.CheckSampledController;
import controller.SamplePage_Controller;
import controller.Utils;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProductPageActivity extends Activity {
	private String description, quantity, date, time, endDate, endTime, region,
			address, productId, username;
	private Button sampleButton;

	private ImageView image;
	private TextView descriptionField, quantityField, addressField,
			dateTimeField, endDateTimeField, regionTV, userField;
	private BroadcastReceiver initiate_products_receiver, checkSampledReceiver;
	private LinearLayout outerlayout;
	private WindowManager wmanager;
	private Display display;
	private ProgressBar progressbar;
//	ImageButton back_button;
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.product_page);

		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);

		// picture
		Bundle extras = getIntent().getExtras();
		image = (ImageView) findViewById(R.id.productpage_image);
		ViewGroup.LayoutParams params11 = image.getLayoutParams();
		params11.height = (size.y) / 2;
		image.setLayoutParams(params11);

		descriptionField = (TextView) findViewById(R.id.productpage_description);
		quantityField = (TextView) findViewById(R.id.productpage_quantity);
		addressField = (TextView) findViewById(R.id.productpage_address);
		dateTimeField = (TextView) findViewById(R.id.productpage_dateTime);
		endDateTimeField = (TextView) findViewById(R.id.productpage_enddateTime);
		userField = (TextView) findViewById(R.id.productpage_user);
		regionTV = (TextView) findViewById(R.id.productpage_region);

		description = extras.getString("description", "");
		address = extras.getString("address", "");
		username = extras.getString("username", "");
		date = extras.getString("date", "");
		time = extras.getString("time", "");
		endDate = extras.getString("endDate", "");
		endTime = extras.getString("endTime", "");
		region = extras.getString("region", "");
		quantity = extras.getString("quantity", "");
		productId = extras.getString("productId", "");

		sampleButton = (Button) findViewById(R.id.productpage_sampleButton);
		outerlayout = (LinearLayout) findViewById(R.id.productpage_outerlayout);
		progressbar = (ProgressBar) findViewById(R.id.prodcutpage_progressBar1);
		progressbar.setVisibility(View.VISIBLE);
		outerlayout.setVisibility(View.GONE);

		if (quantity.equals("0")) {
			sampleButton.setVisibility(View.INVISIBLE);
			quantityField.setTextColor(Color.RED);
		} else {
			quantityField.setTextColor(Color.GREEN);
		}
		
		String drawable_path = Static_Info.url_image + productId;
		
		try{
			Drawable drawable = getButtonDrawable(productId, drawable_path);
			image.setBackground(drawable);
		}catch(Exception e){}
		
		
		descriptionField.setText(description);
		quantityField.setText(quantity);
		addressField.setText(address);
		dateTimeField.setText(date + " " + time);
		endDateTimeField.setText(endDate + " " + endTime);
		userField.setText(username);
		regionTV.setText(region);

		sampleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				sampleClicked();

			}
		});

		new CheckSampledController(this, Static_view_info.userId + "",
				productId).execute();

		initiate_products_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_sample_result(result);
			}

		};
		IntentFilter receiverfilter = new IntentFilter(
				Static_view_info.SAMPLE_BROADCAST);
		registerReceiver(initiate_products_receiver, receiverfilter);

		checkSampledReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_checkSample_result(result);
			}

		};
		IntentFilter receiverfilter1 = new IntentFilter(
				Static_view_info.CHECK_SAMPLED_BROADCAST);
		registerReceiver(checkSampledReceiver, receiverfilter1);

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
			unregisterReceiver(initiate_products_receiver);
			unregisterReceiver(checkSampledReceiver);
		}catch(Exception e){}
		super.onStop();
	}
	
	private void handle_sample_result(int result) {
		if (result == 1) {
			Toast.makeText(this, "The product is added to your samples",
					Toast.LENGTH_SHORT).show();
			Static_view_info.sampleStateChanged = true;
			int q = Integer.parseInt(quantity);
			q--;
			quantity = q + "";
			quantityField.setText(quantity);
			sampleButton.setVisibility(View.INVISIBLE);
		} else {
			Toast.makeText(this, "Sampling failed", Toast.LENGTH_SHORT).show();
		}
	}

	private void sampleClicked() {
		new SamplePage_Controller(new ProgressDialog(ProductPageActivity.this),
				ProductPageActivity.this, description, Static_view_info.userId
						+ "").execute();
	}

	private Drawable getButtonDrawable(String filename, String image_url) {
		
		System.out.println("filename = "+ filename + "  image_url = "+ image_url);
		
		File cacheDir = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"Test market_place");
		// filename = image_url.hashCode() + "";
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
		
	//	Intent in = new Intent(ProductPageActivity.this, HomePageActivity.class);
	//	startActivity(in);
		overridePendingTransition(R.anim.slide_right_show,
				R.anim.slide_right_hide);
		finish();
	}

	private void handle_checkSample_result(int result) {
		if (result == 1) {
			sampleButton.setVisibility(View.GONE);
		} else if (result == 0) {
			sampleButton.setVisibility(View.VISIBLE);
		}

		progressbar.setVisibility(View.GONE);
		outerlayout.setVisibility(View.VISIBLE);
	}
}
