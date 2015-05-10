package controller;

import com.example.testmarket.Static_view_info;

import model.CheckSampledModel;
import model.GetUserName_Model;
import model.Signin_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class CheckSampledController extends AsyncTask<Void, Void, Void> {

	private String userId, productId;
	private Context context;

	public CheckSampledController(Context context, String userId,
			String productId) {
		this.userId = userId;
		this.productId = productId;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... args) {
		int result = CheckSampledModel.execute(userId, productId);
		Intent intent = new Intent(Static_view_info.CHECK_SAMPLED_BROADCAST);
		intent.putExtra("result", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

	}

}
