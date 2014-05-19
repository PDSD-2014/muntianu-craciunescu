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
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.cardemulation.CardEmulation;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class PlayGameActivity extends Activity {
	String hostIp = Constanst.hostIp;

	String lobbyName;
	String globalUsername = "";
	BaseAdapter imageAdapter;

	// Integer[] cardsThumbId;

	TextView tw;
	GridView gridView;
	Button dropCard;
	private ProgressDialog pdia;

	String lastCardClickedOn;
	Integer lastPositionClickedOn;
	String tokens[];
	Integer[] cardsThumbId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_game);

		globalUsername = getIntent().getStringExtra("userId");
		lobbyName = getIntent().getStringExtra("lobbyName");
		String cards = getIntent().getStringExtra("cards");

		String pased = globalUsername + " " + lobbyName + " cards:  " + cards;

		tw = (TextView) findViewById(R.id.play_text);
		tw.setText(pased);

		tokens = cards.toLowerCase().split("=");
		cardsThumbId = new Integer[4];
		boolean isFirst = false;
		if (tokens.length == 5) {
			for (int i = 0; i < 4; i++) {

				cardsThumbId[i] = PictureFinder.findPictureByName(tokens[i]);
			}
			// cardsThumbId[4] = PictureFinder.findPictureByName("placeholder");

		}
		// ---------------------------------------
		gridView = (GridView) findViewById(R.id.play_grid_view);
		imageAdapter = new ImageAdapter(this, cardsThumbId);

		gridView.setAdapter(imageAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				tw.setText(tokens[position]);
				lastCardClickedOn = tokens[position].toUpperCase();
				lastPositionClickedOn = position;
			}
		});// gridViewListener

		// ---------------------------------------

		Button dropButton = (Button) findViewById(R.id.play_drop_card);
		dropButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new NetworkPlay().execute();
			}
		});// buttonListener

	}// on create

	class NetworkPlay extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			Log.i("AsyncTask_NetworkPlay", "onPreExecute");

			pdia = ProgressDialog.show(PlayGameActivity.this, "Waiting",
					"FOR NEW CARD", true, false);
			pdia.setMessage("WAITING For all other players to be ready....");
			pdia.show();

		}

		@Override
		protected String doInBackground(String... params) {

			Socket sockfd;
			try {
				SocketAddress sockaddr = new InetSocketAddress(hostIp, 6792);
				sockfd = new Socket();
				sockfd.connect(sockaddr);
				if (sockfd.isConnected()) {

					Log.i("AsyncTask_NetworkPlay",
							"doInBackground: Socket created, streams assigned");

					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(sockfd.getOutputStream())),
							true);

					String outWritten = "PLAY_" + lobbyName + "_"
							+ globalUsername + "_" + lastCardClickedOn;
					out.println(outWritten);
					Log.i("AsyncTask_NetworkPlay", "Wrote in Socket:"
							+ outWritten);

					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(
							sockfd.getInputStream()));
					// String translation = in.readLine();

					String Message = in.readLine();

					out.close();
					in.close();

					Log.i("AsyncTask_NetworkPlay", "Recv_message:" + Message);
					return Message;

				}
			} catch (IOException ioe) {
				ioe.printStackTrace();

			}
			return null;
		}

		protected void onPostExecute(String result) {
			pdia.dismiss();
			if (result.startsWith("PLAY_OK")) {
				String newCards = result.split("CARDS=")[1];
				tw.setText(newCards);

				String aba = newCards.toLowerCase();

				Log.i("AsyncTask_NetworkPlay", " Updated Interface with card"
						+ aba + " lastPositionClickedOn="
						+ lastPositionClickedOn);

				tokens[lastPositionClickedOn] = aba;

				cardsThumbId[lastPositionClickedOn] = PictureFinder
						.findPictureByName(aba);

				imageAdapter.notifyDataSetInvalidated();
				imageAdapter.notifyDataSetChanged();

			}
			if (result.startsWith("ENDGAME_WINNER")) {
				String status = result.split(":")[1];

				Intent i = new Intent(getApplicationContext(),
						EndGameActivity.class);

				i.putExtra("userId", globalUsername);
				i.putExtra("status", status);
				startActivity(i);

			}

		}// onPostExecute
	}// networkPlay

}// class
