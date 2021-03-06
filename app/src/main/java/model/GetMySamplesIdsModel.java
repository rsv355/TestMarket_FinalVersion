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


public class GetMySamplesIdsModel {

	public static int execute(int userid) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", "" + userid));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_getMySamplesIDs, "POST", params);

		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 1) {
				// log in succeed
				JSONArray idsArray = json.getJSONArray("samplesIDs");
				JSONArray redeemArray = json.getJSONArray("samplesredeem");

				Static_view_info.idsSize = idsArray.length();
				String ids = idsArray.getString(0);
				String redeems = redeemArray.getString(0);
				for (int i = 1; i < idsArray.length(); i++) {
					ids += " " + idsArray.getString(i);
					redeems += " " + redeemArray.getString(i);
				}
				Static_view_info.mySamplesIds = ids;
				Static_view_info.mySamplesredeem = redeems;
				return 1;
			} else if (success == 2) {
				// There is no samples
				Static_view_info.mySamplesIds = "null";
				return 2;
			}
			return 0; // user not found
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;

	}

}
