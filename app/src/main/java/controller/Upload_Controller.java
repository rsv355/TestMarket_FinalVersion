package controller;

import com.example.testmarket.Static_view_info;

import model.Upload_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class Upload_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String desc, quant, addr, date1, time1, endDate1, endTime1, region,
			email, imageURL;
	private Context context;

	public Upload_Controller(ProgressDialog pDialog, Context context,
			String description, String quantity, String address, String date,
			String time, String endDate1, String endTime1, String region,
			String email1, String imageURLtoupload) {
		this.pDialog = pDialog;
		this.imageURL = imageURLtoupload;
		this.desc = description;
		this.quant = quantity;
		this.addr = address;
		this.date1 = date;
		this.time1 = time;
		this.endDate1 = endDate1;
		this.endTime1 = endTime1;
		this.region = region;
		this.email = email1;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog.setMessage("Uploading Please Wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... args) {
		int result = Upload_Model.createProduct(desc, quant, addr, date1,
				time1, endDate1, endTime1, region, email, imageURL);
		Intent intent = new Intent(Static_view_info.Uploaded_BROADCAST);
		intent.putExtra("productId", result);
		context.getApplicationContext().sendBroadcast(intent);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
