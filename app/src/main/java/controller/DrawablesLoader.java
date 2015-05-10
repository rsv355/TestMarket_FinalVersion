package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.ListAdapter;
import com.example.testmarket.RowData;
import com.example.testmarket.Static_view_info;


import model.Signin_Model;
import model.Static_Info;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

public class DrawablesLoader extends AsyncTask<Void, Void, Void> {

	private Context context;
	private ArrayList<RowData> countryList;
	private ListAdapter adapter;

	public DrawablesLoader(Context context, ArrayList<RowData> countryList,
			ListAdapter adapter) {

		this.context = context;
		this.countryList = countryList;
		this.adapter = adapter;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... args) {
		for (int counter = 0; counter < countryList.size(); counter++) {

			String drawable_name = countryList.get(counter).getProductId();
			String drawable_path = Static_Info.url_image + drawable_name;
			Drawable drawable = getButtonDrawable(drawable_name, drawable_path);
			countryList.get(counter).setDrawable(drawable);
			Intent intent = new Intent(
					Static_view_info.DRAWABLE_THREAD_BROADCAST);
			intent.putExtra("result", "1");
			context.getApplicationContext().sendBroadcast(intent);

		}

		return null;
	}

	private Drawable getButtonDrawable(String filename, String image_url) {
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
				InputStream is = new BufferedInputStream(new URL(image_url).openStream(), BUFFER_IO_SIZE);;
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

//	private Object fetch(String address) throws MalformedURLException,
//			IOException {
//		URL url = new URL(address);
//		URLConnection connection = url.openConnection();
//		connection.connect(); 
//		Object content = url.getContent();
//		return content;
//	}

	@Override
	protected void onPostExecute(Void result) {

	}

}
