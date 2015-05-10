package com.example.testmarket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.example.testmarket.R;

import controller.Upload_Controller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class UploadPanel implements HomePagePanelInterface {

	private WindowManager wmanager;
	private Display display;
	private int imageWidth, imageHeight;

	private RelativeLayout outerLayout;
	private Button uploadGallaryButton, postButton, uploadURLButton;
	private Spinner countrySpinner;
	String selected_country = "North";
	private String[] countryList;
	private LinearLayout urlContainer, imgContanier;
	private ImageView imgtoupload, cameraPic;
	private EditText descriptionText, addressText, quantityText;
	private String upLoadServerUri = null;
	private String imagepath = null;
	private int serverResponseCode = 0;
	private BroadcastReceiver upload_receiver = null;
	private String productId, imageway = "false";
	private double imageSize = 0.0;
	private String filePath;
	public int TAKE_PICTURE = 2;
	private File fileTemp;

	// For time picker

	private Button month_plus;
	private EditText month_display;
	private Button month_minus;
	private Button date_plus;
	private EditText date_display;
	private Button date_minus;
	private Button year_plus;
	private EditText year_display;
	private Button year_minus;
	private Button hour_plus;
	private EditText hour_display;
	private Button hour_minus;
	private Button min_plus;
	private EditText min_display;
	private Button min_minus;

	private DateHandler dateHandler;

	// end date
	private Button month_plus1;
	private EditText month_display1;
	private Button month_minus1;
	private Button date_plus1;
	private EditText date_display1;
	private Button date_minus1;
	private Button year_plus1;
	private EditText year_display1;
	private Button year_minus1;
	private Button hour_plus1;
	private EditText hour_display1;
	private Button hour_minus1;
	private Button min_plus1;
	private EditText min_display1;
	private Button min_minus1;

	private DateHandler enddateHandler;

	private Context context;
	private Activity parentActivity;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(final Context context, final Activity parentActivity) {
		this.context = context;
		this.parentActivity = parentActivity;
		wmanager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		imageWidth = (int) (size.x / 1.5);
		imageHeight = imageWidth;

		setupCountrySpinner();
		outerLayout = (RelativeLayout) parentActivity
				.findViewById(R.id.uploadtab_outerlayout);
		uploadGallaryButton = (Button) parentActivity
				.findViewById(R.id.upload_gallary);
		postButton = (Button) parentActivity.findViewById(R.id.post);
		uploadURLButton = (Button) parentActivity.findViewById(R.id.uploadURL);
		urlContainer = (LinearLayout) parentActivity
				.findViewById(R.id.URLContainer);
		imgContanier = (LinearLayout) parentActivity
				.findViewById(R.id.ImageContainer);
		imgtoupload = (ImageView) parentActivity
				.findViewById(R.id.uploadthispic);
		cameraPic = (ImageView) parentActivity.findViewById(R.id.camerapic);
		descriptionText = (EditText) parentActivity
				.findViewById(R.id.description);
		addressText = (EditText) parentActivity.findViewById(R.id.address);
		addressText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					InputMethodManager inputManager = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(parentActivity
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					textView.clearFocus();
					countrySpinner.requestFocus();
					countrySpinner.performClick();
				}
				return true;
			}
		});
		quantityText = (EditText) parentActivity.findViewById(R.id.quantity);
		upLoadServerUri = "http://Adiuva.sg/dev/test_market/UploadToServer.php";
		initializeReference();

		imgtoupload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPicInGallery();

			}
		});
		// to do
		cameraPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPicInGallery();

			}
		});

		uploadGallaryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				imageway = "false";
				uploadGallaryClicked();

			}
		});

		uploadURLButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dispatchTakePictureIntent();
				imageway = "true";
				imgContanier.setVisibility(8);
				urlContainer.setVisibility(0);

			}
		});

		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				postClicked();

			}
		});
		upload_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int productId = intent.getIntExtra("productId", -1);
				handle_signIn_result(productId);
			}

		};
		
		try{
				IntentFilter receiverfilter = new IntentFilter(
						Static_view_info.Uploaded_BROADCAST);
				context.registerReceiver(upload_receiver, receiverfilter);
			
			
		}catch(Exception e){}
		
		
	}

	private void setupCountrySpinner() {
		countryList = new String[] { "North", "South", "East", "West", "Center" };

		countrySpinner = (Spinner) parentActivity
				.findViewById(R.id.uploadtap_region);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
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

	private void initializeReference() {

		month_plus = (Button) parentActivity.findViewById(R.id.month_plus);
		month_display = (EditText) parentActivity
				.findViewById(R.id.month_display);
		month_minus = (Button) parentActivity.findViewById(R.id.month_minus);

		date_plus = (Button) parentActivity.findViewById(R.id.date_plus);
		date_display = (EditText) parentActivity
				.findViewById(R.id.date_display);
		date_minus = (Button) parentActivity.findViewById(R.id.date_minus);

		year_plus = (Button) parentActivity.findViewById(R.id.year_plus);
		year_display = (EditText) parentActivity
				.findViewById(R.id.year_display);
		year_minus = (Button) parentActivity.findViewById(R.id.year_minus);

		hour_plus = (Button) parentActivity.findViewById(R.id.hour_plus);
		hour_display = (EditText) parentActivity
				.findViewById(R.id.hour_display);
		hour_minus = (Button) parentActivity.findViewById(R.id.hour_minus);

		min_plus = (Button) parentActivity.findViewById(R.id.min_plus);
		min_display = (EditText) parentActivity.findViewById(R.id.min_display);
		min_minus = (Button) parentActivity.findViewById(R.id.min_minus);

		dateHandler = new DateHandler(month_plus, month_display, month_minus,
				date_plus, date_display, date_minus, year_plus, year_display,
				year_minus, hour_plus, hour_display, hour_minus, min_plus,
				min_display, min_minus);

		// end date
		month_plus1 = (Button) parentActivity.findViewById(R.id.endmonth_plus);
		month_display1 = (EditText) parentActivity
				.findViewById(R.id.endmonth_display);
		month_minus1 = (Button) parentActivity
				.findViewById(R.id.endmonth_minus);

		date_plus1 = (Button) parentActivity.findViewById(R.id.enddate_plus);
		date_display1 = (EditText) parentActivity
				.findViewById(R.id.enddate_display);
		date_minus1 = (Button) parentActivity.findViewById(R.id.enddate_minus);

		year_plus1 = (Button) parentActivity.findViewById(R.id.endyear_plus);
		year_display1 = (EditText) parentActivity
				.findViewById(R.id.endyear_display);
		year_minus1 = (Button) parentActivity.findViewById(R.id.endyear_minus);

		hour_plus1 = (Button) parentActivity.findViewById(R.id.endhour_plus);
		hour_display1 = (EditText) parentActivity
				.findViewById(R.id.endhour_display);
		hour_minus1 = (Button) parentActivity.findViewById(R.id.endhour_minus);

		min_plus1 = (Button) parentActivity.findViewById(R.id.endmin_plus);
		min_display1 = (EditText) parentActivity
				.findViewById(R.id.endmin_display);
		min_minus1 = (Button) parentActivity.findViewById(R.id.endmin_minus);

		enddateHandler = new DateHandler(month_plus1, month_display1,
				month_minus1, date_plus1, date_display1, date_minus1,
				year_plus1, year_display1, year_minus1, hour_plus1,
				hour_display1, hour_minus1, min_plus1, min_display1, min_minus1);
	}

	private void uploadGallaryClicked() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		parentActivity.startActivityForResult(
				Intent.createChooser(intent, "Complete action using"), 1);
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
			parentActivity.startActivityForResult(takePictureIntent,
					TAKE_PICTURE);
		}
	}

	private void postClicked() {
		
		String description = descriptionText.getText().toString();
		String date = dateHandler.getYear() + "-" + dateHandler.getMonth()
				+ "-" + dateHandler.getDay();
		String time = dateHandler.getHour() + ":" + dateHandler.getMinute();
		System.out.println("Date-----> " + date);
		System.out.println("Time---->" + time);
		String enddate = enddateHandler.getYear() + "-"
				+ enddateHandler.getMonth() + "-" + enddateHandler.getDay();
		String endtime = enddateHandler.getHour() + ":"
				+ enddateHandler.getMinute();

		String address = addressText.getText().toString();
		String quantity = quantityText.getText().toString();

		Calendar cal_start = Calendar.getInstance();
		cal_start.set(dateHandler.getYear(), dateHandler.getMonth(), 
				dateHandler.getDay(), dateHandler.getHour(), dateHandler.getMinute(), 00);
		
		Calendar cal_end = Calendar.getInstance();
		cal_end.set(enddateHandler.getYear(), enddateHandler.getMonth(), 
				enddateHandler.getDay(), enddateHandler.getHour(), enddateHandler.getMinute(), 00);
		
		
		
		if (!description.equals("") && !date.equals("") && !time.equals("")
				&& !address.equals("") && !quantity.equals("")
				&& (imageSize < 3 && imageSize != 0.0) && cal_end.compareTo(cal_start) > 0) {
			new Upload_Controller(new ProgressDialog(context), context,
					description, quantity, address, date, time, enddate,
					endtime, selected_country, Static_view_info.userId + "",
					imageway).execute();
			imageSize = 0.0;

		} else if (imageSize >= 3) {
			Toast.makeText(context, "The image is too large",
					Toast.LENGTH_SHORT).show();
		} else if(cal_end.compareTo(cal_start) <= 0){
			Toast.makeText(context, "enter the end date after the collection date", Toast.LENGTH_SHORT)
			.show();
		} else if (imageSize == 0.0) {
			Toast.makeText(context, "select an image", Toast.LENGTH_SHORT)
					.show();
		} else {
			// Blank
			Toast.makeText(context, "Please, fill all fields",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void handle_signIn_result(int result) {

		// if (result == -2)
		// Toast.makeText(this, "there is no ID", Toast.LENGTH_SHORT).show();

		if (result == -1)
			Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();

		else {
			if (imageway.equals("false")) {
				productId = String.valueOf(result);// without .jpeg
				Toast.makeText(context, "please wait until uploading complete",
						Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {
					public void run() {
						uploadFile(imagepath);

					}
				}).start();

			} else {
				productId = String.valueOf(result);// without .jpeg
				Toast.makeText(context, "please wait until uploading complete",
						Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {
					public void run() {
						uploadFile(filePath);
					}
				}).start();

			}
		}
	}

	private int uploadFile(String sourceFileUri) {
		if (imageSize > 3) {
			return -1;
		} else {

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

				Log.e("uploadFile", "Source File not exist :" + imagepath);
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
							+ productId + "\"" + lineEnd);

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
								Toast.makeText(context,
										"File Upload Complete.",
										Toast.LENGTH_SHORT).show();
								clear();
								Static_view_info.sampleStateChanged_listings = true;
								if (fileTemp != null)
									fileTemp.delete();
							}
						});
						// close the streams //
						fileInputStream.close();
						dos.flush();
						dos.close();
						Intent intent = new Intent(parentActivity,
								HomePageActivity.class);
						parentActivity.startActivity(intent);
						parentActivity.finish();
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

					Log.e("Upload file to server", "error: " + ex.getMessage(),
							ex);
				} catch (Exception e) {
					e.printStackTrace();

					parentActivity.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context,
									"Got Exception : see logcat ",
									Toast.LENGTH_SHORT).show();
						}
					});
					Log.e("Upload file to server Exception",
							"Exception : " + e.getMessage(), e);
				}
				return serverResponseCode;

			} // End else block
		}
	}

	@SuppressLint("NewApi")
	private void clear() {
		descriptionText.setText("");
		addressText.setText("");
		quantityText.setText("");
		imgtoupload.setBackground(null);
		cameraPic.setBackground(null);
		urlContainer.setVisibility(8);
		imgContanier.setVisibility(8);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == 1 || requestCode == 4)
				&& resultCode == parentActivity.RESULT_OK) {
			Uri selectedImageUri = data.getData();
			imagepath = getPath(selectedImageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(imagepath, options);
			urlContainer.setVisibility(8);
			imgContanier.setVisibility(0);
			File sourceFile = new File(imagepath);
			imageSize = ((sourceFile.length()) / 1024.0) / 1024.0;
			if (imageSize > 3) {

				BitmapFactory.Options bounds = new BitmapFactory.Options();
				bounds.inSampleSize = 2;
				Bitmap imgBitmap = BitmapFactory.decodeFile(imagepath, bounds);

				postButton.setClickable(true);
				Drawable d = new BitmapDrawable(parentActivity.getResources(),
						imgBitmap);
				imgtoupload.setImageBitmap(imgBitmap);

				try {
					String path = Environment.getExternalStorageDirectory()
							.toString();
					OutputStream fOut = null;
					fileTemp = new File(path, "Temp" + ".jpg"); // the File to
																// save to
					imagepath = path + "/Temp.jpg";
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
				imageSize = ((fileTemp.length()) / 1024.0) / 1024.0;

			} else {
				postButton.setClickable(true);
				imgtoupload.setImageBitmap(bitmap);
				ViewGroup.LayoutParams params11 = imgtoupload.getLayoutParams();
				params11.width = imageWidth;
				params11.height = imageHeight;
				imgtoupload.setLayoutParams(params11);
			}

		}
		if (requestCode == 5 && resultCode == parentActivity.RESULT_OK) {
			Uri selectedImageUri = data.getData();
			filePath = getPath(selectedImageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			urlContainer.setVisibility(0);
			imgContanier.setVisibility(8);
			File sourceFile = new File(filePath);
			imageSize = ((sourceFile.length()) / 1024.0) / 1024.0;
			if (imageSize > 3) {

				BitmapFactory.Options bounds = new BitmapFactory.Options();
				bounds.inSampleSize = 2;
				Bitmap imgBitmap = BitmapFactory.decodeFile(filePath, bounds);

				postButton.setClickable(true);
				Drawable d = new BitmapDrawable(parentActivity.getResources(),
						imgBitmap);
				cameraPic.setImageBitmap(imgBitmap);

				try {
					String path = Environment.getExternalStorageDirectory()
							.toString();
					OutputStream fOut = null;
					fileTemp = new File(path, "Temp" + ".jpg"); // the File to
																// save to
					filePath = path + "/Temp.jpg";
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
				imageSize = ((fileTemp.length()) / 1024.0) / 1024.0;

			} else {
				postButton.setClickable(true);
				cameraPic.setImageBitmap(bitmap);
				ViewGroup.LayoutParams params11 = cameraPic.getLayoutParams();
				params11.width = imageWidth;
				params11.height = imageHeight;
				cameraPic.setLayoutParams(params11);
			}

		}
		if ((requestCode == TAKE_PICTURE)
				&& resultCode == parentActivity.RESULT_OK) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			// options.inPurgeable = true;

			// Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			urlContainer.setVisibility(0);
			imgContanier.setVisibility(8);
			// cameraPic.setImageBitmap(bitmap);
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");

			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/Test market_place");
			myDir.mkdirs();
			String fname = "cam.jpg";
			filePath = root + "/Test market_place/cam.jpg";
			File file = new File(myDir, fname);
			if (file.exists())
				file.delete();
			try {
				FileOutputStream out = new FileOutputStream(file);
				imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
				imageSize = ((file.length()) / 1024.0) / 1024.0;

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (imageSize > 3) {
				postButton.setClickable(false);
				Toast.makeText(context,
						"Image size exceeded 3 M.B, Re-select image",
						Toast.LENGTH_SHORT).show();

			} else {
				cameraPic.setImageBitmap(imageBitmap);
				ViewGroup.LayoutParams params11 = cameraPic.getLayoutParams();
				params11.width = imageWidth;
				params11.height = imageHeight;
				cameraPic.setLayoutParams(params11);
			}

		}

	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = parentActivity.managedQuery(uri, projection, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public void hide() {
		clear();
		outerLayout.setVisibility(View.GONE);

	}

	@Override
	public void show() {
		outerLayout.setVisibility(View.VISIBLE);
	}

	private void showPicInGallery() {
		if (imageway.equals("false")) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_EDIT);
			intent.setDataAndType(Uri.fromFile(new File(imagepath)), "image/*");
			parentActivity.startActivityForResult(intent, 4);
		}

		else {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_EDIT);
			intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
			parentActivity.startActivityForResult(intent, 5);
		}

	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
