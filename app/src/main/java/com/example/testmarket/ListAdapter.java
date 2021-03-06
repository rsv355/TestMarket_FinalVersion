package com.example.testmarket;

import java.util.ArrayList;

import com.example.testmarket.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<RowData> {

	private static ArrayList<RowData> countryList;
	private Context context;

	public ListAdapter(Context context, int textViewResourceId,
			ArrayList<RowData> countryList) {
		super(context, textViewResourceId, countryList);
		ListAdapter.countryList = new ArrayList<RowData>();
		ListAdapter.countryList.addAll(countryList);
		this.context = context;
	}

	private class ViewHolder {
		TextView description;
		TextView quantity;
		TextView date_and_time;
		TextView region;
		ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.cell6, null);
			final LinearLayout infoLayout = (LinearLayout) convertView
					.findViewById(R.id.marekettab_layout1);

			ViewGroup.LayoutParams params2 = infoLayout.getLayoutParams();
			// size
			params2.height = Static_view_info.screenHeight / 4;// change the
																// hight
																// size
			infoLayout.setLayoutParams(params2);

			holder = new ViewHolder();
			holder.description = (TextView) convertView
					.findViewById(R.id.markettab_description1);
			holder.quantity = (TextView) convertView
					.findViewById(R.id.markettab_quantity1);
			holder.date_and_time = (TextView) convertView
					.findViewById(R.id.markettab_enddate1);
			holder.region = (TextView) convertView
					.findViewById(R.id.markettab_region1);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.markettab_icon1);

			final LinearLayout layout2 = (LinearLayout) convertView
					.findViewById(R.id.marekettab_layout2);
			ViewGroup.LayoutParams params22 = layout2.getLayoutParams();
			// size
			params22.width = (Static_view_info.screenWidth) / 2
					+ Static_view_info.screenWidth / 10;
			layout2.setLayoutParams(params22);

			final LinearLayout layout3 = (LinearLayout) convertView
					.findViewById(R.id.marekettab_layout3);
			ViewGroup.LayoutParams params3 = layout3.getLayoutParams();
			// size
			params3.width = Static_view_info.screenWidth / 2
					- Static_view_info.screenWidth / 10;
			layout3.setLayoutParams(params3);

			final ImageView desicon = (ImageView) convertView
					.findViewById(R.id.marekettab_descriptionicon);
			ViewGroup.LayoutParams params4 = desicon.getLayoutParams();
			// size
			params4.width = Static_view_info.screenWidth / 10;
			desicon.setLayoutParams(params4);

			final ImageView quantityicon = (ImageView) convertView
					.findViewById(R.id.marekettab_quantityicon);
			ViewGroup.LayoutParams params5 = quantityicon.getLayoutParams();
			// size
			params5.width = Static_view_info.screenWidth / 10;
			quantityicon.setLayoutParams(params5);

			final ImageView enddateicon = (ImageView) convertView
					.findViewById(R.id.marekettab_enddateicon);
			ViewGroup.LayoutParams params6 = enddateicon.getLayoutParams();
			// size
			params6.width = Static_view_info.screenWidth / 10;
			enddateicon.setLayoutParams(params6);

			final ImageView regionicon = (ImageView) convertView
					.findViewById(R.id.marekettab_regionicon1);
			ViewGroup.LayoutParams params7 = regionicon.getLayoutParams();
			// size
			params7.width = Static_view_info.screenWidth / 10;
			regionicon.setLayoutParams(params7);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RowData country = ListAdapter.countryList.get(position);
		holder.description
				.setText(descriptionSummary(country.getDescription()));

		String quantity = country.getQuantity();
		if (quantity.equals("0")) {
			holder.quantity.setTextColor(Color.RED);
			holder.quantity.setText("Out Of Stock");
		}

		else {
			holder.quantity.setTextColor(Color.BLACK);
			holder.quantity.setText("Remaining Quantity: "
					+ country.getQuantity());
		}
		holder.date_and_time.setText(country.getDate());
		holder.icon.setBackgroundDrawable(country.getDrawable());
		holder.region.setText(country.getRegion());
		return convertView;

	}

	private String descriptionSummary(String input) {
		if (input.length() > 100)
			return input.substring(0, 100) + " ...... ";

		return input;
	}

}
