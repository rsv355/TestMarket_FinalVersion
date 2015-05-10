package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateProfile_Model {

	public static int execute(String userId, String email,
			String password, String name, String contact, String location,
			String birthday, String gender) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("contact", contact));
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("birthday", birthday));
		params.add(new BasicNameValuePair("gender", gender));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_update_profile, "POST", params);

		// check for success insert
		try {

			int success = json.getInt("success");
			if (success == 1) {
				// insert succeed
				return 1;
			} else {
				return -1; // insert failed
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// success
		return -1;

	}

}
