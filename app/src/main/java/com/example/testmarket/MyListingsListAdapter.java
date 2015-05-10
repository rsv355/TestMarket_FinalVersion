package com.example.testmarket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.ParseException;

import com.example.testmarket.R;

import controller.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyListingsListAdapter extends ArrayAdapter<RowData> {

	private static ArrayList<RowData> countryList;
	private Context context;

	public MyListingsListAdapter(Context context, int textViewResourceId,
			ArrayList<RowData> countryList) {
		super(context, textViewResourceId, countryList);
		MyListingsListAdapter.countryList = new ArrayList<RowData>();
		MyListingsListAdapter.countryList.addAll(countryList);
		this.context = context;
	}

	private class ViewHolder {
		TextView description;
		TextView quantity;
		TextView date_and_time;
		TextView region;
		ImageView icon;
		TextView redeemed;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.mylistings_cell, null);
			final LinearLayout infoLayout = (LinearLayout) convertView
					.findViewById(R.id.mylistingstab_layout1);

			ViewGroup.LayoutParams params2 = infoLayout.getLayoutParams();
			params2.height = Static_view_info.screenHeight / 4;// change the
																// hight
																// size
			infoLayout.setLayoutParams(params2);

			holder = new ViewHolder();
			holder.description = (TextView) convertView
					.findViewById(R.id.mylistingstab_description1);
			holder.quantity = (TextView) convertView
					.findViewById(R.id.mylistingstab_quantity1);
			holder.date_and_time = (TextView) convertView
					.findViewById(R.id.mylistingstab_enddate1);
			holder.region = (TextView) convertView
					.findViewById(R.id.mylistingstab_region1);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_icon1);
			holder.redeemed = (TextView) convertView
					.findViewById(R.id.mylistingstab_redeemed);

			final LinearLayout layout2 = (LinearLayout) convertView
					.findViewById(R.id.mylistingstab_layout2);
			ViewGroup.LayoutParams params22 = layout2.getLayoutParams();
			// size
			params22.width = (Static_view_info.screenWidth) / 2
					+ Static_view_info.screenWidth / 10;
			layout2.setLayoutParams(params22);

			final LinearLayout layout3 = (LinearLayout) convertView
					.findViewById(R.id.mylistingstab_layout3);
			ViewGroup.LayoutParams params3 = layout3.getLayoutParams();
			// size
			params3.width = Static_view_info.screenWidth / 2
					- Static_view_info.screenWidth / 10;
			layout3.setLayoutParams(params3);

			final ImageView desicon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_descriptionicon);
			ViewGroup.LayoutParams params4 = desicon.getLayoutParams();
			// size
			params4.width = Static_view_info.screenWidth / 10;
			desicon.setLayoutParams(params4);

			final ImageView quantityicon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_quantityicon);
			ViewGroup.LayoutParams params5 = quantityicon.getLayoutParams();
			// size
			params5.width = Static_view_info.screenWidth / 10;
			quantityicon.setLayoutParams(params5);

			final ImageView enddateicon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_enddateicon);
			ViewGroup.LayoutParams params6 = enddateicon.getLayoutParams();
			// size
			params6.width = Static_view_info.screenWidth / 10;
			enddateicon.setLayoutParams(params6);

			final ImageView regionicon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_regionicon1);
			ViewGroup.LayoutParams params7 = regionicon.getLayoutParams();
			// size
			params7.width = Static_view_info.screenWidth / 10;
			regionicon.setLayoutParams(params7);

			final ImageView redeemicon = (ImageView) convertView
					.findViewById(R.id.mylistingstab_redeemedicon);
			ViewGroup.LayoutParams params8 = redeemicon.getLayoutParams();
			// size
			params8.width = Static_view_info.screenWidth / 10;
			redeemicon.setLayoutParams(params8);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RowData country = MyListingsListAdapter.countryList.get(position);
		holder.description
				.setText(descriptionSummary(country.getDescription()));
		String quantity = country.getQuantity();
		if (quantity.equals("0")){
			holder.quantity.setTextColor(Color.RED);
			holder.quantity.setText("Out Of Stock");
		}

		else{
			holder.quantity.setTextColor(Color.BLACK);
			holder.quantity.setText("Remaining Samples: "
					+ country.getQuantity());
		}
		holder.date_and_time.setText(country.getDate());
		holder.icon.setBackgroundDrawable(country.getDrawable());
		holder.region.setText(country.getRegion());
		String endDate = country.getEndDate();
		String endTime = country.getEndTime();
		endDate = editEndDate(endDate);

		String todayDate = todayDate();
		if (checkExpired(endDate, endTime, todayDate)) {
			holder.redeemed.setTextColor(Color.RED);
			holder.redeemed.setText("Expired");

		} else {
			holder.redeemed.setTextColor(Color.BLACK);
			holder.redeemed.setText("Available");
		}

		return convertView;

	}

	private String descriptionSummary(String input) {
		if (input.length() > 50)
			return input.substring(0, 50) + " ...... ";

		return input;
	}

	private String editEndDate(String endDate) {
		String[] array = endDate.split("-");
		String day = array[2];
		String month = array[1];
		String year = array[0];

		String newDate = day + "-" + month + "-" + year;
		return newDate;
	}

	public String todayDate() {
		Calendar cal = Calendar.getInstance();
		String month_display = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day_display = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String year_display = String.valueOf(cal.get(Calendar.YEAR));
		String hour_display = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		String min_display = String.valueOf(cal.get(Calendar.MINUTE));
		String today = day_display + "-" + month_display + "-" + year_display
				+ "-" + hour_display + "-" + min_display;
		return today;
	}

	private boolean checkExpired(String endDate, String endTime, String today) {
		String[] endDate1 = endDate.split("-");
		String[] endTime1 = endTime.split(":");
		int endDay = Integer.parseInt(endDate1[0]);
		int endMonth = Integer.parseInt(endDate1[1]);
		int endYear = Integer.parseInt(endDate1[2]);
		int endHour = Integer.parseInt(endTime1[0]);
		int endMin = Integer.parseInt(endTime1[1]);

		String[] today1 = today.split("-");
		int todayDay = Integer.parseInt(today1[0]);
		int todayMonth = Integer.parseInt(today1[1]);
		int todayYear = Integer.parseInt(today1[2]);
		int todayHour = Integer.parseInt(today1[3]);
		int todayMin = Integer.parseInt(today1[4]);

		if (endYear > todayYear)
			return false;
		else if (endYear == todayYear) {
			if (endMonth > todayMonth)
				return false;
			else if (endMonth == todayMonth) {
				if (endDay > todayDay)
					return false;
				else if (endDay == todayDay) {
					if (endHour > todayHour)
						return false;
					else if (endHour == todayHour) {
						if (endMin > todayMin)
							return false;
						else
							return true;
					} else {
						return true;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			// expired
			return true;
		}

	}
}
