package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;

public class GetProducts_initial_Model {
	public static int check(String startId, String action, String region) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("number_of_rows", ""
				+ Static_view_info.MAX_PRODUCTS));

		params.add(new BasicNameValuePair("userId", ""
				+ Static_view_info.userId));
		params.add(new BasicNameValuePair("startId", startId));
		params.add(new BasicNameValuePair("action", action));
		params.add(new BasicNameValuePair("region", region));
		params.add(new BasicNameValuePair("currentDate","" ));
		// getting JSON Object
		// Note that create product url accepts POST method
		
		System.out.println("params = "+ params);
		
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_initial_products, "POST", params);

		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 1) {
				// operation suceeded
				Static_view_info.currentProducts = json
						.getJSONArray("products");
				return 1;
			} else
				return 0; // products not found
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;

	}
}
