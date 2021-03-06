package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import model.GetMyListingsModel;
import model.GetMySamplesModel;
import model.Static_Info;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class GetMyListings_Controller extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String ids;

	public GetMyListings_Controller(Context context, int startingIndex,
			int endingIndex) {

		this.context = context;
		getIds(startingIndex, endingIndex);
	}

	private void getIds(int startingIndex, int endingIndex) {
		String[] array = Static_view_info.myListingsIds.split(" ");
		ids = array[startingIndex];
		for (int i = startingIndex + 1; i < endingIndex; i++) {
			ids += " " + array[i];
		}
		array = null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... args) {

		try {
			int result = GetMyListingsModel.execute(ids);
			for (int counter = 0; counter < Static_view_info.currentMyListings
					.length(); counter++) {
				JSONObject temp = Static_view_info.currentMyListings
						.getJSONObject(counter);
				String drawable_name = temp.getString("id");
				String drawable_path = Static_Info.url_image + drawable_name;
				Drawable drawable = getButtonDrawable(drawable_name,
						drawable_path);
				Static_view_info.mylistings_drawables.add(counter, drawable);

			}
			Intent intent = new Intent(Static_view_info.MYLISTINGS_BROADCAST);
			intent.putExtra("result", result);
			context.getApplicationContext().sendBroadcast(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Drawable d = null;
		
		File f = new File(cacheDir + "/" + filename);
		if (!f.exists()) {
			try {
				int BUFFER_IO_SIZE = 16000;
				InputStream is = new BufferedInputStream(
						new URL(image_url).openStream(), BUFFER_IO_SIZE);

				f.createNewFile();
				OutputStream os = new FileOutputStream(new File(cacheDir + "/"
						+ filename));
				Utils.CopyStream(is, os);
				os.close();
				is.close();
				d = Drawable.createFromPath(cacheDir + "/" + filename);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return d;
		} else {
			Drawable dd = null;
			try{
				dd = Drawable.createFromPath(cacheDir + "/" + filename);
			}catch(Exception e){}
			
			return dd;
		}
	}

	private Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	@Override
	protected void onPostExecute(Void result) {

	}

}
