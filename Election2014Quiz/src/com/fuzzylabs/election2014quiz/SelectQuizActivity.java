package com.fuzzylabs.election2014quiz;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SelectQuizActivity extends Activity implements OnClickListener {

	View view1, view2, view3, view4;
	TextView quiz1Points, quiz2Points, quiz3Points, quiz4Points;
	ElectionDb info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_quiz);

		setTitle("Select Quiz");

		info = new ElectionDb(getApplicationContext());

		view1 = findViewById(R.id.view1);
		view1.setOnClickListener(this);
		view2 = findViewById(R.id.view2);
		view2.setOnClickListener(this);
		view3 = findViewById(R.id.view3);
		view3.setOnClickListener(this);
		view4 = findViewById(R.id.view4);
		view4.setOnClickListener(this);

		quiz1Points = (TextView) findViewById(R.id.quiz1Points);
		quiz2Points = (TextView) findViewById(R.id.quiz2Points);
		quiz3Points = (TextView) findViewById(R.id.quiz3Points);
		quiz4Points = (TextView) findViewById(R.id.quiz4Points);
		
		try {
			info.open();
			if(info.getInfo(GenQuiz.VOTED).equals("1"))
				quiz4Points.setText("Current Standings");
			else
				quiz4Points.setText("Vote Now");
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setValues();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setValues();
		try {
			info.open();
			if(info.getInfo(GenQuiz.VOTED).equals("1"))
				quiz4Points.setText("Current Standings");
			else
				quiz4Points.setText("Vote Now");
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.election2014quiz";

			String body = "--Fuzzy Labs--\n" + "---Election 2014 Quiz---\n"
					+ "$ Who Am I? Quiz $\n" + "$ Scams Quiz $\n"
					+ "$ General Election Quiz $\n"
					+ "$ Vote Now & Current Standings $\n"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Election 2014 Quiz"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		String value;
		Intent intent;
		if (view.getId() == view1.getId()) {
			value = GenQuiz.QUIZ_TYPE_1;
			intent = new Intent(SelectQuizActivity.this, GenQuizActivity.class);
			intent.putExtra(GenQuiz.TYPE_ID, value);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (view.getId() == view2.getId()) {
			value = GenQuiz.QUIZ_TYPE_2;
			intent = new Intent(SelectQuizActivity.this, GenQuizActivity.class);
			intent.putExtra(GenQuiz.TYPE_ID, value);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (view.getId() == view3.getId()) {
			value = GenQuiz.QUIZ_TYPE_3;
			intent = new Intent(SelectQuizActivity.this, GenQuizActivity.class);
			intent.putExtra(GenQuiz.TYPE_ID, value);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (view.getId() == view4.getId()) {
			try {
				info.open();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String voted = info.getInfo(GenQuiz.VOTED);
			info.close();
			if (voted.equals("0"))
				intent = new Intent(SelectQuizActivity.this,
						VoteForActivity.class);
			else
				intent = new Intent(SelectQuizActivity.this,
						VoteShowActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	private void setValues() {
		String points;
		String text = "Score : ";
		points = returnScore("1");
		quiz1Points.setText(text + points);
		points = returnScore("2");
		quiz2Points.setText(text + points);
		points = returnScore("3");
		quiz3Points.setText(text + points);
	}

	private String returnScore(String typeId) {
		int point = 0;
		int maxPoint = 0;
		try {
			info.open();
			point = info.getQuizCorrectSize(typeId);
			maxPoint = info.getQuizSize(typeId);
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return point * 10 + "/" + maxPoint * 10;
	}
}
