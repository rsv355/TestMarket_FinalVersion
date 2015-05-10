package com.example.testmarket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmarket.R;

import controller.ConnectionDetector;
import controller.FacebookLogin_Controller;
import controller.Signin_Controller;

public class SignInActivity extends Activity {

	private EditText emailEditText;
	private EditText passwordEditText;
	private TextView direct_signup;
	private Button signIn;
	private String email1;
	private String password1;
	private BroadcastReceiver signIn_receiver;
	private ConnectionDetector internet_connection;

	// for facebook login
	private String Username, Bday, email, gender, country, password, contact;
//	private LoginButton button_fb;
	private boolean login = false;
	// SocialAuth Component
	SocialAuthAdapter adapter;
	Profile profileMap;
	Button fb_login_button;
	String providerName;
	
	// dimensions
	private WindowManager wmanager;
	private Display display;
	private int imageWidth, imageHeight;
	Global_access ga;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_signin);

		ga = new Global_access();
		
		wmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Point size = new Point();
		display = wmanager.getDefaultDisplay();
		display.getSize(size);
		imageWidth = (int) (1.5 * size.x / 2.5);
		imageHeight = (int) (size.y / 3);

		
		if(ga.loadSavedPreferences(ga.TAG_LOGIN, SignInActivity.this)){
			Static_view_info.userId = Integer.parseInt(ga.loadSavedPreferences_string(ga.TAG_USER_ID, SignInActivity.this));
			Intent intent = new Intent(SignInActivity.this,
					HomePageActivity.class);
			intent.putExtra("email", ga.loadSavedPreferences_string(ga.TAG_EMAIL, SignInActivity.this));

			startActivity(intent);
			finish();
		}
		
		
	//	setupFacebookButton();
		adapter = new SocialAuthAdapter(new ResponseListener());
		fb_login_button = (Button) findViewById(R.id.fb_login_button);
		
		internet_connection = new ConnectionDetector(getApplicationContext());
		emailEditText = (EditText) findViewById(R.id.email);
		passwordEditText = (EditText) findViewById(R.id.password);
		signIn = (Button) findViewById(R.id.signin_button);
		direct_signup = (TextView) findViewById(R.id.signup_redirect_to_signup);

		ImageView logo = (ImageView) findViewById(R.id.logoo);
		ImageView emialIcon = (ImageView) findViewById(R.id.imageView3);
		ImageView passIcon = (ImageView) findViewById(R.id.imageView2);
		ViewGroup.LayoutParams params11 = logo.getLayoutParams();
		params11.width = imageWidth;
		params11.height = imageHeight;
		logo.setLayoutParams(params11);

		Button fbLogin = (Button) findViewById(R.id.fb_login_button);
		ViewGroup.LayoutParams params1 = fbLogin.getLayoutParams();
		params1.width = imageWidth;
		params1.height = imageHeight / 5;
		fbLogin.setLayoutParams(params1);
		signIn.setLayoutParams(params1);

		ViewGroup.LayoutParams params3 = emialIcon.getLayoutParams();
		params3.width = imageWidth / 6;
		params3.height = imageHeight / 6;
		emialIcon.setLayoutParams(params3);
		passIcon.setLayoutParams(params3);

		ViewGroup.LayoutParams params2 = emailEditText.getLayoutParams();
		params2.width = imageWidth;
		params2.height = imageHeight / 6;
		emailEditText.setLayoutParams(params2);
		passwordEditText.setLayoutParams(params2);

		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				signinClicked();
			}
		});

		direct_signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SignInActivity.this,
						SignupActivity.class));

			}
		});
		signIn_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				int facebookFlag = intent.getIntExtra("facebook", 0);
				handle_signIn_result(result, facebookFlag);
			}

		};
		
		try{
			IntentFilter receiverfilter = new IntentFilter(
					Static_view_info.SIGN_IN_BROADCAST);
			registerReceiver(signIn_receiver, receiverfilter);
		}catch(Exception e){}
		
		
		
		
		fb_login_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				adapter.authorize(SignInActivity.this, Provider.FACEBOOK);
				
				/*if(fb_login_button.getText().toString().equals("Logout")){
					boolean status = adapter.signOut(SignInActivity.this, providerName);
					fb_login_button.setText("Login");
				}else{
					
				}*/				
			}
		});
	}

	@Override
	protected void onStop() {
		try{
			unregisterReceiver(signIn_receiver);
		}catch(Exception e){}
		super.onStop();
	}
	private void setupFacebookButton() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.example.testmarket", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("ghazy",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
