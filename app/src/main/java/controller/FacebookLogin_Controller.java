package controller;


import com.example.testmarket.Static_view_info;

import model.FacebookLogin_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class FacebookLogin_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String email, password, name, contact, location, birthday, gender;
	private Context context;

	public FacebookLogin_Controller(ProgressDialog pDialog, Context context,
			String email, String password, String name, String contact,
			String location, String birthday, String gender) {
		this.pDialog = pDialog;
		this.email = email;
		this.password = password;
		this.name = name;
		this.contact = contact;
		this.location = location;
		this.birthday = birthday;
		this.gender = gender;
		this.context = context;
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
		int result = FacebookLogin_Model.createUser(email, password, name,
				contact, location, birthday, gender);
		Intent intent = new Intent(Static_view_info.SIGN_IN_BROADCAST);
		intent.putExtra("result", result);
		intent.putExtra("facebook", 1);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
