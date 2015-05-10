package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class SamplePage_Model {

	public static int createSample(String description, String userId) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("userId", userId));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_sample_product, "POST", params);

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
