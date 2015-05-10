package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckSampledModel {

	public static int execute(String userId, String productId) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();
		String[] r = { "error" };
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair("productid", productId));
		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_check_sampled, "POST", params);

		// check for success tag
		try {
			int found = json.getInt("found");
			return found;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
