package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testmarket.Static_view_info;


public class FacebookLogin_Model {

	public static int createUser(String email, String password, String name,
			String contact, String location, String birthday, String gender) {

		JSONParser jsonParser;
		jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", encrypt_md5(password)));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("contact", contact));
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("birthday", birthday));
		params.add(new BasicNameValuePair("gender", gender));

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(
				Static_Info.url_facebook_login, "POST", params);

		// check for success tag
		try {
			int success = json.getInt("success");
			if (success == 2) {
				// log in succeed
				int userId = json.getInt("id");
				Static_view_info.userId = userId;
				return 2;
			} else if (success == 1) {
				// sign up
				int userId = json.getInt("id");
				Static_view_info.userId = userId;
				return 1;
			} else
				return 0; // failed

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// success
		return -1;

	}

	private final static String encrypt_md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}
