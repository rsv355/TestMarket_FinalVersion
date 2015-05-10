package com.example.testmarket;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import com.example.testmarket.R;

import controller.ConnectionDetector;
import controller.Signup_Controller;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SignupActivity extends Activity {

	private EditText nameEiditText, emailEiditText, passwordEiditText,
			contactEiditText;

	private Spinner countrySpinner, birthdaySpinner, birthmonthSpinner,
			birthyearSpinner, genderSpinner;

	private Button signupButton;
	ImageButton back_button;

	private String[] genderList, dayList, monthList, yearList, countryList;
	private String selected_gender = "Female", selected_day = "01",
			selected_month = "01", selected_year = "1992",
			selected_country = "North";

	private BroadcastReceiver signUp_receiver;
	private ConnectionDetector internetConnection;
	private String registrationCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_signup);

		internetConnection = new ConnectionDetector(this);
		defineEditTexts();
		setupGenderSpinner();
		setupDaySpinner();
		setupMonthSpinner();
		setupYearSpinner();
		setupCountrySpinner();
		signupButton = (Button) findViewById(R.id.signup_button);
		signupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (internetConnection.isConnectingToInternet()) {
					signupClicked();
				} else {
					Toast.makeText(SignupActivity.this,
							"Your are not connected to the Internet",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		signUp_receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra("result", -1);
				handle_signIn_result(result);
			}

		};
		
		try{
			IntentFilter receiverfilter = new IntentFilter(
					Static_view_info.SIGN_UP_BROADCAST);
			registerReceiver(signUp_receiver, receiverfilter);
		}catch(Exception e){}
		
		
		back_button = (ImageButton) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onStop() {
		try{
			unregisterReceiver(signUp_receiver);
		}catch(Exception e){}
		super.onStop();
	}
	
	private void signupClicked() {
		String email = emailEiditText.getText().toString();
		String password = passwordEiditText.getText().toString();
		String name = nameEiditText.getText().toString();
		String contact = contactEiditText.getText().toString();
		String birthday = selected_year + "-" + selected_month + "-"
				+ selected_day;

		Calendar cal_current = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(selected_year), Integer.parseInt(selected_month), Integer.parseInt(selected_day), 00, 00, 00);
		
		if (email.equals("") || password.equals("") || name.equals("")
				|| contact.equals("") || contact.length() != 11
				|| contact.charAt(0) != '+' || !email.contains("@") 
				|| password.trim().length()<6 || cal.compareTo(cal_current) >= 0) {
			if (contact.length() != 11 || contact.charAt(0) != '+')
				Toast.makeText(SignupActivity.this,
						"Error in contact format. Example: +65xxxxxxxx",
						Toast.LENGTH_SHORT).show();
			else if (!email.contains("@")) {
				Toast.makeText(SignupActivity.this,
						"Error in email format. Example: abc@example.com",
						Toast.LENGTH_SHORT).show();
			} 
			else if(password.trim().length()<6){
				Toast.makeText(SignupActivity.this,
						"Minimum password length is 6 digits",
						Toast.LENGTH_SHORT).show();
			}
			else if(cal.compareTo(cal_current) >= 0){
				Toast.makeText(SignupActivity.this,
						"Select Correct Birthdate",
						Toast.LENGTH_SHORT).show();
			}
			else {
				// Blank
				Toast.makeText(
						this,
						"There are blank fields! \n Please, fill all fields...",
						Toast.LENGTH_LONG).show();
			}
		} else {
			// proceed to signup
			new Signup_Controller(new ProgressDialog(SignupActivity.this),
					SignupActivity.this, email, password, name, contact,
					selected_country, birthday, selected_gender).execute();
			// registrationCode = generateVerficationCode();
			// sendMail(email, "Test Market Registration Confirmation",
			// "Please Use " + registrationCode
			// + "  as a verification code");
			// VerificationDialog(email, password, name, contact, birthday);

		}

	}

	private void defineEditTexts() {
		nameEiditText = (EditText) findViewById(R.id.signup_name);
		emailEiditText = (EditText) findViewById(R.id.signup_email);
		passwordEiditText = (EditText) findViewById(R.id.signup_password);
		contactEiditText = (EditText) findViewById(R.id.signup_contact);
		contactEiditText.setText("+65");
	}

	private void setupGenderSpinner() {
		genderList = new String[] { "Female", "Male" };
		genderSpinner = (Spinner) findViewById(R.id.signup_genderlist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SignupActivity.this, android.R.layout.simple_spinner_item,
				genderList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(adapter);
		genderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_gender = genderList[position];
				// switch (position) {
				// case 0:
				// selected_gender = genderList[position];
				// Log.d("ghazy", "selected gender : " + genderList[position]);
				// break;
				// case 1:
				// selected_gender = genderList[position];
				// Log.d("ghazy", "selected gender : " + genderList[position]);
				// break;
				// }

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void setupDaySpinner() {

		dayList = new String[31];
		int counter = 0;
		for (int i = 1; i <= 9; i++) {
			dayList[counter] = "0" + i;
			counter++;
		}
		for (int i = 10; i <= 31; i++) {
			dayList[counter] = "" + i;
			counter++;
		}
		birthdaySpinner = (Spinner) findViewById(R.id.signup_daylist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SignupActivity.this, android.R.layout.simple_spinner_item,
				dayList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthdaySpinner.setAdapter(adapter);
		birthdaySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_day = dayList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void setupMonthSpinner() {
		monthList = new String[12];
		int counter = 0;
		for (int i = 1; i <= 9; i++) {
			monthList[counter] = "0" + i;
			counter++;
		}
		for (int i = 10; i <= 12; i++) {
			monthList[counter] = "" + i;
			counter++;
		}
		birthmonthSpinner = (Spinner) findViewById(R.id.signup_monthllist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SignupActivity.this, android.R.layout.simple_spinner_item,
				monthList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthmonthSpinner.setAdapter(adapter);
		birthmonthSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						selected_month = monthList[position];
						// switch (position) {
						// case 0:
						// Log.d("ghazy", "selected month : "
						// + monthList[position]);
						// break;
						// case 1:
						// Log.d("ghazy", "selected month : "
						// + monthList[position]);
						// break;
						// }

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	private void setupYearSpinner() {
		yearList = new String[71];
		int counter = 0;

		for (int i = 2020; i >= 1950; i--) {
			yearList[counter] = "" + i;
			counter++;
		}
		birthyearSpinner = (Spinner) findViewById(R.id.signup_yearlist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SignupActivity.this, android.R.layout.simple_spinner_item,
				yearList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		birthyearSpinner.setAdapter(adapter);
		birthyearSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						selected_year = yearList[position];
						// switch (position) {
						// case 0:
						// Log.d("ghazy", "selected year : "
						// + yearList[position]);
						// break;
						// case 1:
						// Log.d("ghazy", "selected year : "
						// + yearList[position]);
						// break;
						// }

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	private void setupCountrySpinner() {
		countryList = new String[] { "North", "South", "East", "West", "Center" };

		countrySpinner = (Spinner) findViewById(R.id.signup_countrylist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SignupActivity.this, android.R.layout.simple_spinner_item,
				countryList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(adapter);
		countrySpinner.pointToPosition(50, 50);
		countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selected_country = countryList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void handle_signIn_result(int result) {

		switch (result) {
		case 1:// Success
			Toast.makeText(this, "You are signed up successfully",
					Toast.LENGTH_LONG).show();
			// Intent intent = new Intent(SignupActivity.this, TabBar.class);
			// intent.putExtra("email", emailEiditText.getText().toString());
			
			Global_access ga = new Global_access();
			ga.savePreferences(ga.TAG_LOGIN, true, SignupActivity.this);
			ga.savePreferences(ga.TAG_LOGIN_TYPE, ga.TAG_DIRECT_LOGIN, SignupActivity.this);
			ga.savePreferences(ga.TAG_EMAIL, emailEiditText.getText().toString(), SignupActivity.this);
			ga.savePreferences(ga.TAG_USER_ID, String.valueOf(Static_view_info.userId), SignupActivity.this);
			
			Intent intent = new Intent(SignupActivity.this,
					ProfilePicPicker.class);
			intent.putExtra("email", emailEiditText.getText().toString());
			startActivity(intent);
			finish();
			break;
		case 0:// wrong email or password
			Toast.makeText(this, "This email already exists", Toast.LENGTH_LONG)
					.show();
			break;
		case 2:// wrong email or password
			Toast.makeText(this, "This name already exists", Toast.LENGTH_LONG)
					.show();
			break;

		}
	}

//	private Session createSessionObject() {
//		Properties properties = new Properties();
//		properties.put("mail.smtp.auth", "true");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.host", "smtp.gmail.com");
//		properties.put("mail.smtp.port", "587");
//
//		return Session.getInstance(properties, new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(
//						"testmarketandroidapp123@gmail.com", "ghazyandbeshr123");
//			}
//		});
//	}

	// private void VerificationDialog(final String email, final String
	// password,
	// final String name, final String contact, final String birthday) {
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// alert.setTitle("Email Verification");
	// alert.setMessage("Check Your Email... type the verification code: ");
	//
	// // Set an EditText view to get user input
	// final EditText input = new EditText(this);
	// input.setInputType(InputType.TYPE_CLASS_NUMBER);
	// alert.setView(input);
	//
	// alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// String value = input.getText().toString();
	// // Do something with value!
	//
	// if (value.equals(registrationCode)) {
	//
	// new Signup_Controller(new ProgressDialog(
	// SignupActivity.this), SignupActivity.this, email,
	// password, name, contact, selected_country,
	// birthday, selected_gender).execute();
	// } else {
	// Toast.makeText(SignupActivity.this,
	// "Verification code is false", Toast.LENGTH_SHORT)
	// .show();
	// }
	// }
	// });
	//
	// alert.setNegativeButton("Cancel",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// // Canceled.
	//
	// }
	// });
	// alert.show();
	// }

	private String generateVerficationCode() {
		String code = "";
		int i1, i2, i3, i4, i5;

		Random random = new Random();
		i1 = (random.nextInt(9) + 0);
		i2 = (random.nextInt(9) + 0);
		i3 = (random.nextInt(9) + 0);
		i4 = (random.nextInt(9) + 0);
		i5 = (random.nextInt(9) + 0);

		code += i1 + "" + i2 + "" + i3 + "" + i4 + "" + i5;
		return code;
	}

//	private Message createMessage(String email, String subject,
//			String messageBody, Session session) throws MessagingException,
//			UnsupportedEncodingException {
//		Message message = new MimeMessage(session);
//		message.setFrom(new InternetAddress(
//				"testmarketandroidapp123@gmail.com",
//				"Test Market Email Verification"));
//		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
//				email, email));
//		message.setSubject(subject);
//		message.setText(messageBody);
//		return message;
//	}
//
//	private class SendMailTask extends AsyncTask<Message, Void, Void> {
//		private ProgressDialog progressDialog;
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			progressDialog = ProgressDialog.show(SignupActivity.this,
//					"Please wait", "Sending mail", true, false);
//		}
//
//		@Override
//		protected void onPostExecute(Void aVoid) {
//			super.onPostExecute(aVoid);
//			progressDialog.dismiss();
//		}
//
//		@Override
//		protected Void doInBackground(Message... messages) {
//			try {
//				Transport.send(messages[0]);
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//	}
//
//	private void sendMail(String email, String subject, String messageBody) {
//		Session session = createSessionObject();
//
//		try {
//			Message message = createMessage(email, subject, messageBody,
//					session);
//			new SendMailTask().execute(message);
//		} catch (AddressException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void onBackPressed() {
		Intent in = new Intent(SignupActivity.this,SignInActivity.class);
		startActivity(in);
		SignupActivity.this.finish();
	}

}
