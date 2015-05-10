package controller;

import com.example.testmarket.Static_view_info;

import model.Static_Info;
import model.SamplePage_Model;
import model.UpdateListingsItem_Model;
import model.UpdateProfile_Model;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class UpdateListing_Controller extends AsyncTask<Void, Void, Void> {

	private ProgressDialog pDialog;
	private String userId, description, address, date, Time, region, endDate , endTime, quantity;
	private Context context;

	public UpdateListing_Controller(ProgressDialog pDialog, Context context,
			String UserId, String descrp, String address,
			String date1, String Time1, String region, String endDate1,String endTime1, String quantity) {
		this.pDialog = pDialog;
		this.context = context;
		this.userId = UserId;
		this.description = descrp;
		this.address = address;
		this.date = date1;
		this.Time=Time1;
		this.region = region;
		this.endDate = endDate1;
		this.endTime=endTime1;
		this.quantity = quantity;
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
		if(description.equals("1") && address.equals("1")){
			int result = UpdateListingsItem_Model.execute(userId, description, address, date,Time,
					region, endDate, endTime, quantity);
			Intent intent = new Intent(Static_view_info.UPDATE_LISTING_BROADCAST);
			intent.putExtra("result", result);
			context.getApplicationContext().sendBroadcast(intent);
			return null;
		}else{
			int result = UpdateListingsItem_Model.execute(userId, description, address, date,Time,
					region, endDate, endTime, quantity);
			Intent intent = new Intent(Static_view_info.UPDATE_LISTING_BROADCAST);
			intent.putExtra("result", result);
			context.getApplicationContext().sendBroadcast(intent);
			return null;
		}
		
	}

	@Override
	protected void onPostExecute(Void result) {
		pDialog.dismiss();
	}

}
