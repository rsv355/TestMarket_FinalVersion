package controller;


import com.example.testmarket.Static_view_info;

import model.Signin_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class Signin_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String email;
	private String password;
	private Context context;

	public Signin_Controller(ProgressDialog pDialog, Context context,
			String email1, String password) {
		this.pDialog = pDialog;
		this.email = email1;
		this.password = password;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog.setMessage("Signing In Please Wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... args) {
		int result = Signin_Model.check(email, password);
		Intent intent = new Intent(Static_view_info.SIGN_IN_BROADCAST);
		intent.putExtra("result", result);
		intent.putExtra("facebook", 0);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
