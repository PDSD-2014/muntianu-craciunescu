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
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class LobyActivity extends Activity {

	BaseExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	Button refreshButton;

	Button joinLobby;

	long LastClickedOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobyy);

		/************* ExpandableListView ************************/
		expListView = (ExpandableListView) findViewById(R.id.lobby_listView);
		setUpList();
		listAdapter = new MyExpandableListAdapter(this, listDataHeader,
				listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				LastClickedOn = id;
				Log.i("LobyActivity", "clicked on" + LastClickedOn);
				return false;
			}
		});

		/************* Refresh Lobby Button ************************/
		refreshButton = (Button) findViewById(R.id.lobby_refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new NetworkLobby().execute();
			}
		});

		/************* Join Lobby Button ************************/
		joinLobby = (Button) findViewById(R.id.lobby_join);
		joinLobby.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String groupName = (String) ((MyExpandableListAdapter) listAdapter)
						.getGroup((int) LastClickedOn);
				String[] params = { groupName };
				new NetworkJoinLobby().execute(params);
			}
		});// setOnClickListener

	}// onCreate
	/****************************************************************************************/
	// necessary for initial population of the ExpandableList
	private void setUpList() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		listDataHeader.add("No Lobby is Available");

		List<String> defaultPlayers = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			defaultPlayers.add("player" + i);
		}

		listDataChild.put(listDataHeader.get(0), defaultPlayers);

	}

	/****************************************************************************************/
	class NetworkLobby extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask_NetworkLobby", "onPreExecute");
		}

		@Override
		protected String doInBackground(String... params) {
			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress("192.168.137.1",
						6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTask_NetworkLobby",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "GETLOBBY";
					out.println(outWritten);
					Log.i("AsyncTask_NetworkLobby", "Wrote in Socket:"
							+ outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));
					// String translation = in.readLine();

					String Message = in.readLine();

					out.close();
					in.close();

					Log.i("AsyncTask_NetworkLobby", "Recv_message:" + Message);
					return Message;

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}

			return null;
		}

		protected void onPostExecute(String result) {

			// listDataHeader = new ArrayList<String>();
			// listDataChild = new HashMap<String, List<String>>();

			listDataChild.clear();
			listDataHeader.clear();
			String[] lobbies = result.split("=");
			for (int i = 0; i < lobbies.length; i++) {

				String currentLobbyParsed = lobbies[i];
				String name = currentLobbyParsed.split("-")[0];
				listDataHeader.add(name);
				String[] playersInLobby = currentLobbyParsed.split("-")[1]
						.split("_");
				List<String> currentPlayers = new ArrayList<String>();
				for (int k = 0; k < playersInLobby.length; k++) {
					currentPlayers.add(playersInLobby[k]);
				}
				listDataChild.put(listDataHeader.get(i), currentPlayers);

			}// for lobies
			Log.i("AsyncTask_NetworkLobby", "UpdateList");
			listAdapter.notifyDataSetInvalidated();
			listAdapter.notifyDataSetChanged();
			// expListView.setAdapter(listAdapter);
		}// onPostExecute

	}// class NetworkLobby

	/****************************************************************************************/
	class NetworkJoinLobby extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask_NetworkJoinLobby", "onPreExecute");
		}

		@Override
		protected String doInBackground(String... params) {
			String lobbyName = params[0];
			// TODO Auto-generated method stub
			boolean result = false;
			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress("192.168.137.1",
						6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTask_NetworkJoinLobby",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "JOINLOBBY_" + lobbyName;
					out.println(outWritten);
					Log.i("AsyncTask_NetworkJoinLobby", "Wrote in Socket:"
							+ outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));
					// String translation = in.readLine();

					String Message = in.readLine();

					out.close();
					in.close();

					Log.i("AsyncTask_NetworkJoinLobby", "Recv_message:"
							+ Message);
					return Message;

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}
			return null;
		}

		protected void onPostExecute(String result) {

			if (result.equals("JOIN_OK")) {
				Log.i("AsyncTask_NetworkJoinLobby", "JOIN_OK:");
				// CONSTRUCT  param for intent
				List<String> players = ((MyExpandableListAdapter) listAdapter)
						.getChildList(LastClickedOn);
				String passedParam = "";
				for (String s : players) {
					passedParam += s + "_";
				}

				// passingParams
				Intent i = new Intent(getApplicationContext(),
						LobbyRoomActivty.class);
				i.putExtra("players", passedParam);
				startActivity(i);
			} else {
				Log.i("AsyncTask_NetworkJoinLobby", "JOIN_NOTOK:");
			}

		}
	}// class NetworkJoinLobby

}// class
