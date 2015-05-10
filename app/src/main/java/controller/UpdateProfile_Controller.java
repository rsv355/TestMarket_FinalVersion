package controller;

import com.example.testmarket.Static_view_info;

import model.Static_Info;
import model.SamplePage_Model;
import model.UpdateProfile_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class UpdateProfile_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String userId, email, password, name, contact, location, birthday,
			gender;
	private Context context;

	public UpdateProfile_Controller(ProgressDialog pDialog, Context context,
			String UserId, String email, String password, String name,
			String contact, String location, String birthday, String gender) {
		this.pDialog = pDialog;
		this.context = context;
		this.userId = UserId;
		this.email = email;
		this.password = password;
		this.name = name;
		this.contact = contact;
		this.location = location;
		this.birthday = birthday;
		this.gender = gender;
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
		int result = UpdateProfile_Model.execute(userId, email, password, name,
				contact, location, birthday, gender);
		Intent intent = new Intent(Static_view_info.UPDATE_PROFILE_BROADCAST);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
