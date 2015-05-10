package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import model.GetMyListingsIdsModel;
import model.GetMySamplesIdsModel;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class GetMyListingsIds_Controller extends AsyncTask<Void, Void, Void> {

	private Context context;
	private int userid;

	public GetMyListingsIds_Controller(Context context, int userid) {

		this.context = context;
		this.userid = userid;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... args) {

		int result = GetMyListingsIdsModel.execute(userid);
		// Broadcast
		Intent intent = new Intent(Static_view_info.MYLISTINGSIDS_BROADCAST);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

}
