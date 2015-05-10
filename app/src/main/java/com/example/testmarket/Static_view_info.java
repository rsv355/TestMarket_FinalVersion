package com.example.testmarket;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Static_view_info {

	public static boolean signIn = false;

	// Broadcasts keys
	public static String SIGN_IN_BROADCAST = "SIGN_IN_BROADCAST";
	public static String SIGN_UP_BROADCAST = "SIGN_UP_BROADCAST";
	public static String Uploaded_BROADCAST = "Uploaded_BROADCAST";
	public static String LOADING_PRODUCTS = "LOADING_PRODUCTS_FINISHED";
	public static String MYSAMPLESIDS_BROADCAST = "MYSAMPLESIDS_BROEADCAST";
	public static String MYSAMPLES_BROADCAST = "MYSAMPLES_BROADCAST";
	public static String MYLISTINGSIDS_BROADCAST = "MYLISTINGSIDS_BROEADCAST";
	public static String MYLISTINGS_BROADCAST = "MYLISTINGS_BROADCAST";
	public static String GETUSERNAME_BROADCAST = "GETUSERNAME_BROADCAST";
	public static String SAMPLE_BROADCAST = "SAMPLE_BROADCAST";
	public static String REDEM_BROADCAST = "REDEM_BROADCAST";
	public static String DRAWABLE_THREAD_BROADCAST = "DRAWABLE_THREAD_BROADCAST";
	public static String PROFILE_BROADCAST = "PROFILE_BROADCAST";
	public static String CHECK_SAMPLED_BROADCAST = "CHECK_SAMPLED_BROADCAST";
	public static String UPDATE_PROFILE_BROADCAST = "UPDATE_PROFILE_BROADCAST";
	public static String UPDATE_MyLISTING_PRODUCT_UPDATE_BROADCAST = "UPDATE_MyLISTING_PRODUCT_UPDATE_BROADCAST";
	public static String UPDATE_LISTING_BROADCAST = "UPDATE_LISTING_BROADCAST";

	// ------------------------------
	public static int userId;
	// test market place settings
	public static int screenWidth, screenHeight;
	public static JSONArray currentProducts;
	public static int startingProduct;
	public static int MAX_PRODUCTS = 50;
	public static ArrayList<Drawable> prodcuts_drawables = new ArrayList<Drawable>();
	// -------------------------------
	// My Samples tab settings
	public static String mySamplesIds = "null";
	public static String mySamplesredeem = "null";
	public static int MAX_MYSAMPLES = 10;
	public static JSONArray currentMySamples;
	public static int idsIndex;
	public static int idsSize;
	public static ArrayList<Drawable> mysamples_drawables = new ArrayList<Drawable>();
	public static boolean sampleStateChanged = true;

	// -------------------------------
	// My Listings tab settings
	public static String myListingsIds = "null";
	public static String myListingsredeem = "null";
	public static int MAX_MYLISTINGS = 10;
	public static JSONArray currentMyListings;
	public static int idsIndex_listings;
	public static int idsSize_listings;
	public static ArrayList<Drawable> mylistings_drawables = new ArrayList<Drawable>();
	public static boolean sampleStateChanged_listings = true;
	// ------------------------------------------
	// My profile
	public static JSONArray profileInfo;
	public static Drawable profilePic;

}
