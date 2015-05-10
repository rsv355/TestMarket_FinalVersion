package com.example.testmarket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import com.example.testmarket.R;

import controller.GetUserInfo_Controller;
import controller.Signup_Controller;
import controller.UpdateProfile_Controller;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MyProfilePanel implements HomePagePanelInterface {

	private BroadcastReceiver profile_receiver, profile_update_receiver;
	private String name, location, email, contact, birthday;
	private EditText emailTV;
	private EditText nameTV, locationTV, contactTV, genderTV, birthdayTV;
	private Button editButton, doneButton;
	private RelativeLayout outerLayout;
	private LinearLayout layout;
	private ImageView profPic;
	private Spinner countrySpinner, genderSpinner;
	private WindowManager wmanager;
	private Display display;
	private int imageWidth, imageHeight;
	private GetUserInfo_Controller thread;

	private String upLoadServerUri = "http://Adiuva.sg/dev/test_market/UploadToServerProfile.php";
	private int serverResponseCode = 0;

	private Activity parentActivity;
	private Context context;
	private ProgressBar progressBar;

	private boolean edit;
	private boolean picClicked = false;
	private String imagePath;
	private double imageSize;
	File fileTemp;
	private String selected_country = "North", selected_gender = "Female";

	private String selected_day = "01", selected_month = "01",
			selected_year = "1992";
	private String[] dayList, monthList, yearList, countryList,genderList;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(final Context context, final Activity parentActivity) {

		this.parentActivity = parentActivity;
		this.context = context;
		nameTV = (EditText) parentActivity.findViewById(R.id.myprofile_name_1);
		locationTV = (EditText) parentActivity
				.findViewById(R.id.myprofile_region1);
		editButton = (Button) parentActivity.findViewById(R.id.refresh_button);
		doneButton = (Button) parentActivity.findViewById(R.id.done);
	//	logout_button = (Button) parentActivity.findViewById(R.id.logout_button);
		
		emailTV = (EditText) parentActivity.findViewById(R.id.myprofile_email1);
		contactTV = (EditText) parentActivity
				.findViewById(R.id.myprofile_contact1);
		genderTV = (EditText) parentActivity
				.findViewById(R.id.myprofile_gender1);
		birthdayTV = (EditText) parentActivity
				.findViewById(R.id.myprofile_birthdate1);
		profPic = (ImageView) parentActivity.findViewById(R.id.pp);
		outerLayout = (RelativeLayout) parentActivity
				.findViewById(R.id.myprofile_outerlayout);
		layout = (LinearLayout) parentActivity
				.findViewById(R.id.myprofile_layout);
		progressBar = (ProgressBar) parentActivity
				.findViewById(R.id.myprofiletap_progressBar1);

		birthdayTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (edit) {
					// custom dialog
					final Dialog dialog = new Dialog(parentActivity);
					dialog.setContentView(R.layout.custom);
					dialog.setTitle("Select Birthdate");

					Button okButton = (Button) dialog
							.findViewById(R.id.dialog_button1);
					Spinner birthdaySpinner = (Spinner) dialog
							.findViewById(R.id.dialog_daylist);
					Spinner birthmonthSpinner = (Spinner) dialog
							.findViewById(R.id.dialog_monthllist);
					Spinner birthyearSpinner = (Spinner) dialog
							.findViewById(R.id.dialog_yearlist);

					setupDaySpinner(birthdaySpinner);
					setupMonthSpinner(birthmonthSpinner);
					setupYearSpinner(birthyearSpinner);
					// if button is clicked, close the custom dialog
					okButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							birthday = selected_year + "-" + selected_month
									+ "-" + selected_day;
							selected_day = "01";
							selected_month = "01";
							selected_year = "1992";
							birthdayTV.setText(birthday);
							dialog.dismiss();
						}
					});

					Button cancelButton = (Button) dialog
							.findViewById(R.id.dialog_button2);
					// if button is clicked, close the custom dialog
					cancelButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							selected_day = "01";
							selected_month = "01";
							selected_year = "1992";

							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		});

		// set false of editTexts
		emailTV.setFocusable(false);
		contactTV.setFocusable(false);
		genderTV.setFocusable(false);
		birthdayTV.setFocusable(false);
		birthdayTV.setClickable(false);
		nameTV.setFocusable(false);
		locationTV.setFocusable(false);
		edit = false;

		// set dimensions
		wmanager = (WindowManager) parentActivity
				.getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		imageWidth = (int) (size.x);
		imageHeight = (int) (size.y);

		final LinearLayout layout3 = (LinearLayout) parentActivity
				.findViewById(R.id.myprofile_layout3);
		ViewGroup.LayoutParams params0 = layout3.getLayoutParams();
		// size
		params0.width = (imageWidth / 2);
		params0.height = imageHeight / 3;
		layout3.setLayoutParams(params0);

		ViewGroup.LayoutParams params1 = profPic.getLayoutParams();
		params1.width = (imageWidth / 2);
		params1.height = imageHeight / 4;
		profPic.setLayoutParams(params1);

		final ImageView desicon = (ImageView) parentActivity
				.findViewById(R.id.myprofile_emailicon);
		ViewGroup.LayoutParams params4 = desicon.getLayoutParams();
		// size
		params4.width = Static_view_info.screenWidth / 10;
		desicon.setLayoutParams(params4);

		final ImageView regionIcon = (ImageView) parentActivity
				.findViewById(R.id.myprofile_regionicon1);
		ViewGroup.LayoutParams params5 = regionIcon.getLayoutParams();
		// size
		params5.width = Static_view_info.screenWidth / 10;
		regionIcon.setLayoutParams(params5);

		final ImageView birthdateIcon = (ImageView) parentActivity
				.findViewById(R.id.myprofile_birthdateicon);
		ViewGroup.LayoutParams params6 = birthdateIcon.getLayoutParams();
		// size
		params6.width = Static_view_info.screenWidth / 10;
		birthdateIcon.setLayoutParams(params6);

		final ImageView contactIcon = (ImageView) parentActivity
				.findViewById(R.id.myprofile_contacticon);
		ViewGroup.LayoutParams params7 = contactIcon.getLayoutParams();
		// size
		params7.width = Static_view_info.screenWidth / 10;
		contactIcon.setLayoutParams(params7);

		final ImageView genderIcon = (ImageView) parentActivity
				.findViewById(R.id.myprofile_gendericon);
		ViewGroup.LayoutParams params8 = genderIcon.getLayoutParams();
		// size
		params8.width = Static_view_info.screenWidth / 10;
		genderIcon.setLayoutParams(params8);

		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				setupCountrySpinner();
				setupGenderSpinner();
				editClicked();

			}
		});

		profPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				profPicClicked();

			}
		});

		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				doneClicked();

			}
		});

		profile_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String[] result = intent.getStringArrayExtra("result");
				handle_profile_result(result);
			}

		};

		try{
				IntentFilter receiverfilter = new IntentFilter(
						Static_view_info.PROFILE_BROADCAST);
				context.registerReceiver(profile_receiver, receiverfilter);
			
			
		}catch(Exception e){}
		
		

		profile_update_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_profileUpdate_result(result);
			}

		};

		try{
				IntentFilter receiverfilter1 = new IntentFilter(
						Static_view_info.UPDATE_PROFILE_BROADCAST);
				context.registerReceiver(profile_update_receiver, receiverfilter1);
						
			
		}catch(Exception e){}
		
		
		
