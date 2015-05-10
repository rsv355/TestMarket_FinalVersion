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
import controller.UpdateListing_Controller;
import controller.UpdateProfile_Controller;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

public class MyListingsItemPageActivity extends Activity {

	private BroadcastReceiver listing_update_receiver;

	private RelativeLayout tabBarLayout;
	private ImageButton removeButton, editButton;
	private Button doneButton;
	private int pressed = -1;

	private String description, date, time, endDate, endTime, region, address,
			productId, redeem, quantity;
	
//	ImageButton back_button;
	private ImageView image;
	private EditText descriptionField, addressField, dateTimeField,
			endDateTimeField, regiontv, quantityField;
	private TextView expiredField;
	// private BroadcastReceiver initiate_redem_receiver;
	private int screenWidth, screenHeight;

	private WindowManager wmanager;
	private Display display;
	private String[] countryList;
	private String selected_country = "North";
	private Spinner countrySpinner;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mylistings_itempage);

		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;

		setupBar();
		// picture
		Bundle extras = getIntent().getExtras();
		image = (ImageView) findViewById(R.id.mylistings_itempage_image);
		ViewGroup.LayoutParams params11 = image.getLayoutParams();
		params11.height = (size.y) / 2;
		image.setLayoutParams(params11);

		descriptionField = (EditText) findViewById(R.id.mylistings_itempage_description);
		addressField = (EditText) findViewById(R.id.mylistings_itempage_address);
		dateTimeField = (EditText) findViewById(R.id.mylistings_itempage_dateTime);
		endDateTimeField = (EditText) findViewById(R.id.mylistings_itempage_enddateTime);
		regiontv = (EditText) findViewById(R.id.mylistings_itempage_region);
		expiredField = (TextView) findViewById(R.id.mylistings_itempage_expired);
		quantityField = (EditText) findViewById(R.id.mylistings_itempage_quantity);
		doneButton = (Button) findViewById(R.id.mylistings_itempage_donebutton);

		descriptionField.setFocusable(false);
		addressField.setFocusable(false);
		dateTimeField.setFocusable(false);
		endDateTimeField.setFocusable(false);
		regiontv.setFocusable(false);
		quantityField.setFocusable(false);

		description = extras.getString("description", "");
		address = extras.getString("address", "");
		date = extras.getString("date", "");
		time = extras.getString("time", "");
		productId = extras.getString("productId", "");
		redeem = extras.getString("redeem", "false");
		endDate = extras.getString("endDate", "");
		endTime = extras.getString("endTime", "");
		region = extras.getString("region", "");
		quantity = extras.getString("quantity", "");
		setupCountrySpinner();
		// check whether the item is expired
		String endDate1 = editEndDate(endDate);
		String todayDate = todayDate();
		if (checkExpired(endDate1, endTime, todayDate)) {
			// expiredField
			expiredField.setVisibility(View.VISIBLE);
		} else {
			// Not expiredField
			expiredField.setVisibility(View.INVISIBLE);
		}

		// check whether the item is out of stock
		if (quantity.equals("0")) {
			// out of stock
			quantityField.setText(quantity + "  (out of stock)");
			// doneButton.setVisibility(View.VISIBLE);
		} else {
			// the item is still available in stock
			quantityField.setText(quantity);
			quantityField.setTextColor(Color.BLACK);
			// doneButton.setVisibility(View.INVISIBLE);
		}
		doneButton.setVisibility(View.INVISIBLE);

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
		doneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// done editing

				descriptionField.setFocusable(false);
				addressField.setFocusable(false);
				dateTimeField.setFocusable(false);
				endDateTimeField.setFocusable(false);
				regiontv.setFocusable(false);
				quantityField.setFocusable(false);
				doneClicked();
				regiontv.setText(selected_country);
				doneButton.setVisibility(View.INVISIBLE);
			}
		});

		listing_update_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_listiongUpdate_result(result);
			}

		};

		IntentFilter receiverfilter1 = new IntentFilter(
				Static_view_info.UPDATE_LISTING_BROADCAST);
		MyListingsItemPageActivity.this.registerReceiver(
				listing_update_receiver, receiverfilter1);

		// initiate_redem_receiver = new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context context, Intent intent) {
		// int result = intent.getIntExtra("result", -1);
		// handle_sample_result(result);
		// }
		//
		// };
		// IntentFilter receiverfilter = new IntentFilter(
		// Static_view_info.REDEM_BROADCAST);
		// registerReceiver(initiate_redem_receiver, receiverfilter);
		
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
			unregisterReceiver(listing_update_receiver);
		}catch(Exception e){}
		super.onStop();
	}
	
	private void handle_sample_result(int result) {
		if (result == 1) {
			Toast.makeText(this, "The product is expiredField",
					Toast.LENGTH_SHORT).show();
			doneButton.setVisibility(View.INVISIBLE);
			Static_view_info.sampleStateChanged = true;
		} else {
			Toast.makeText(this, "Redeeming failed", Toast.LENGTH_SHORT).show();
		}
	}

	private void sampleClicked() {
		new RedemPage_Controller(new ProgressDialog(
				MyListingsItemPageActivity.this),
				MyListingsItemPageActivity.this, description,
				Static_view_info.userId + "").execute();
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
				InputStream is = new BufferedInputStream(new URL(image_url).openStream(), BUFFER_IO_SIZE);
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
		
	//	Intent in = new Intent(MyListingsItemPageActivity.this, HomePageActivity.class);
	//	startActivity(in);
		overridePendingTransition(R.anim.slide_right_show,
				R.anim.slide_right_hide);
		finish();
	}

	private void dialogBox(String title, String message) {
		int textSize = 16;
		TextView t = new TextView(MyListingsItemPageActivity.this);
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

	private String editEndDate(String endDate) {
		String[] array = endDate.split("-");
		String day = array[2];
		String month = array[1];
		String year = array[0];

		String newDate = day + "-" + month + "-" + year;
		return newDate;
	}

	public String todayDate() {
		Calendar cal = Calendar.getInstance();
		String month_display = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day_display = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String year_display = String.valueOf(cal.get(Calendar.YEAR));
		String hour_display = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		String min_display = String.valueOf(cal.get(Calendar.MINUTE));
		String today = day_display + "-" + month_display + "-" + year_display
				+ "-" + hour_display + "-" + min_display;
		return today;
	}

	private boolean checkExpired(String endDate, String endTime, String today) {
		String[] endDate1 = endDate.split("-");
		String[] endTime1 = endTime.split(":");
		int endDay = Integer.parseInt(endDate1[0]);
		int endMonth = Integer.parseInt(endDate1[1]);
		int endYear = Integer.parseInt(endDate1[2]);
		int endHour = Integer.parseInt(endTime1[0]);
		int endMin = Integer.parseInt(endTime1[1]);

		String[] today1 = today.split("-");
		int todayDay = Integer.parseInt(today1[0]);
		int todayMonth = Integer.parseInt(today1[1]);
		int todayYear = Integer.parseInt(today1[2]);
		int todayHour = Integer.parseInt(today1[3]);
		int todayMin = Integer.parseInt(today1[4]);

		if (endYear > todayYear)
			return false;
		else if (endYear == todayYear) {
			if (endMonth > todayMonth)
				return false;
			else if (endMonth == todayMonth) {
				if (endDay > todayDay)
					return false;
				else if (endDay == todayDay) {
					if (endHour > todayHour)
						return false;
					else if (endHour == todayHour) {
						if (endMin > todayMin)
							return false;
						else
							return true;
					} else {
						return true;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			// expiredField
			return true;
		}

	}

	private void setupBar() {
		tabBarLayout = (RelativeLayout) findViewById(R.id.mylistings_itempage_tabbar);
		editButton = (ImageButton) findViewById(R.id.mylistings_itempage_edit_button);
		removeButton = (ImageButton) findViewById(R.id.mylistings_itempage_remove_button);
		doneButton = (Button) findViewById(R.id.mylistings_itempage_donebutton);

		ViewGroup.LayoutParams params1 = tabBarLayout.getLayoutParams();
		params1.height = screenHeight / 12;
		tabBarLayout.setLayoutParams(params1);

		ViewGroup.LayoutParams params3 = removeButton.getLayoutParams();
		params3.width = screenWidth / 7;
		params3.height = screenHeight / 14;
		removeButton.setLayoutParams(params3);

		ViewGroup.LayoutParams params4 = editButton.getLayoutParams();
		params4.width = screenWidth / 8;
		params4.height = screenHeight / 14;
		editButton.setLayoutParams(params4);

		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				descriptionField.setFocusableInTouchMode(true);
				descriptionField.requestFocus();
				addressField.setFocusableInTouchMode(true);
				dateTimeField.setFocusableInTouchMode(true);
				regiontv.setFocusableInTouchMode(true);
				endDateTimeField.setFocusableInTouchMode(true);
				quantityField.setFocusableInTouchMode(true);
				pressed = 0;
				
				// descriptionField.setFocusable(true);
				// descriptionField.requestFocus();
				// addressField.setFocusable(true);
				// dateTimeField.setFocusable(true);
				// endDateTimeField.setFocusable(true);
				// regiontv.setFocusable(true);
				// quantityField.setFocusable(true);

				doneButton.setVisibility(View.VISIBLE);
				countrySpinner.setVisibility(View.VISIBLE);
				regiontv.setVisibility(View.GONE);
			}
		});

		removeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pressed = 1;
				doneButton.setVisibility(View.VISIBLE);
				doneClicked();
			}
		});

	}

	private void doneClicked() {
		if (pressed == 0) {// Edit pressed
			String descrp = descriptionField.getText().toString();
			String address = addressField.getText().toString();
			String dateTime = dateTimeField.getText().toString();
			String endDate = endDateTimeField.getText().toString();
			String region = regiontv.getText().toString();
			String quantity = quantityField.getText().toString();
			String start[] = { "", "" };
			String end[] = { "", "" };
			start = dateTime.split(" ");
			end = endDate.split(" ");

			if (descrp.equals("") || address.equals("") || dateTime.equals("")
					|| region.equals("") || endDate.equals("")
					|| quantity.equals("")) {
				// Toast
				Toast.makeText(MyListingsItemPageActivity.this,
						"please fill all Fields", Toast.LENGTH_SHORT).show();

			} else {
				// To PHP UPDATE Query
				new UpdateListing_Controller(new ProgressDialog(
						MyListingsItemPageActivity.this),
						MyListingsItemPageActivity.this, productId, descrp,
						address, start[0], start[1], selected_country, end[0],
						end[1], quantity).execute();
			}
		} else if (pressed == 1) {// delete pressed
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Confirm");
			builder.setMessage("Are you sure you want to delete this product?");

			builder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Do Delete
							new UpdateListing_Controller(new ProgressDialog(
									MyListingsItemPageActivity.this),
									MyListingsItemPageActivity.this, productId,
									"1", "1", "1", "1", "1", "1", "1", "1")
									.execute();
							dialog.dismiss();
						}

					});

			builder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
							dialog.dismiss();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();

		}

	}

	private void handle_listiongUpdate_result(int result) {
		if (result == 1) {
			if (pressed == 0) {
				// update successful
				Toast.makeText(MyListingsItemPageActivity.this,
						"profile updated Successfully", Toast.LENGTH_SHORT)
						.show();
				// to disable
				descriptionField.setFocusable(false);
				addressField.setFocusable(false);
				regiontv.setFocusable(false);
				endDateTimeField.setFocusable(false);
				quantityField.setFocusable(false);
				dateTimeField.setFocusable(false);
			} else {
				// update successful
				Toast.makeText(MyListingsItemPageActivity.this,
						"product deleted Successfully", Toast.LENGTH_SHORT)
						.show();
				Static_view_info.sampleStateChanged = true;
				Static_view_info.sampleStateChanged_listings = true;
				finish();
			}

			doneButton.setVisibility(View.GONE);
			countrySpinner.setVisibility(View.GONE);
			regiontv.setVisibility(View.VISIBLE);
		}
	}

	private void setupCountrySpinner() {
		countryList = new String[] { "North", "South", "East", "West", "Center" };

		countrySpinner = (Spinner) findViewById(R.id.mylistings_itempage_spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MyListingsItemPageActivity.this,
				android.R.layout.simple_spinner_item, countryList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(adapter);
		countrySpinner.pointToPosition(50, 50);
		countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_country = countryList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

}