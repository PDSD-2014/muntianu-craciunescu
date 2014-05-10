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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	TextView registerScreen;
	Button loginButton;
	EditText email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		email = (EditText) findViewById(R.id.Email);
		password = (EditText) findViewById(R.id.Password);

		loginButton = (Button) findViewById(R.id.LoginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String emailWrittenOnUi = "";
				if (email.getText() != null) {
					emailWrittenOnUi = email.getText().toString();
				}

				String passwordWrittenOnUi = "";
				if (password.getText() != null) {
					passwordWrittenOnUi = password.getText().toString();
				}
				String[] params = { emailWrittenOnUi, passwordWrittenOnUi };
				new NetworkLogin().execute(params);
			}
		});

		registerScreen = (TextView) findViewById(R.id.link_to_register);
		// Listening to register new account link
		registerScreen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
			}
		});

	}

	class NetworkLogin extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask", "onPreExecute");
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
					
					Log.i("AsyncTask",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);
					
					String outWritten = "LOGIN_" + email + "_" + password;
					out.println(outWritten);
					Log.i("AsyncTask", "Wrote in Socket:" + outWritten);


					BufferedReader in;
                    in = new BufferedReader(new InputStreamReader(sockfd.getInputStream())); 
                    //String translation = in.readLine();
					
					String Message = in.readLine();
					
					
					out.close();
					in.close();
					
					Log.i("AsyncTask", "Recv_message:" + Message);
					return Message;
					

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}
			return null;
		}

		protected void onPostExecute(String result) {
			//registerScreen.setText(result);
			if(result.equals("VALID_AUTH")){
				Intent i = new Intent(getApplicationContext(),
						LobyActivity.class);
				startActivity(i);
			}
			
		}

	}
}
