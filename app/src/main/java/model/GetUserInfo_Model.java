package model;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



public class GetUserInfo_Model {

	public static String[] execute(String userId) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();
		String[] r= {"error"};
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", userId));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(Static_Info.url_profile_info,
				"POST", params);
		System.out.println("json = "+ json);
		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 1) {
				String name = json.getString("name");
				String location = json.getString("location");
				String email = json.getString("email");
				String contact = json.getString("contact");
				String gender = json.getString("gender");
				String birthday = json.getString("birthday");
				String [] result={name,location,email,contact,gender,birthday};
				return result;
			} else
				return r; // user not found
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return r;
	}



}
