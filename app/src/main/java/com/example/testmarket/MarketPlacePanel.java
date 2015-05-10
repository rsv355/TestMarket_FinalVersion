package com.example.testmarket;

import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.testmarket.R;
import controller.ConnectionDetector;
import controller.DrawablesLoader;
import controller.GetProducts_initial_Controller;
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
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class MarketPlacePanel implements HomePagePanelInterface {
	private WindowManager wmanager;
	private Display display;

	private FilterActionMenu filterMenu;
	private ToggleButton filterButton;
	private RelativeLayout outerLayout;
	private ListView listView;
	private ArrayList<RowData> countryList;
	private ListAdapter dataAdapter = null;
	private View loadMoreView;
	private int action = 0;/* initiate */
	private BroadcastReceiver drawable_thread_receiver,
			initiate_products_receiver, getUserName_receiver;
	private ProgressBar progressBar;

	private TextView page1, page2, page3, page4, page5, noProductstv;
	private ImageButton nextPage, previousPage, refresh;

	private RowData clickedItem;
	private int clickedPosition;

	private int page1Text = 1, page2Text = 2, page3Text = 3, page4Text = 4,
			page5Text = 5, currentPage = 1;
	private boolean FIRST_TIME = true, productPage = false;

	private GetProducts_initial_Controller initialThread;
	private ConnectionDetector internetConnection;

	private Context context;
	private Activity parentActivity;
	private int screenWidth, screenHeight;
	private RadioGroup filterRadioGroup;
	private int FILTER_ALL = 0, FILTER_NORTH = 1, FILTER_SOUTH = 2,
			FILTER_EAST = 3, FILTER_WEST = 4, FILTER_CENTER = 5;
	private int currentFilter = FILTER_ALL;
	String g_code = "", g_region = "";
	
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
		screenWidth = size.x;
		screenHeight = size.y;
		Static_view_info.screenWidth = screenWidth;
		Static_view_info.screenHeight = screenHeight;

		outerLayout = (RelativeLayout) parentActivity
				.findViewById(R.id.testmarkettap_outerlayout);
		filterButton = (ToggleButton) parentActivity
				.findViewById(R.id.filter_toggle);
		progressBar = (ProgressBar) parentActivity
				.findViewById(R.id.testmarkettap_progressBar1);
		listView = (ListView) parentActivity.findViewById(R.id.listView1);
		listView.setVisibility(View.INVISIBLE);

		loadMoreView = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listfooter, null, false);
		loadMoreView.setClickable(false);
		listView.addFooterView(loadMoreView);

		// create an ArrayAdaptar from the String Array
		countryList = new ArrayList<RowData>();

		// enables filtering for the contents of the given ListView
		listView.setTextFilterEnabled(true);

		page1 = (TextView) parentActivity.findViewById(R.id.product_page1);
		page2 = (TextView) parentActivity.findViewById(R.id.product_page2);
		page3 = (TextView) parentActivity.findViewById(R.id.product_page3);
		page4 = (TextView) parentActivity.findViewById(R.id.product_page4);
		page5 = (TextView) parentActivity.findViewById(R.id.product_page5);
		noProductstv = (TextView) parentActivity
				.findViewById(R.id.testmarkettap_textView1);
		previousPage = (ImageButton) parentActivity
				.findViewById(R.id.product_previouspage);
		nextPage = (ImageButton) parentActivity
				.findViewById(R.id.product_nextpage);
		previousPage.setVisibility(View.INVISIBLE);

		refresh = (ImageButton) parentActivity.findViewById(R.id.refresh_butto);

		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refresh();

			}
		});
		setupClickListeners();
		popupMenuTrans();

		// initiating the products
		initialThread = new GetProducts_initial_Controller(context, "-1", "0",
				"");
		initialThread.execute();

		initiate_products_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_initiateProducts_result(result);
			}

		};
		
		try{
			if(initiate_products_receiver != null){
				IntentFilter receiverfilter = new IntentFilter(
						Static_view_info.LOADING_PRODUCTS);
				parentActivity.registerReceiver(initiate_products_receiver,
						receiverfilter);
			}
		}catch(Exception e){}
		

		drawable_thread_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);

				try{
					dataAdapter.notifyDataSetChanged();
				}catch(Exception e){}
				
			}

		};
		
		try{
			if(drawable_thread_receiver != null){
				IntentFilter receiverfilter3 = new IntentFilter(
						Static_view_info.DRAWABLE_THREAD_BROADCAST);
				parentActivity.registerReceiver(drawable_thread_receiver,
						receiverfilter3);
			}
			
		}catch(Exception e){}
		

		getUserName_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String username = intent.getStringExtra("result");
				int flag = intent.getIntExtra("flag", -1);
				if (flag == 2)
					openProductPage(username);
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

	private void refresh() {
		progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.INVISIBLE);
		noProductstv.setVisibility(View.INVISIBLE);
		clean(false);
		// initiating the products
		//initialThread = new GetProducts_initial_Controller(context, "-1", "0","");
		
		initialThread = new GetProducts_initial_Controller(context,"-1", g_code, g_region);
		
		initialThread.execute();
	}

	@Override
	public void hide() {
		// as on pause
		if (!productPage) {
			initialThread.cancel(true);
			clean(true);
			outerLayout.setVisibility(View.GONE);
			hideMenus();
		}
	}

	@Override
	public void show() {
		// on resume
		// TODO Auto-generated method stub
		outerLayout.setVisibility(View.VISIBLE);
		if (!productPage) {
			if (!FIRST_TIME) {
				Log.d("ghazy", "onresume called");
				progressBar.setVisibility(View.VISIBLE);
				listView.setVisibility(View.INVISIBLE);
				noProductstv.setVisibility(View.INVISIBLE);
				hideMenus();
				FIRST_TIME = true;
				action = 0;
				previousPage.setVisibility(View.INVISIBLE);

				String code = "0", region = "";
				if (currentFilter == FILTER_ALL) {
					code = "0";
					action = 0;
					region = "";
				} else if (currentFilter == FILTER_NORTH) {
					code = "3";
					action = 3;
					region = "North";
				} else if (currentFilter == FILTER_SOUTH) {
					code = "3";
					action = 3;
					region = "South";
				} else if (currentFilter == FILTER_EAST) {
					code = "3";
					action = 3;
					region = "East";
				} else if (currentFilter == FILTER_WEST) {
					code = "3";
					action = 3;
					region = "West";
				} else if (currentFilter == FILTER_CENTER) {
					code = "3";
					action = 3;
					region = "Center";
				}
				
				initialThread = new GetProducts_initial_Controller(context,
						"-1", code, region);
				initialThread.execute();
			}
		} else {
			productPage = false;
			if (Static_view_info.sampleStateChanged) {
				int quantity = Integer.parseInt(countryList
						.get(clickedPosition).getQuantity());
				Static_view_info.sampleStateChanged = false;
				quantity--;
				countryList.get(clickedPosition).setQuantity(quantity + "");
				dataAdapter.notifyDataSetChanged();
			}
		}
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
					currentPage = 1;
					loadPage();
				} else {
					Toast.makeText(context,
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
					currentPage = 2;
					loadPage();
				} else {
					Toast.makeText(context,
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
					currentPage = 3;
					loadPage();
				} else {
					Toast.makeText(context,
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
					currentPage = 4;
					loadPage();
				} else {
					Toast.makeText(context,
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
					currentPage = 5;
					loadPage();
				} else {
					Toast.makeText(context,
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
					JSONObject temp;
					try {
						temp = Static_view_info.currentProducts
								.getJSONObject(0);
						String startId = temp.getString("id");
						action = 2;
						progressBar.setVisibility(View.VISIBLE);
						listView.setVisibility(View.INVISIBLE);
						noProductstv.setVisibility(View.INVISIBLE);
						hideMenus();
						String code = "2", region = "";
						if (currentFilter == FILTER_ALL) {
							code = "2";
							action = 2;
							region = "";
						} else if (currentFilter == FILTER_NORTH) {
							code = "5";
							action = 5;
							region = "North";
						} else if (currentFilter == FILTER_SOUTH) {
							code = "5";
							action = 5;
							region = "South";
						} else if (currentFilter == FILTER_EAST) {
							code = "5";
							action = 5;
							region = "East";
						} else if (currentFilter == FILTER_WEST) {
							code = "5";
							action = 5;
							region = "West";
						} else if (currentFilter == FILTER_CENTER) {
							code = "5";
							action = 5;
							region = "Center";
						}
						initialThread = new GetProducts_initial_Controller(
								context, startId, code, region);
						initialThread.execute();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(context,
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
					JSONObject temp;
					try {
						temp = Static_view_info.currentProducts
								.getJSONObject(Static_view_info.currentProducts
										.length() - 1);
						String startId = temp.getString("id");
						action = 1;
						progressBar.setVisibility(View.VISIBLE);
						hideMenus();
						listView.setVisibility(View.INVISIBLE);
						noProductstv.setVisibility(View.INVISIBLE);
						String code = "1", region = "";
						if (currentFilter == FILTER_ALL) {
							code = "1";
							action = 1;
							region = "";
						} else if (currentFilter == FILTER_NORTH) {
							code = "4";
							action = 4;
							region = "North";
						} else if (currentFilter == FILTER_SOUTH) {
							code = "4";
							action = 4;
							region = "South";
						} else if (currentFilter == FILTER_EAST) {
							code = "4";
							action = 4;
							region = "East";
						} else if (currentFilter == FILTER_WEST) {
							code = "4";
							action = 4;
							region = "West";
						} else if (currentFilter == FILTER_CENTER) {
							code = "4";
							action = 4;
							region = "Center";
						}
						initialThread = new GetProducts_initial_Controller(
								context, startId, code, region);
						initialThread.execute();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(context,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void loadPage() {
		clean(false);
		initiateListView();
		dataAdapter.notifyDataSetChanged();

	}

	private void clean(boolean all) {

		for (int i = 0; i < countryList.size(); i++) {
			Drawable currentDrawable = countryList.get(i).getDrawable();
			if (currentDrawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) currentDrawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
			currentDrawable = null;
		}
		countryList.clear();
		Static_view_info.prodcuts_drawables.clear();
		if (all) {
			// Static_view_info.currentProducts = null;
			dataAdapter = null;
		}
	}

	private void initiateListView() {
		if (Static_view_info.currentProducts != null) {
			JSONArray products_array;
			try {
				products_array = Static_view_info.currentProducts;
				int start = 0, end = 0;
				switch (currentPage) {
				case 1:
					start = 0;
					end = (products_array.length() > 10) ? 10 : products_array
							.length();
					break;
				case 2:
					start = 10;
					end = (products_array.length() > 20) ? 20 : products_array
							.length();
					break;

				case 3:
					start = 20;
					end = (products_array.length() > 30) ? 30 : products_array
							.length();
					break;
				case 4:
					start = 30;
					end = (products_array.length() > 40) ? 40 : products_array
							.length();
					break;
				case 5:
					start = 40;
					end = (products_array.length() > 50) ? 50 : products_array
							.length();
					break;
				}

				String productId = "1";
				Static_view_info.prodcuts_drawables = new ArrayList<Drawable>();
				
				for (int i = start; i < end; i++) {
					JSONObject temp = products_array.getJSONObject(i);
					String description = temp.getString("Description");
					String quantity = temp.getString("Quantity");
					String date = temp.getString("CollectionDate");
					String time = temp.getString("CollectionTime");
					String endDate = temp.getString("endDate");
					String endTime = temp.getString("endTime");
					String region = temp.getString("region");
					String address = temp.getString("CollectionAddress");
					String userId = temp.getString("UserId");
					productId = temp.getString("id");

					RowData rowData = new RowData(productId, description, time,
							date, endDate, endTime, region, address, quantity,
							userId, "false", null/* drawable */);
					countryList.add(rowData);

				}
				if (FIRST_TIME) {
					FIRST_TIME = false;
				}
				dataAdapter = new ListAdapter(context, R.layout.cell6,
						countryList);
				listView.setAdapter(dataAdapter);
				// Loading picture within AsynTask Method
				new DrawablesLoader(context, countryList, dataAdapter)
						.execute();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// listView.setOnScrollListener(new EndlessScrollListener());

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if (internetConnection.isConnectingToInternet()) {
					clickedItem = countryList.get(position);
					String uploaderUser = clickedItem.getUserId();
					clickedPosition = position;
					new GetUserName_Controller(new ProgressDialog(context),
							context, uploaderUser, 2).execute();
				} else {
					Toast.makeText(context,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void handle_initiateProducts_result(int result) {
		if (result == 1) {
			noProductstv.setVisibility(View.GONE);
			if (action == 1) {
				action = 0;
				page1Text += 5;
				page2Text += 5;
				page3Text += 5;
				page4Text += 5;
				page5Text += 5;

				previousPage.setVisibility(View.VISIBLE);
				clean(true);
			} else if (action == 2) {
				action = 0;
				page1Text -= 5;
				page2Text -= 5;
				page3Text -= 5;
				page4Text -= 5;
				page5Text -= 5;
				currentPage = 1;
				nextPage.setVisibility(View.VISIBLE);
			}
			page1.setVisibility(View.VISIBLE);
			page2.setVisibility(View.VISIBLE);
			page3.setVisibility(View.VISIBLE);
			page4.setVisibility(View.VISIBLE);
			page5.setVisibility(View.VISIBLE);
			nextPage.setVisibility(View.VISIBLE);
			previousPage.setVisibility(View.VISIBLE);
			page1.setTextColor(Color.RED);
			page2.setTextColor(Color.BLACK);
			page3.setTextColor(Color.BLACK);
			page4.setTextColor(Color.BLACK);
			page5.setTextColor(Color.BLACK);
			clean(true);
			progressBar.setVisibility(View.INVISIBLE);
			listView.setVisibility(View.VISIBLE);
			try {
				int length = Static_view_info.currentProducts.length();
				if (length <= 10) {
					page2.setVisibility(View.INVISIBLE);
					page3.setVisibility(View.INVISIBLE);
					page4.setVisibility(View.INVISIBLE);
					page5.setVisibility(View.INVISIBLE);
					nextPage.setVisibility(View.INVISIBLE);
				} else if (length <= 20) {
					page3.setVisibility(View.INVISIBLE);
					page4.setVisibility(View.INVISIBLE);
					page5.setVisibility(View.INVISIBLE);
					nextPage.setVisibility(View.INVISIBLE);
				} else if (length <= 30) {
					page4.setVisibility(View.INVISIBLE);
					page5.setVisibility(View.INVISIBLE);
					nextPage.setVisibility(View.INVISIBLE);
				} else if (length <= 40) {
					page5.setVisibility(View.INVISIBLE);
					nextPage.setVisibility(View.INVISIBLE);
				} else if (length < 50) {
					nextPage.setVisibility(View.INVISIBLE);
				}
				currentPage = 1;
				initiateListView();
				dataAdapter.notifyDataSetChanged();
			} catch (OutOfMemoryError e) {
				Log.d("ghazy", "here");
			}
		} else {
			if (action == 2) {
				previousPage.setVisibility(View.INVISIBLE);
				currentPage = 1;
				page1.setTextColor(Color.RED);
				page2.setTextColor(Color.BLACK);
				page3.setTextColor(Color.BLACK);
				page4.setTextColor(Color.BLACK);
				page5.setTextColor(Color.BLACK);
				loadPage();
				action = 0;
				progressBar.setVisibility(View.INVISIBLE);
				// noProductstv.setVisibility(View.VISIBLE);
				listView.setVisibility(View.VISIBLE);
			}
			if (action == 1) {
				nextPage.setVisibility(View.INVISIBLE);
				action = 0;
				progressBar.setVisibility(View.INVISIBLE);
				// noProductstv.setVisibility(View.VISIBLE);
				listView.setVisibility(View.VISIBLE);
			}

			if (action == 3 ) {
				progressBar.setVisibility(View.INVISIBLE);
				listView.setVisibility(View.INVISIBLE);
				noProductstv.setVisibility(View.VISIBLE);
			}
//			if (action == 5) {
//				previousPage.setVisibility(View.INVISIBLE);
//				currentPage = 1;
//				page1.setTextColor(Color.RED);
//				page2.setTextColor(Color.BLACK);
//				page3.setTextColor(Color.BLACK);
//				page4.setTextColor(Color.BLACK);
//				page5.setTextColor(Color.BLACK);
//				loadPage();
//				action = 3;
//				progressBar.setVisibility(View.INVISIBLE);
//				// noProductstv.setVisibility(View.VISIBLE);
//				listView.setVisibility(View.VISIBLE);
//			}
//			if (action == 4) {
//				nextPage.setVisibility(View.INVISIBLE);
//				action = 3;
//				progressBar.setVisibility(View.INVISIBLE);
//				// noProductstv.setVisibility(View.VISIBLE);
//				listView.setVisibility(View.VISIBLE);
//			}

		}
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
		Intent intent = new Intent(context, ProductPageActivity.class);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		intent.putExtra("description", description);
		intent.putExtra("address", address);
		intent.putExtra("username", username);
		intent.putExtra("date", date);
		intent.putExtra("time", time);
		intent.putExtra("endDate", endDate);
		intent.putExtra("endTime", endTime);
		intent.putExtra("region", region);
		intent.putExtra("quantity", quantity);
		intent.putExtra("productId", productId);
		productPage = true;
		context.startActivity(intent);
		//parentActivity.finish();
	}

	private void popupMenuTrans() {

		ViewGroup displayWarpper = (ViewGroup) parentActivity
				.findViewById(R.id.homepage_layout);

		filterMenu = new FilterActionMenu(displayWarpper, filterButton,
				R.id.homepage_tabbar, screenWidth, screenHeight);
		filterRadioGroup = filterMenu.setRadioGroup();
		filterRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup rGroup,
							int checkedId) {

						progressBar.setVisibility(View.VISIBLE);
						hideMenus();
						listView.setVisibility(View.INVISIBLE);
						noProductstv.setVisibility(View.INVISIBLE);
						String region = "", code = "";
						switch (checkedId) {
						case R.id.radio0:
							region = "";
							code = "0";
							action = 0;
							currentFilter = FILTER_ALL;
							break;
						case R.id.radio1:
							region = "North";
							code = "3";
							action = 3;
							currentFilter = FILTER_NORTH;
							break;

						case R.id.radio2:
							region = "South";
							code = "3";
							action = 3;
							currentFilter = FILTER_SOUTH;
							break;
						case R.id.radio3:
							region = "East";
							code = "3";
							action = 3;
							currentFilter = FILTER_EAST;
							break;
						case R.id.radio4:
							region = "West";
							code = "3";
							action = 3;
							currentFilter = FILTER_WEST;
							break;
						case R.id.radio5:
							region = "Center";
							code = "3";
							action = 3;
							currentFilter = FILTER_CENTER;
							break;

						}
						// initiating the products
						g_code = code;
						g_region = region;
						initialThread = new GetProducts_initial_Controller(
								context, "-1", code, region);
						initialThread.execute();
						hideMenus();
					}
				});
	}

	private void hideMenus() {

		filterMenu.hide();
		filterButton.setChecked(false);

	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
