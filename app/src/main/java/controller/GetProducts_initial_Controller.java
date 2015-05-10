package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;

import model.GetProducts_initial_Model;
import model.Signin_Model;
import model.Static_Info;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetProducts_initial_Controller extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String startId, action;
	private String region;

	public GetProducts_initial_Controller(Context context, String startId,
			String action, String region) {
		this.context = context;
		this.startId = startId;
		this.action = action;
		this.region = region;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... args) {

		int result = GetProducts_initial_Model.check(startId, action, region);

		Intent intent = new Intent(Static_view_info.LOADING_PRODUCTS);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);

		return null;
	}

}
