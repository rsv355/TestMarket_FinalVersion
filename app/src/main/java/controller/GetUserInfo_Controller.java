package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.testmarket.Static_view_info;

import model.GetUserInfo_Model;
import model.Static_Info;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class GetUserInfo_Controller extends AsyncTask<Void, Void, Void> {

	private String userId;
	private Context context;

	public GetUserInfo_Controller(Context context, String userId) {
		this.userId = userId;

		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... args) {
		String[] result = GetUserInfo_Model.execute(userId);
		Drawable profilePic = getButtonDrawable(Static_view_info.userId + "",
				Static_Info.url_profile_pic + Static_view_info.userId + "");
		Intent intent = new Intent(Static_view_info.PROFILE_BROADCAST);
		intent.putExtra("result", result);
		Static_view_info.profilePic = profilePic;
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

	}

	private Drawable getButtonDrawable(String filename, String image_url) {
		File cacheDir = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"Test market_place/profile");
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
			Drawable d = Drawable.createFromPath(cacheDir + "/" + filename);
			return d;
		} else {
			Drawable d = Drawable.createFromPath(cacheDir + "/" + filename);
			return d;
		}
	}

	private Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