/*
		button_fb = (LoginButton) findViewById(R.id.fb_login_button);
		button_fb.setReadPermissions(Arrays.asList("basic_info",
				"user_birthday", "email", "user_friends", "user_location"));
		button_fb.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Session.openActiveSession(SignInActivity.this, true,
						new Session.StatusCallback() {

							@SuppressWarnings("deprecation")
							@Override
							public void call(final Session session,
									SessionState state, Exception exception) {
								if (session.isOpened()) {
									// Make request to the /me API
									Request.executeMeRequestAsync(session,
											new Request.GraphUserCallback() {
												// Callback after Graph API
												// response with user object

												@Override
												public void onCompleted(
														GraphUser user,
														Response response) {
													if (user != null) {
														Username = user
																.getName();
														Bday = user
																.getBirthday();
														email = (String) user
																.getProperty("email");
														gender = (String) user
																.getProperty("gender");
														String countryArr[] = user
																.getLocation()
																.getName()
																.split(" ");
														country = countryArr[1];
														password = user.getId()
																+ email;
														contact = "--------";
														facebookLoging(email,
																password,
																Username,
																contact,
																country, Bday,
																gender);
														Log.d("ghazy", email);
														Log.d("ghazy", password);
														Log.d("ghazy", Username);
														Log.d("ghazy", Bday);
														Log.d("ghazy", gender);
														Log.d("ghazy", country);
														Log.d("ghazy", contact);
													}
												}
											});
								}
							}
						});
				// logout();

			}
		});*/
	}

	private void logout() {
//		Session session = Session.getActiveSession();
//		session.closeAndClearTokenInformation();
	}

	private void signinClicked() {
		if (internet_connection.isConnectingToInternet()) {
			email1 = emailEditText.getText().toString();
			password1 = passwordEditText.getText().toString();
			new Signin_Controller(new ProgressDialog(SignInActivity.this),
					SignInActivity.this, email1, password1).execute();
		} else {
			Toast.makeText(this, "Your are not connected to the Internet",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void handle_signIn_result(int result, int facebookFlag) {
		
		switch (result) {
		case 1:// Success
			if (facebookFlag == 0) {
				// Toast.makeText(this, "You are signed in Successfully",
				// Toast.LENGTH_LONG).show();
				// new GetProducts_initial_Controller(new
				// ProgressDialog(SignInActivity.this),SignInActivity.this).execute();
				
				ga.savePreferences(ga.TAG_LOGIN, true, SignInActivity.this);
				ga.savePreferences(ga.TAG_LOGIN_TYPE, ga.TAG_DIRECT_LOGIN, SignInActivity.this);
				ga.savePreferences(ga.TAG_EMAIL, email1, SignInActivity.this);
				ga.savePreferences(ga.TAG_USER_ID, String.valueOf(Static_view_info.userId), SignInActivity.this);
				
				Intent intent = new Intent(SignInActivity.this,
						HomePageActivity.class);
				intent.putExtra("email", email1);

				startActivity(intent);
				finish();
			} else {
				
				ga.savePreferences(ga.TAG_LOGIN, true, SignInActivity.this);
				ga.savePreferences(ga.TAG_LOGIN_TYPE, ga.TAG_FB_LOGIN, SignInActivity.this);
				ga.savePreferences(ga.TAG_EMAIL, email, SignInActivity.this);
				ga.savePreferences(ga.TAG_USER_ID, String.valueOf(Static_view_info.userId), SignInActivity.this);
				
				Toast.makeText(this,
						"You are signed up Successfully with facebook",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(SignInActivity.this,
						HomePageActivity.class));
				finish();
			}
			break;

		case 0:// wrong email or password
			if (facebookFlag == 0)
				Toast.makeText(this, "Wrong email or password",
						Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, "Failed to login with facebook",
						Toast.LENGTH_LONG).show();
			break;
		case 2:
			// signed in with facebook
			if (facebookFlag == 1) {
				
				ga.savePreferences(ga.TAG_LOGIN, true, SignInActivity.this);
				ga.savePreferences(ga.TAG_LOGIN_TYPE, ga.TAG_FB_LOGIN, SignInActivity.this);
				ga.savePreferences(ga.TAG_EMAIL, email, SignInActivity.this);
				ga.savePreferences(ga.TAG_USER_ID, String.valueOf(Static_view_info.userId), SignInActivity.this);
				
				Toast.makeText(this,
						"You are signed in Successfully with facebook",
						Toast.LENGTH_SHORT).show();
				startActivity(new Intent(SignInActivity.this,
						HomePageActivity.class));
				finish();
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	/*	super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);*/
	}

	private void facebookLoging(String email, String password, String name,
			String contact, String location, String birthday, String gender) {

		String[] date = birthday.split("/");
		birthday = date[2] + "-" + date[1] + "-" + date[0];
		new FacebookLogin_Controller(new ProgressDialog(SignInActivity.this),
				SignInActivity.this, email, password, name, contact, location,
				birthday, gender).execute();
	}
	
	private final class ResponseListener implements DialogListener 
	{
	   public void onComplete(Bundle values) {
	    
		 //  adapter.getUserProfileAsync(new ProfileDataListener());  
		// Variable to receive message status
					Log.d("Share-Bar", "Authentication Successful");

					// Get name of provider after authentication
					providerName = values.getString(SocialAuthAdapter.PROVIDER);
					Log.d("Share-Bar", "Provider Name = " + providerName);
					//Toast.makeText(SignInActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();
					
					ga.savePreferences(ga.TAG_PROVIDER, providerName, SignInActivity.this);
					adapter.getUserProfileAsync(new ProfileDataListener());
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
	
	// To receive the profile response after authentication
	private final class ProfileDataListener implements SocialAuthListener<Profile> {

		@Override
		public void onExecute(String provider, Profile t) {

			Log.d("Custom-UI", "Receiving Data");
			
			Profile profileMap = t;

			Log.d("Custom-UI", "Validate ID = " + profileMap.getValidatedId());
			Log.d("Custom-UI", "First Name  = " + profileMap.getFirstName());
			Log.d("Custom-UI", "Last Name   = " + profileMap.getLastName());
			Log.d("Custom-UI", "Email       = " + profileMap.getEmail());
			Log.d("Custom-UI", "Gender  	 = " + profileMap.getGender());
			Log.d("Custom-UI", "Country  	 = " + profileMap.getCountry());
			Log.d("Custom-UI", "Language  	 = " + profileMap.getLanguage());
			Log.d("Custom-UI", "Location 	 = " + profileMap.getLocation());
			Log.d("Custom-UI", "Profile Image URL  = " + profileMap.getProfileImageURL());
			
			//fb_login_button.setText("Logout");
			
			Username = profileMap.getFullName();
			Bday = ""+profileMap.getDob();
			email = profileMap.getEmail();
			gender = profileMap.getGender();
			country = profileMap.getCountry();
			password = profileMap.getValidatedId() + email;
			contact = "--------";
			
			facebookLoging(email, password, Username, contact, country, Bday, gender);
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}	
	
	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory( Intent.CATEGORY_HOME );
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
		SignInActivity.this.finish();
	}
	
}
