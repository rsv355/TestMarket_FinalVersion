package com.example.testmarket;

import java.io.IOException;
import java.util.ArrayList;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import com.example.testmarket.R;

import android.R.drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HomePageActivity extends Activity {

	private RelativeLayout tabBarLayout;
	private ImageButton cartButton, cameraButton;
	private ToggleButton humanheadButton;
	private QuickActionMenu humanHeadMenu;
	private ListView transList;
	protected String[] transListItems = { "My Samples", "My Listings",
			"Profile", "Logout" };

	private WindowManager wmanager;
	private Display display;
	private int screenHeight, screenWidth;

	private UploadPanel uploadPanel;
	private MarketPlacePanel marketPlacePanel;
	private MySamplesPanel mySamplesPanel;
	private MyListingsPanel myListingsPanel;
	private MyProfilePanel myProfilePanel;

	private int MARKET_PLACE = 1, UPLOAD = 2, PFROFILE = 3, MYSAMPLES = 4,
			MYLISTING = 5;
	private int currentPage = MARKET_PLACE;
	ArrayList<String> page_list = new ArrayList<String>();
	
	SocialAuthAdapter adapter;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		screenHeight = size.y;
		screenWidth = size.x;

		setupBar();

		uploadPanel = new UploadPanel();
		uploadPanel.onCreate(this, this);
		marketPlacePanel = new MarketPlacePanel();
		marketPlacePanel.onCreate(this, this);
		mySamplesPanel = new MySamplesPanel();
		mySamplesPanel.onCreate(this, this);
		myListingsPanel = new MyListingsPanel();
		myListingsPanel.onCreate(this, this);
		myProfilePanel = new MyProfilePanel();
		myProfilePanel.onCreate(this, this);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
	}
		
	private void popupMenuTrans() {
		int position = -1;
		ViewGroup displayWarpper = (ViewGroup) findViewById(R.id.homepage_layout);

		humanHeadMenu = new QuickActionMenu(displayWarpper, humanheadButton,
				R.id.homepage_tabbar);
		humanHeadMenu.setMenuWidth(200);
		humanHeadMenu.setMenuHeight(190);
		humanHeadMenu.setSelectedItem(position);
		transList = humanHeadMenu.setList(transListItems, "transList");

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				page_list.add(""+currentPage);
				hideMenus();
				uploadPanel.hide();
				myProfilePanel.hide();
				marketPlacePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				if (position == 0) {
					currentPage = MYSAMPLES;
					mySamplesPanel.show();
				} else if (position == 1) {
					currentPage = MYLISTING;
					myListingsPanel.show();
				} else if (position == 2){
					currentPage = PFROFILE;
					myProfilePanel.show();
				}else{
					logout();
				}
				
			}
		});
	}

	private void logout(){
		Global_access ga = new Global_access();
		
		ga.savePreferences(ga.TAG_LOGIN, false, HomePageActivity.this);
		ga.removePreferences(ga.TAG_LOGIN_TYPE, HomePageActivity.this);
		ga.removePreferences(ga.TAG_EMAIL, HomePageActivity.this);
		ga.removePreferences(ga.TAG_USER_ID, HomePageActivity.this);
		
		Intent in = new Intent(HomePageActivity.this, SignInActivity.class);
		startActivity(in);
		finish();
		
	}
	
	private void hideMenus() {

		humanHeadMenu.hide();
		humanheadButton.setChecked(false);

	}

	private void setupBar() {
		tabBarLayout = (RelativeLayout) findViewById(R.id.homepage_tabbar);
		humanheadButton = (ToggleButton) findViewById(R.id.humanhead_button);
		cameraButton = (ImageButton) findViewById(R.id.camera_button);
		cartButton = (ImageButton) findViewById(R.id.cart_button);

		ViewGroup.LayoutParams params1 = tabBarLayout.getLayoutParams();
		params1.height = screenHeight / 12;
		tabBarLayout.setLayoutParams(params1);

		ViewGroup.LayoutParams params2 = humanheadButton.getLayoutParams();
		params2.width = screenWidth / 8;
		params2.height = screenHeight / 14;
		humanheadButton.setLayoutParams(params2);

		ViewGroup.LayoutParams params3 = cartButton.getLayoutParams();
		params3.width = screenWidth / 7;
		params3.height = screenHeight / 14;
		cartButton.setLayoutParams(params3);

		ViewGroup.LayoutParams params4 = cameraButton.getLayoutParams();
		params4.width = screenWidth / 8;
		params4.height = screenHeight / 14;
		cameraButton.setLayoutParams(params4);

		popupMenuTrans();

		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				page_list.add(""+currentPage);
				hideMenus();
				marketPlacePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				myProfilePanel.hide();
				uploadPanel.show();
				currentPage = UPLOAD;
				
			}
		});

		cartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				page_list.add(""+currentPage);
				hideMenus();
				uploadPanel.hide();
				myProfilePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				marketPlacePanel.show();
				currentPage = MARKET_PLACE;
				
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == 1 || requestCode == 2 || requestCode == 4 || requestCode == 5)
				&& resultCode == RESULT_OK) {
			uploadPanel.onActivityResult(requestCode, resultCode, data);
		}
		else if(requestCode == 6 && resultCode == RESULT_OK){
			myProfilePanel.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (currentPage == MARKET_PLACE) {
			marketPlacePanel.hide();
		} else if (currentPage == MYSAMPLES) {
			mySamplesPanel.un_reg();
			//mySamplesPanel.hide();
		} else if (currentPage == MYLISTING) {
			myListingsPanel.un_reg();
			//myListingsPanel.hide();
		}
		overridePendingTransition(R.anim.slide_left_show,
				R.anim.slide_left_hide);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (currentPage == MARKET_PLACE) {
			marketPlacePanel.show();
		} else if (currentPage == MYSAMPLES) {
			mySamplesPanel.reg();
			//mySamplesPanel.show();
		} else if (currentPage == MYLISTING) {
			myListingsPanel.reg();
		//	myListingsPanel.show();
		}
	}
	
	@Override
	public void onBackPressed() {
		
		if(page_list.isEmpty()){
			Intent homeIntent = new Intent(Intent.ACTION_MAIN);
			homeIntent.addCategory( Intent.CATEGORY_HOME );
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
			finish();
			HomePageActivity.this.finish();
		}else{
			
			int last_index = page_list.size();
			int page_id = Integer.parseInt(page_list.get(last_index-1));
			//int page_id = page_index;
			
			currentPage = page_id;
			System.out.println("currentPage = "+ currentPage);
			page_list.remove(last_index-1);
			
			if(page_id == MARKET_PLACE){
				hideMenus();
				uploadPanel.hide();
				myProfilePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				marketPlacePanel.show();
			}
			
			if(page_id == MYSAMPLES){
				hideMenus();
				uploadPanel.hide();
				myProfilePanel.hide();
				marketPlacePanel.hide();
				myListingsPanel.hide();
				mySamplesPanel.show();
			}

			if(page_id == MYLISTING){
				hideMenus();
				uploadPanel.hide();
				myProfilePanel.hide();
				marketPlacePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.show();
			}
			
			if(page_id == PFROFILE){
				hideMenus();
				uploadPanel.hide();
				marketPlacePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				myProfilePanel.show();
			}
			
			if(page_id == UPLOAD){
				hideMenus();
				marketPlacePanel.hide();
				mySamplesPanel.hide();
				myListingsPanel.hide();
				myProfilePanel.hide();
				uploadPanel.show();
			}
			
		}
		
		
	}
	
	private final class ResponseListener implements DialogListener 
	{
	   public void onComplete(Bundle values) {
					Log.d("Share-Bar", "Authentication Successful");
					String providerName = values.getString(SocialAuthAdapter.PROVIDER);
	   }
	   
		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}
	
}
