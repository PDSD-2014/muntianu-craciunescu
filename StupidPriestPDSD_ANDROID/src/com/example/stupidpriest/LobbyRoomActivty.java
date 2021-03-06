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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyRoomActivty extends Activity {

	List<String> players;
	String globalUsername = "";
	
	//String hostIp = "192.168.137.1";
	String hostIp=Constanst.hostIp;
	
	String lobbyName;
	TextView tw;
	Button leaveBTN;
	Button startNowBTN;
	private ProgressDialog pdia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_room_activty);
		tw = (TextView) findViewById(R.id.romm_lobbyName);
		/****************************************************************************************/
		globalUsername = getIntent().getStringExtra("userId");
		String playersConnected = getIntent().getStringExtra("players");
		lobbyName = getIntent().getStringExtra("lobbyName");
		String text = "LobbyName=" + lobbyName + "\n";
		// text += passedParam;
		/****************************************************************************************/
		players = new ArrayList<String>();
		String splits[] = playersConnected.split("_");
		for (String s : splits) {
			players.add(s);
		}

		ListView listview = (ListView) findViewById(R.id.room_listview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, players);
		listview.setAdapter(adapter);
		tw.setText(text);
		/*********************** Leave button ******************************************************/
		leaveBTN = (Button) findViewById(R.id.romm_leaveBTN);
		leaveBTN.setOnClickListener((new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] params = { lobbyName };
				new NetworkLeaveLobby().execute(params);
			}

		}));
		/*********************** Start now button **************************************************/
		startNowBTN = (Button) findViewById(R.id.romm_startNowBTN);
		startNowBTN.setOnClickListener((new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] params = { lobbyName };
				new NetworkStartGameNow().execute(params);
			}

		}));
	}// on create

	/****************************************************************************************/
	class NetworkLeaveLobby extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask_NetworkLeaveLobby", "onPreExecute");

		}

		@Override
		protected String doInBackground(String... params) {
			String lobbyRoomName = params[0];
			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress(hostIp, 6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTask_NetworkLeaveLobby",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "LEAVELOBBY_" + globalUsername + "_"
							+ lobbyRoomName;
					out.println(outWritten);
					Log.i("AsyncTask_NetworkLeaveLobby", "Wrote in Socket:"
							+ outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));
					// String translation = in.readLine();

					String Message = in.readLine();

					out.close();
					in.close();

					Log.i("AsyncTask_NetworkLeaveLobby", "Recv_message:"
							+ Message);
					return Message;

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}
			return null;
		}

		protected void onPostExecute(String result) {
			if (result.startsWith("OK")) {
				Log.i("AsyncTask_NetworkLeaveLobby", "OnPostExecute leave OK ");
				Intent i = new Intent(getApplicationContext(),
						LobyActivity.class);

				i.putExtra("userId", globalUsername);
				startActivity(i);
			} else {
				Log.i("AsyncTask_NetworkLeaveLobby",
						"OnPostExecute create Errror:  " + result);
				tw.setText(result);
				// tw.setBackground(background);
			}
		}
	}// leaveLobby Async Task

	/****************************************************************************************/
	class NetworkStartGameNow extends AsyncTask<String, String, String> {

		private final ReentrantLock lock = new ReentrantLock();
		private final Condition tryAgain = lock.newCondition();
		private volatile boolean finished = false;

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask_NetworkLeaveLobby", "onPreExecute");
			pdia = ProgressDialog.show(LobbyRoomActivty.this, "Waiting",
					"ALL PLAYERS SHOULD BE CONNECTED", true, false);
			pdia.setMessage("WAITING For all other players to be ready....");
			pdia.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String lobbyRoomName = params[0];
			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress(hostIp, 6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTask_NetworkLeaveLobby",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "START_" + lobbyRoomName + "_"
							+ globalUsername;
					out.println(outWritten);
					Log.i("AsyncTask_NetworkLeaveLobby", "Wrote in Socket:"
							+ outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));
					// String translation = in.readLine();
					while (true) {
						String Message = in.readLine();
						if (Message != null) {
							Log.i("AsyncTask_NetworkLeaveLobby",
									"Recv_message:" + Message);
							if (Message.startsWith("START_OK:CARDS")) {
								out.close();
								in.close();
								sockfd.close();
								return Message;
							}
						}else
							Thread.sleep(200);
					}

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {
			pdia.dismiss();
			if (result.startsWith("START_OK")) {
				Log.i("AsyncTask_NetworkLeaveLobby", "OnPostExecute leave OK ");
				Intent i = new Intent(getApplicationContext(),
						PlayGameActivity.class);

				String cards = result.split("CARDS=")[1];
				i.putExtra("cards", cards);
				i.putExtra("lobbyName", lobbyName);
				i.putExtra("userId", globalUsername);
				startActivity(i);
			} else {
				Log.i("AsyncTask_NetworkLeaveLobby",
						"OnPostExecute create Errror:  " + result);
				tw.setText(result);
				// tw.setBackground(background);
			}
		}
	}// NetworkStartGameNow 

}// class
