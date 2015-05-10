package com.example.testmarket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.testmarket.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfilePicPicker extends Activity {

	String email;
	String imagePath;
	double imageSize = 0.0;
	File fileTemp;
	private WindowManager wmanager;
	private Display display;
	private int imageWidth, imageHeight;

	private ImageView profilePic;
	private Button choosePic, launchButton;

	private String upLoadServerUri = "http://ghazy.comoj.com/test_market/UploadToServerProfile.php";
	private int serverResponseCode = 0;
//	ImageButton back_button;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_profile_pic_picker);
		// Catch E-mail to name the profile pic as it
		Intent intent1 = getIntent(); // gets the previously created intent
		email = intent1.getStringExtra("email");

		// set dimensions
		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		imageWidth = (int) (size.x);
		imageHeight = (int) (size.y);

		profilePic = (ImageView) findViewById(R.id.profilePic);

		ViewGroup.LayoutParams params1 = profilePic.getLayoutParams();
		params1.width = imageWidth / 2;
		params1.height = imageWidth / 2;
		profilePic.setLayoutParams(params1);

		choosePic = (Button) findViewById(R.id.choose);
		launchButton = (Button) findViewById(R.id.button2);

		choosePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chooseFromGallery();

			}
		});

		launchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(imageSize>0.0)
					launchAndupload();
				else{
					Intent intent = new Intent(ProfilePicPicker.this,
							HomePageActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});

	/*	back_button = (ImageButton) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});*/
		
	}

	private void launchAndupload() {
		new Thread(new Runnable() {
			public void run() {
				uploadFile(imagePath);
			}
		}).start();
		Intent intent = new Intent(ProfilePicPicker.this,
				HomePageActivity.class);
		startActivity(intent);
		finish();
	}

	private void chooseFromGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Uri selectedImageUri = data.getData();
			imagePath = getPath(selectedImageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
			File sourceFile = new File(imagePath);
			imageSize = ((sourceFile.length()) / 1024.0) / 1024.0;
			if (imageSize > 3) {
				// launchButton.setClickable(false);
				// Toast.makeText(ProfilePicPicker.this,
				// "Image size exceeded 3 M.B, Re-select image",
				// Toast.LENGTH_SHORT).show();

				BitmapFactory.Options bounds = new BitmapFactory.Options();
				bounds.inSampleSize = 2;
				Bitmap imgBitmap = BitmapFactory.decodeFile(imagePath, bounds);

				launchButton.setClickable(true);
				Drawable d = new BitmapDrawable(getResources(), imgBitmap);
				profilePic.setImageBitmap(imgBitmap);

				try {
					String path = Environment.getExternalStorageDirectory()
							.toString();
					OutputStream fOut = null;
					fileTemp = new File(path, "Temp" + ".jpg"); // the File to
																// save to
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
					MediaStore.Images.Media.insertImage(getContentResolver(),
							fileTemp.getAbsolutePath(), fileTemp.getName(),
							fileTemp.getName());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				launchButton.setClickable(true);
				profilePic.setImageBitmap(bitmap);
				ViewGroup.LayoutParams params1 = profilePic.getLayoutParams();
				params1.width = imageWidth / 2;
				params1.height = imageWidth / 2;
				profilePic.setLayoutParams(params1);
			}

		}
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ProfilePicPicker.this,
									"MalformedURLException", Toast.LENGTH_SHORT)
									.show();
						}
					});

					Log.e("Upload file to server", "error: " + ex.getMessage(),
							ex);
				} catch (Exception e) {
					e.printStackTrace();

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ProfilePicPicker.this,
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
	
	@Override
	public void onBackPressed() {
		Intent in = new Intent(ProfilePicPicker.this,HomePageActivity.class);
		startActivity(in);
		ProfilePicPicker.this.finish();
	}
}
