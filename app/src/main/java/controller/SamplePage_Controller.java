package controller;


import com.example.testmarket.Static_view_info;

import model.Static_Info;
import model.SamplePage_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class SamplePage_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String desc, userId;
	private Context context;

	public SamplePage_Controller(ProgressDialog pDialog, Context context,
			String description, String UserId) {
		this.pDialog = pDialog;
		this.desc = description;
		this.context = context;
		this.userId = UserId;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog.setMessage("Please Wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... args) {
		int result = SamplePage_Model.createSample(desc, userId);
		Intent intent = new Intent(Static_view_info.SAMPLE_BROADCAST);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
