package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class Upload_Model {

	public static int createProduct(String description, String quantity,
			String address, String date, String time, String endDate,
			String endTime, String region, String email, String imageURL) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("imageURL", imageURL));
		params.add(new BasicNameValuePair("quantity", quantity));
		params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("address", address));
		params.add(new BasicNameValuePair("date", date));
		params.add(new BasicNameValuePair("time", time));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("endTime", endTime));
		params.add(new BasicNameValuePair("endDate", endDate));
		params.add(new BasicNameValuePair("region", region));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_upload_product, "POST", params);

		// check for success insert
		try {

			String result = json.getString("productId");
			int success = json.getInt("success");
			if (success == 1) {
				// insert succeed
				return Integer.parseInt(result);
			} else {
				return -1; // insert failed
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// success
		return -2;

	}

}
