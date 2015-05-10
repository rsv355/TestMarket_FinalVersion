package com.example.testmarket;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.testmarket.R;
import controller.ConnectionDetector;
import controller.GetMySamplesIds_Controller;
import controller.GetMySamples_Controller;
import controller.GetUserName_Controller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MySamplesPanel implements HomePagePanelInterface {

	private WindowManager wmanager;
	private Display display;
	private RelativeLayout outerLayout;

	private ListView listView;
	private ArrayList<RowData> MySampleList;
	private MySamplesListAdapter dataAdapter = null;
	private View loadMoreView;
	private BroadcastReceiver mySamplesIds_receiver, mySamples_receiver,
			getUserName_receiver;
	private ProgressBar progressBar;

	private TextView page1, page2, page3, page4, page5, noSamples;
	private ImageButton nextPage, previousPage;
	RowData clickedItem;
	int clickedPosition;

	private int page1startingSampleIndex, page1endingSampleIndex;
	private int page2startingSampleIndex, page2endingSampleIndex;
	private int page3startingSampleIndex, page3endingSampleIndex;
	private int page4startingSampleIndex, page4endingSampleIndex;
	private int page5startingSampleIndex, page5endingSampleIndex;

	private int page1Text = 1, page2Text = 2, page3Text = 3, page4Text = 4,
			page5Text = 5;
	private boolean FIRST_TIME = true;

	private GetMySamplesIds_Controller initialThread;
	private GetMySamples_Controller loadThread;

	private ConnectionDetector internetConnection;

	private Context context;
	private Activity parentActivity;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Context context, Activity parentActivity) {
		this.context = context;
		this.parentActivity = parentActivity;

		internetConnection = new ConnectionDetector(context);
		wmanager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);

		outerLayout = (RelativeLayout) parentActivity
				.findViewById(R.id.mysamplestab_outerlayout);
		progressBar = (ProgressBar) parentActivity
				.findViewById(R.id.mysamplestap_progressBar1);
		noSamples = (TextView) parentActivity
				.findViewById(R.id.mysamplestap_textView1);
		noSamples.setVisibility(View.INVISIBLE);
		listView = (ListView) parentActivity
				.findViewById(R.id.mysamplestap_listView1);
		listView.setVisibility(View.INVISIBLE);

		loadMoreView = ((LayoutInflater) parentActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.mysamplelistfooter, null, false);
		loadMoreView.setClickable(false);
		listView.addFooterView(loadMoreView);

		// create an ArrayAdaptar from the String Array
		MySampleList = new ArrayList<RowData>();

		// enables filtering for the contents of the given ListView
		listView.setTextFilterEnabled(true);

		page1 = (TextView) parentActivity.findViewById(R.id.mysamplestap_page1);
		page2 = (TextView) parentActivity.findViewById(R.id.mysamplestap_page2);
		page3 = (TextView) parentActivity.findViewById(R.id.mysamplestap_page3);
		page4 = (TextView) parentActivity.findViewById(R.id.mysamplestap_page4);
		page5 = (TextView) parentActivity.findViewById(R.id.mysamplestap_page5);

		previousPage = (ImageButton) parentActivity
				.findViewById(R.id.mysamplestap_previouspage);
		nextPage = (ImageButton) parentActivity
				.findViewById(R.id.mysamplestap_nextpage);

		setupClickListeners();
		// initiating the products
		// initialThread = new GetMySamplesIds_Controller(parentActivity,
		// Static_view_info.userId);
		// initialThread.execute();

		mySamplesIds_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_initiate_mySamplesIds_result(result);
			}

		};
		
		try{
	
				IntentFilter receiverfilter = new IntentFilter(
						Static_view_info.MYSAMPLESIDS_BROADCAST);
				parentActivity.registerReceiver(mySamplesIds_receiver, receiverfilter);
			
			
		}catch(Exception e){}
		

		mySamples_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_initiate_mySamples_result(result);
			}

		};
		
		try{
			
				IntentFilter receiverfilter1 = new IntentFilter(
						Static_view_info.MYSAMPLES_BROADCAST);
				parentActivity.registerReceiver(mySamples_receiver, receiverfilter1);
			
			
		}catch(Exception e){}
		

		getUserName_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String username = intent.getStringExtra("result");
				int flag = intent.getIntExtra("flag", -1);
				if (flag == 1) {
					openProductPage(username);
				}
			}

		};
		
		try{
			
			if(getUserName_receiver != null){
				IntentFilter receiverfilter2 = new IntentFilter(
						Static_view_info.GETUSERNAME_BROADCAST);
				parentActivity.registerReceiver(getUserName_receiver, receiverfilter2);
			}
			
		}catch(Exception e){}
		

	}

	@Override
	public void hide() {
		if (initialThread != null)
			initialThread.cancel(true);
		if (loadThread != null)
			loadThread.cancel(true);
		outerLayout.setVisibility(View.GONE);

		clean();
	}

	@Override
	public void show() {
		outerLayout.setVisibility(View.VISIBLE);

		Log.d("ghazy", "onresume called");
		progressBar.setVisibility(View.VISIBLE);
		noSamples.setVisibility(View.INVISIBLE);
		listView.setVisibility(View.INVISIBLE);
		// if (Static_view_info.sampleStateChanged) {
		initialThread = new GetMySamplesIds_Controller(parentActivity,
				Static_view_info.userId);
		initialThread.execute();

		Static_view_info.sampleStateChanged = false;
		// } else {
		//
		// if (!Static_view_info.mySamplesIds.equals("null")) {
		// // get frist max mysamples ids
		// page1startingSampleIndex = 0;
		// if (page1startingSampleIndex + Static_view_info.MAX_MYSAMPLES >
		// Static_view_info.idsSize)
		// page1endingSampleIndex = Static_view_info.idsSize;
		// else
		// page1endingSampleIndex = page1startingSampleIndex
		// + Static_view_info.MAX_MYSAMPLES;
		// updatePagesCounters();
		// loadPage(page1startingSampleIndex, page1endingSampleIndex);
		//
		// } else {
		// // The user didn't sample any product
		// noSamples.setVisibility(View.VISIBLE);
		// listView.setVisibility(View.INVISIBLE);
		// progressBar.setVisibility(View.INVISIBLE);
		// }
		//
		// }

	}

	private void setupClickListeners() {
		page1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "page 1 clicked");
					page1.setTextColor(Color.RED);
					page2.setTextColor(Color.BLACK);
					page3.setTextColor(Color.BLACK);
					page4.setTextColor(Color.BLACK);
					page5.setTextColor(Color.BLACK);
					loadPage(page1startingSampleIndex, page1endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		page2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "page 2 clicked");
					page2.setTextColor(Color.RED);
					page1.setTextColor(Color.BLACK);
					page3.setTextColor(Color.BLACK);
					page4.setTextColor(Color.BLACK);
					page5.setTextColor(Color.BLACK);
					loadPage(page2startingSampleIndex, page2endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		page3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "page 3 clicked");
					page3.setTextColor(Color.RED);
					page1.setTextColor(Color.BLACK);
					page2.setTextColor(Color.BLACK);
					page4.setTextColor(Color.BLACK);
					page5.setTextColor(Color.BLACK);

					loadPage(page3startingSampleIndex, page3endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		page4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "page 4 clicked");
					page4.setTextColor(Color.RED);
					page1.setTextColor(Color.BLACK);
					page3.setTextColor(Color.BLACK);
					page2.setTextColor(Color.BLACK);
					page5.setTextColor(Color.BLACK);

					loadPage(page4startingSampleIndex, page4endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		page5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "page 5 clicked");
					page5.setTextColor(Color.RED);
					page1.setTextColor(Color.BLACK);
					page3.setTextColor(Color.BLACK);
					page4.setTextColor(Color.BLACK);
					page2.setTextColor(Color.BLACK);

					loadPage(page5startingSampleIndex, page5endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		previousPage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "prev clicked");
					page1startingSampleIndex = page1startingSampleIndex
							- (5 * Static_view_info.MAX_MYSAMPLES);
					page1endingSampleIndex = page1startingSampleIndex
							+ Static_view_info.MAX_MYSAMPLES;
					page1Text -= 5;
					page2Text -= 5;
					page3Text -= 5;
					page4Text -= 5;
					page5Text -= 5;
					updatePagesCounters();
					loadPage(page1startingSampleIndex, page1endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		nextPage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					Log.d("ghazy", "next clicked");
					page1startingSampleIndex = page5endingSampleIndex;
					if (page1startingSampleIndex
							+ Static_view_info.MAX_MYSAMPLES < Static_view_info.idsSize)
						page1endingSampleIndex = page1startingSampleIndex
								+ Static_view_info.idsSize;
					else
						page1endingSampleIndex = Static_view_info.idsSize;
					page1Text += 5;
					page2Text += 5;
					page3Text += 5;
					page4Text += 5;
					page5Text += 5;
					updatePagesCounters();
					loadPage(page1startingSampleIndex, page1endingSampleIndex);
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void updatePagesCounters() {
		if (page1startingSampleIndex == 0) {
			previousPage.setVisibility(View.INVISIBLE);
		} else {
			previousPage.setVisibility(View.VISIBLE);
		}

		page1.setText("" + page1Text);
		page1.setTextColor(Color.RED);
		page1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		if (page1endingSampleIndex < Static_view_info.idsSize) {
			page2.setVisibility(View.VISIBLE);
			page2.setText("" + page2Text);
			page2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
			page2.setTextColor(Color.BLACK);
			page2startingSampleIndex = page1endingSampleIndex;
			if (page2startingSampleIndex + Static_view_info.MAX_MYSAMPLES < Static_view_info.idsSize) {
				page2endingSampleIndex = page2startingSampleIndex
						+ Static_view_info.MAX_MYSAMPLES;
				page3.setVisibility(View.VISIBLE);
				page3.setText("" + page3Text);
				page3.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				page3.setTextColor(Color.BLACK);
				page3startingSampleIndex = page2endingSampleIndex;
				if (page3startingSampleIndex + Static_view_info.MAX_MYSAMPLES < Static_view_info.idsSize) {
					page3endingSampleIndex = page3startingSampleIndex
							+ Static_view_info.MAX_MYSAMPLES;
					page4.setVisibility(View.VISIBLE);
					page4.setText("" + page4Text);
					page4.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
					page4.setTextColor(Color.BLACK);
					page4startingSampleIndex = page3endingSampleIndex;
					if (page4startingSampleIndex
							+ Static_view_info.MAX_MYSAMPLES < Static_view_info.idsSize) {
						page4endingSampleIndex = page4startingSampleIndex
								+ Static_view_info.MAX_MYSAMPLES;
						page5.setVisibility(View.VISIBLE);
						page5.setText("" + page5Text);
						page5.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
						page5.setTextColor(Color.BLACK);
						page5startingSampleIndex = page4endingSampleIndex;
						if (page5startingSampleIndex
								+ Static_view_info.MAX_MYSAMPLES < Static_view_info.idsSize) {
							page5endingSampleIndex = page5startingSampleIndex
									+ Static_view_info.MAX_MYSAMPLES;
							nextPage.setVisibility(View.VISIBLE);
						} else {
							nextPage.setVisibility(View.INVISIBLE);
						}
					} else {
						page4endingSampleIndex = Static_view_info.idsSize;
						page5.setVisibility(View.INVISIBLE);
						nextPage.setVisibility(View.INVISIBLE);
					}
				} else {
					page3endingSampleIndex = Static_view_info.idsSize;
					page4.setVisibility(View.INVISIBLE);
					page5.setVisibility(View.INVISIBLE);
					nextPage.setVisibility(View.INVISIBLE);
				}
			} else {
				page2endingSampleIndex = Static_view_info.idsSize;
				page3.setVisibility(View.INVISIBLE);
				page4.setVisibility(View.INVISIBLE);
				page5.setVisibility(View.INVISIBLE);
				nextPage.setVisibility(View.INVISIBLE);
			}

		} else {
			page2.setVisibility(View.INVISIBLE);
			page3.setVisibility(View.INVISIBLE);
			page4.setVisibility(View.INVISIBLE);
			page5.setVisibility(View.INVISIBLE);
			nextPage.setVisibility(View.INVISIBLE);
		}
	}

	private void loadPage(int startingProductId, int endingProductId) {
		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.INVISIBLE);
		noSamples.setVisibility(View.INVISIBLE);
		clean();
		loadThread = new GetMySamples_Controller(parentActivity,
				startingProductId, endingProductId);
		loadThread.execute();
	}

	private void clean() {
		for (int i = 0; i < MySampleList.size(); i++) {
			Drawable currentDrawable = MySampleList.get(i).getDrawable();
			if (currentDrawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) currentDrawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
			currentDrawable = null;
		}
		MySampleList.clear();
		Static_view_info.mysamples_drawables.clear();
		Static_view_info.currentMySamples = null;
		dataAdapter = null;
	}

	private void handle_initiate_mySamplesIds_result(int result) {
		if (result == 1) {
			// get frist max mysamples ids
			page1startingSampleIndex = 0;
			if (page1startingSampleIndex + Static_view_info.MAX_MYSAMPLES > Static_view_info.idsSize)
				page1endingSampleIndex = Static_view_info.idsSize;
			else
				page1endingSampleIndex = page1startingSampleIndex
						+ Static_view_info.MAX_MYSAMPLES;
			loadPage(page1startingSampleIndex, page1endingSampleIndex);

		} else if (result == 2) {
			// The user didn't sample any product
			noSamples.setVisibility(View.VISIBLE);
			listView.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			if (FIRST_TIME)
				FIRST_TIME = false;
		}

	}

	private void handle_initiate_mySamples_result(int result) {
		if (result == 1) {
			progressBar.setVisibility(View.INVISIBLE);
			noSamples.setVisibility(View.INVISIBLE);
			listView.setVisibility(View.VISIBLE);
			try {
				initiateListView();
			} catch (OutOfMemoryError e) {
				Log.d("ghazy", "outOfMemory");
			}
		}
	}

	private void initiateListView() {
		if (Static_view_info.currentMySamples != null) {
			JSONArray samples_array;
			try {
				String[] redeems = Static_view_info.mySamplesredeem.split(" ");
				samples_array = Static_view_info.currentMySamples;
				String productId = "1";
				for (int i = 0; i < samples_array.length(); i++) {
					JSONObject temp = samples_array.getJSONObject(i);
					String description = temp.getString("Description");
					String quantity = temp.getString("Quantity");
					String time = temp.getString("CollectionTime");
					String date = temp.getString("CollectionDate");
					String endDate = temp.getString("endDate");
					String endTime = temp.getString("endTime");
					String region = temp.getString("region");
					String address = temp.getString("CollectionAddress");
					String userId = temp.getString("UserId");
					productId = temp.getString("id");
					Drawable drawable = Static_view_info.mysamples_drawables
							.get(i);

					RowData rowData = new RowData(productId, description, time,
							date, endDate, endTime, region, address, quantity,
							userId, redeems[i], drawable);
					MySampleList.add(rowData);
				}

				redeems = null;
				if (FIRST_TIME) {
					FIRST_TIME = false;
					updatePagesCounters();
				}

				dataAdapter = new MySamplesListAdapter(parentActivity,
						R.layout.cell6, MySampleList);

				listView.setAdapter(dataAdapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {

				if (internetConnection.isConnectingToInternet()) {
					clickedItem = MySampleList.get(position);
					String uploaderUser = clickedItem.getUserId();
					clickedPosition = position;
					new GetUserName_Controller(new ProgressDialog(
							parentActivity), parentActivity, uploaderUser, 1)
							.execute();
				} else {
					Toast.makeText(parentActivity,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	private void openProductPage(String username) {
		String description = clickedItem.getDescription();
		String address = clickedItem.getAdress();
		String date = clickedItem.getDate();
		String time = clickedItem.getTime();
		String endDate = clickedItem.getEndDate();
		String endTime = clickedItem.getEndTime();
		String region = clickedItem.getRegion();
		String quantity = clickedItem.getQuantity();
		String productId = clickedItem.getProductId();
		String redeem = clickedItem.getIsRedeem();
		Intent intent = new Intent(parentActivity, RedemPageActivity.class);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		intent.putExtra("description", description);
		intent.putExtra("address", address);
		intent.putExtra("username", username);
		intent.putExtra("date", date);
		intent.putExtra("time", time);
		intent.putExtra("quantity", quantity);
		intent.putExtra("productId", productId);
		intent.putExtra("redeem", redeem);
		intent.putExtra("endDate", endDate);
		intent.putExtra("endTime", endTime);
		intent.putExtra("region", region);

		// productPage = true;
		parentActivity.startActivity(intent);
		parentActivity.overridePendingTransition(R.anim.slide_left_show,
				R.anim.slide_left_hide);
	//	parentActivity.finish();
		
	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	void un_reg(){
		parentActivity.unregisterReceiver(mySamplesIds_receiver);
		parentActivity.unregisterReceiver(mySamples_receiver);
		parentActivity.unregisterReceiver(getUserName_receiver);
	}

	void reg(){
		IntentFilter receiverfilter = new IntentFilter(
				Static_view_info.MYSAMPLESIDS_BROADCAST);
		parentActivity.registerReceiver(mySamplesIds_receiver, receiverfilter);
		
		IntentFilter receiverfilter1 = new IntentFilter(
						Static_view_info.MYSAMPLES_BROADCAST);
				parentActivity.registerReceiver(mySamples_receiver, receiverfilter1);
		
		IntentFilter receiverfilter2 = new IntentFilter(
						Static_view_info.GETUSERNAME_BROADCAST);
				parentActivity.registerReceiver(getUserName_receiver, receiverfilter2);
	}
}
