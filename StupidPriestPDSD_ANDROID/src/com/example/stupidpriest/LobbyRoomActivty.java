package com.example.stupidpriest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LobbyRoomActivty extends Activity {

	TextView tw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_room_activty);
		tw=(TextView)findViewById(R.id.room_textview);
		
		String passedParam=getIntent().getStringExtra("players");
		tw.setText(passedParam);
	}
}
