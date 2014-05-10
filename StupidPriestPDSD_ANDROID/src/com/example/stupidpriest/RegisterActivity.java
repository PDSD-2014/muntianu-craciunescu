package com.example.stupidpriest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	EditText email, password, confirmedPasword;
	TextView displayStatus;
	Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		displayStatus = (TextView) findViewById(R.id.textViewUp);

		email = (EditText) findViewById(R.id.register_email);
		password = (EditText) findViewById(R.id.register_password);
		confirmedPasword = (EditText) findViewById(R.id.register_confirm_password);

		registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String inputEmail = "";
				if (email.getText() != null) {
					inputEmail = email.getText().toString();
				}

				String inputPassword = "";
				if (password.getText() != null) {
					inputPassword = password.getText().toString();
				}

				String inputConfirmedPassword = "";
				if (confirmedPasword != null) {
					inputConfirmedPassword = confirmedPasword.getText()
							.toString();
				}

				if (inputPassword.equals(inputConfirmedPassword) != true) {
					displayStatus.setText("Password Mismatch.ReEnterPassword");
					displayStatus.setTextColor(Color.parseColor("#ffff0000"));

					Log.i("RegisterActivity", "Clearing text");
					confirmedPasword.setText("");
					password.setText("");
				}

				String[] params = { inputEmail, inputPassword };
				new NetworkRegister().execute(params);

			}// onCliclEnds
		});// ListenerEnds

	}// onCreate

	class NetworkRegister extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTaskRegister", "onPreExecute");
		}

		@Override
		protected String doInBackground(String... params) {

			String email = params[0];
			String password = params[1];
			// TODO Auto-generated method stub
			boolean result = false;
			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress("192.168.137.1",
						6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTaskRegister",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "REGISTER_" + email + "_" + password;
					out.println(outWritten);
					Log.i("AsyncTaskRegister", "Wrote in Socket:" + outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));

					String Message = in.readLine();

					out.close();
					in.close();

					Log.i("AsyncTaskRegister", "Recv_message:" + Message);
					return Message;

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}
			return null;

		}//do in background

		protected void onPostExecute(String result) {

			if (result.equals("VALID_REGISTRATION")) {
				Log.i("AsyncTaskRegister","Registration Succed");
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);

				startActivity(i);

			}
			else
			{
				Log.i("AsyncTaskRegister","Registration Failed");
				email.setText("");
				confirmedPasword.setText("");
				password.setText("");
				
				displayStatus.setTextColor(Color.parseColor("#ffff0000"));
				displayStatus.setText("Server Responded with "+result+" Please try again later\nEnter your email");
				
			
			}
			
		}//on postExecute

	}//class NetworkRegister
}//class
