package com.example.testmarket;

import model.RedemPage_Model;
import model.Static_Info;
import model.SamplePage_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RedemPage_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String desc, userId;
	private Context context;

	public RedemPage_Controller(ProgressDialog pDialog, Context context,
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
		int result = RedemPage_Model.createSample(desc, userId);
		Intent intent = new Intent(Static_view_info.REDEM_BROADCAST);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
