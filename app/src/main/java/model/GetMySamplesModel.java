package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;


import android.util.Log;

public class GetMySamplesModel {

	public static int execute(String ids) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ids", "" + ids));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_getMySamples, "POST", params);

		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 1) {
				// log in succeed
				JSONArray mysamples = json.getJSONArray("mysamples");
				Static_view_info.currentMySamples = mysamples;
				return 1;
			}
			return 0; // user not found
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;

	}

}
