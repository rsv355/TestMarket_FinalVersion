package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateListingsItem_Model {

	public static int execute(String productId, String description,
			String address, String date, String Time, String region, String endDate,
			String endTime, String quantity) {
		


		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", productId));
		params.add(new BasicNameValuePair("Quantity", quantity));
		params.add(new BasicNameValuePair("Description", description));
		params.add(new BasicNameValuePair("CollectionDate", date));
		params.add(new BasicNameValuePair("CollectionAddress", address));	
		params.add(new BasicNameValuePair("CollectionTime", Time));
		params.add(new BasicNameValuePair("endDate", endDate));
		params.add(new BasicNameValuePair("endTime", endTime));
		params.add(new BasicNameValuePair("region", region));
		
		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_update_Listing, "POST", params);

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
