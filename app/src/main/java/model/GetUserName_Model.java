package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class GetUserName_Model {

	public static String execute(String userId) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(Static_Info.url_getUserName,
				"POST", params);

		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 1) {
				String mysamples = json.getString("name");
				return mysamples;
			} else
				return "UNKNOWN"; // user not found
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "UNKNOWN";
	}



}