/*		logout_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Global_access ga = new Global_access();
				
				ga.savePreferences(ga.TAG_LOGIN, false, context);
				ga.removePreferences(ga.TAG_LOGIN_TYPE, context);
				ga.removePreferences(ga.TAG_EMAIL, context);
				ga.removePreferences(ga.TAG_USER_ID, context);
				
				
				Intent in = new Intent(context.getApplicationContext(), SignInActivity.class);
				context.startActivity(in);
				parentActivity.finish();
			}
		});*/
	}
	
	private void setupDaySpinner(Spinner birthdaySpinner) {

		dayList = new String[31];
		int counter = 0;
		for (int i = 1; i <= 9; i++) {
			dayList[counter] = "0" + i;
			counter++;
		}
		for (int i = 10; i <= 31; i++) {
			dayList[counter] = "" + i;
			counter++;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentActivity,
				android.R.layout.simple_spinner_item, dayList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthdaySpinner.setAdapter(adapter);
		birthdaySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_day = dayList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void setupMonthSpinner(Spinner birthmonthSpinner) {
		monthList = new String[12];
		int counter = 0;
		for (int i = 1; i <= 9; i++) {
			monthList[counter] = "0" + i;
			counter++;
		}
		for (int i = 10; i <= 12; i++) {
			monthList[counter] = "" + i;
			counter++;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentActivity,
				android.R.layout.simple_spinner_item, monthList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthmonthSpinner.setAdapter(adapter);
		birthmonthSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						selected_month = monthList[position];
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	private void setupYearSpinner(Spinner birthyearSpinner) {
		yearList = new String[71];
		int counter = 0;

		for (int i = 2020; i >= 1950; i--) {
			yearList[counter] = "" + i;
			counter++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentActivity,
				android.R.layout.simple_spinner_item, yearList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthyearSpinner.setAdapter(adapter);
		birthyearSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						selected_year = yearList[position];
						// switch (position) {
						// case 0:
						// Log.d("ghazy", "selected year : "
						// + yearList[position]);
						// break;
						// case 1:
						// Log.d("ghazy", "selected year : "
						// + yearList[position]);
						// break;
						// }

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	@Override
	public void hide() {
		if (thread != null) {
			thread.cancel(true);
			thread = null;
		}
		outerLayout.setVisibility(View.GONE);

	}

	@Override
	public void show() {
		layout.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		outerLayout.setVisibility(View.VISIBLE);
		thread = new GetUserInfo_Controller(parentActivity,
				Static_view_info.userId + "");
		thread.execute();

	}

	private void handle_profile_result(String[] result) {
		// {name,location,email,contact,gender,birthday}
	
		try{
			name = result[0];
			location = result[1];
			email = result[2];
			contact = result[3];
			selected_gender = result[4];
			birthday = result[5];

			nameTV.setText(name);
			locationTV.setText(location);
			emailTV.setText(email);
			contactTV.setText(contact);
			genderTV.setText(selected_gender);
			birthdayTV.setText(birthday);
			profPic.setBackgroundDrawable(Static_view_info.profilePic);
			layout.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}catch(Exception e){}
		

	}

	private void editClicked() {
		edit = true;
		picClicked = false;
		doneButton.setVisibility(View.VISIBLE);
		countrySpinner.setVisibility(View.VISIBLE);
		genderSpinner.setVisibility(View.VISIBLE);
		emailTV.setFocusableInTouchMode(true);
		emailTV.requestFocus();
		contactTV.setFocusableInTouchMode(true);
		locationTV.setVisibility(View.GONE);
		genderTV.setVisibility(View.GONE);
		nameTV.setFocusableInTouchMode(true);
		// birthdayTV.setFocusableInTouchMode(true);
		birthdayTV.setClickable(true);
		//genderTV.setFocusableInTouchMode(true);

	}

	private void profPicClicked() {
		if (edit) {
			picClicked = true;
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra("return-data", true);
			parentActivity.startActivityForResult(
					Intent.createChooser(intent, "Complete action using"), 6);
		} else {
			picClicked = false;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri selectedImageUri = data.getData();
		imagePath = getPath(selectedImageUri);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
		File sourceFile = new File(imagePath);
		imageSize = ((sourceFile.length()) / 1024.0) / 1024.0;

		if (imageSize > 3) {

			BitmapFactory.Options bounds = new BitmapFactory.Options();
			bounds.inSampleSize = 2;
			Bitmap imgBitmap = BitmapFactory.decodeFile(imagePath, bounds);

			doneButton.setClickable(true);
			Drawable d = new BitmapDrawable(parentActivity.getResources(),
					imgBitmap);
			profPic.setBackgroundDrawable(d);

			try {
				String path = Environment.getExternalStorageDirectory()
						.toString();
				OutputStream fOut = null;
				fileTemp = new File(path, "Temp" + ".jpg"); // the File to save
															// to
				imagePath = path + "/Temp.jpg";
				fOut = new FileOutputStream(fileTemp);
				imgBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving
																			// the
																			// Bitmap
																			// to
																			// a
																			// file
																			// compressed
																			// as
																			// a
																			// JPEG
																			// with
																			// 85%
																			// compression
																			// rate
				fOut.flush();
				fOut.close(); // do not forget to close the stream
				MediaStore.Images.Media.insertImage(
						parentActivity.getContentResolver(),
						fileTemp.getAbsolutePath(), fileTemp.getName(),
						fileTemp.getName());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			doneButton.setClickable(true);
			Drawable d = new BitmapDrawable(parentActivity.getResources(),
					bitmap);
			profPic.setBackgroundDrawable(d);
		}
	}

	private String getPath(Uri uri) {
		System.out.println("called getPath");
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = parentActivity.managedQuery(uri, projection, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private int uploadFile(String sourceFileUri) {
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 3 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {

			Log.e("uploadFile", "Source File not exist :" + imagePath);
			return 0;

		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ Static_view_info.userId + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					parentActivity.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "File Upload Complete.",
									Toast.LENGTH_SHORT).show();

							Static_view_info.sampleStateChanged_listings = true;
						}
					});
					if (fileTemp != null) {
						fileTemp.delete();
					}
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				ex.printStackTrace();

				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context, "MalformedURLException",
								Toast.LENGTH_SHORT).show();
					}
				});

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				e.printStackTrace();

				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context, "Got Exception : see logcat ",
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			return serverResponseCode;

		}
	}

	private void doneClicked() {
		edit = false;
		// get text
		String gender = genderTV.getText().toString();
		String birthday = birthdayTV.getText().toString();
		String name = nameTV.getText().toString();
		String location = locationTV.getText().toString();
		String contact = contactTV.getText().toString();
		String email = emailTV.getText().toString();

		if (gender.equals("") || birthday.equals("") || name.equals("")
				|| location.equals("") || contact.equals("")
				|| email.equals("") || contact.length() != 11) {
			if (contact.length() != 11) {
				Toast.makeText(parentActivity,
						"Error in contact format. Example: +65xxxxxxxx",
						Toast.LENGTH_SHORT).show();
			} else {
				// Toast
				Toast.makeText(parentActivity, "please fill all Fields",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			// To PHP UPDATE Query
			new UpdateProfile_Controller(new ProgressDialog(parentActivity),
					context, Static_view_info.userId + "", email, "", name,
					contact, selected_country, birthday, selected_gender).execute();
			// upload pic
			if (picClicked) {
				String root = Environment.getExternalStorageDirectory()
						.toString();
				File myDir = new File(root + "/Test market_place" + "/profile"
						+ "/" + Static_view_info.userId);
				boolean deleted = myDir.delete();
				new Thread(new Runnable() {
					public void run() {
						uploadFile(imagePath);
					}
				}).start();

			}
		}

	}

	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();

		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 0) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}

	private void handle_profileUpdate_result(int result) {
		if (result == 1) {
			// update successful
			Toast.makeText(parentActivity, "profile updated Successfully",
					Toast.LENGTH_SHORT).show();
			// to disable
			
			countrySpinner = (Spinner) parentActivity.findViewById(R.id.spinner1);
			genderSpinner = (Spinner) parentActivity.findViewById(R.id.spinner2);
			
			emailTV.setFocusable(false);
			contactTV.setFocusable(false);
			locationTV.setFocusable(false);
			nameTV.setFocusable(false);
			birthdayTV.setFocusable(false);
			birthdayTV.setClickable(false);
			genderTV.setFocusable(false);
			doneButton.setVisibility(View.GONE);
			countrySpinner.setVisibility(View.GONE);
			genderSpinner.setVisibility(View.GONE);
			locationTV.setText(selected_country);
			locationTV.setVisibility(View.VISIBLE);
			genderTV.setVisibility(View.VISIBLE);
			genderTV.setText(selected_gender);
		}
	}

	private void setupCountrySpinner() {
		countryList = new String[] { "North", "South", "East", "West", "Center" };

		countrySpinner = (Spinner) parentActivity.findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentActivity,
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

	private void setupGenderSpinner() {
		genderList = new String[] { "Male", "Female" };

		genderSpinner = (Spinner) parentActivity.findViewById(R.id.spinner2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentActivity,
				android.R.layout.simple_spinner_item, genderList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(adapter);
		genderSpinner.pointToPosition(50, 50);
		genderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_gender = genderList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	public void onCreateDialog() {
		// switch (id) {
		// case DATE_DIALOG_ID:
		// // set date picker as current date
		// return new DatePickerDialog(parentActivity, datePickerListener,
		// year, month, day);
		// }
		// return null;

		AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);

		alert.setTitle("Email Verification");
		alert.setMessage("Check Your Email... type the verification code: ");

		// Set an EditText view to get user input
		final DatePicker input = new DatePicker(parentActivity);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.

					}
				});
		alert.show();

	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
