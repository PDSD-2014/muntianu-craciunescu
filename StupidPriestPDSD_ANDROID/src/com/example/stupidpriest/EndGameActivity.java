package com.example.stupidpriest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndGameActivity extends Activity {
	String hostIp = Constanst.hostIp;
	String globalUsername;

	TextView tw;
	Button newGameBTN;
	ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_game);

		String status = getIntent().getStringExtra("status");
		globalUsername = getIntent().getStringExtra("userId");

		tw = (TextView) findViewById(R.id.end_text);
		

		newGameBTN = (Button) findViewById(R.id.end_play_again);
		newGameBTN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						LobyActivity.class);

				i.putExtra("userId", globalUsername);
				startActivity(i);
			}
		});
		
		
		iv=(ImageView)findViewById(R.id.imageView1);
		if(status.equals("YES")){
			iv.setImageResource(R.drawable.winning);
			tw.setText(" So much win .");
		}
		else
		{
			iv.setImageResource(R.drawable.losing);
			tw.setText("Bad luck..Try again");
		}
	}
}
