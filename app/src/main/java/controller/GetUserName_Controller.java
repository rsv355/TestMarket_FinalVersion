package controller;


import com.example.testmarket.Static_view_info;

import model.GetUserName_Model;
import model.Signin_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class GetUserName_Controller extends AsyncTask<Void, Void, Void> {

	private String userId;
	private Context context;
	private ProgressDialog pDialog;
	private int flag1;

	public GetUserName_Controller(ProgressDialog pDialog, Context context,
			String userId, int flag) {
		this.userId = userId;
		this.pDialog = pDialog;
		this.context = context;
		this.flag1=flag;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog.setMessage("...loading...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... args) {
		String result = GetUserName_Model.execute(userId);
		Intent intent = new Intent(Static_view_info.GETUSERNAME_BROADCAST);
		intent.putExtra("result", result);
		intent.putExtra("flag", flag1);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
