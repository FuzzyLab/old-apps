package com.fuzzylabs.election2014quiz;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class GenQuizActivity extends Activity implements OnClickListener,
		OnTouchListener {

	private static String quizType;
	private static GenQuiz genQuiz;
	private static ElectionDb info;
	private static List<Integer> unansweredList;
	private static Random randi = new Random();
	private static int level;

	private static TextView quesView, scoreView, levelView, quesNoView;
	private static Button btn1;
	private static Button btn2;
	private static Button btn3;
	private static Button btn4;
	ScrollingMovementMethod smm = new ScrollingMovementMethod();

	private static boolean isPlaying = false;
	private static String quizName;
	private static GenPostAnswerTask pat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gen_quiz);
		info = new ElectionDb(getApplicationContext());

		quizType = getIntent().getExtras().getString(GenQuiz.TYPE_ID);
		if (quizType.equals("1"))
			quizName = "Who Am I? Quiz";
		else if (quizType.equals("2"))
			quizName = "Scam Quiz";
		else if (quizType.equals("3"))
			quizName = "General Election Quiz";

		setTitle(quizName);

		quesView = (TextView) findViewById(R.id.genQuestionText);
		quesView.setMovementMethod(smm);
		scoreView = (TextView) findViewById(R.id.scoreText);
		levelView = (TextView) findViewById(R.id.levelText);
		quesNoView = (TextView) findViewById(R.id.quesNoText);
		btn1 = (Button) findViewById(R.id.genAnswer1);
		btn1.setOnClickListener(this);
		btn1.setOnTouchListener(this);
		btn2 = (Button) findViewById(R.id.genAnswer2);
		btn2.setOnClickListener(this);
		btn2.setOnTouchListener(this);
		btn3 = (Button) findViewById(R.id.genAnswer3);
		btn3.setOnClickListener(this);
		btn3.setOnTouchListener(this);
		btn4 = (Button) findViewById(R.id.genAnswer4);
		btn4.setOnClickListener(this);
		btn4.setOnTouchListener(this);
		try {
			info.open();
			level = Integer.parseInt(info.getInfo(quizType));
			unansweredList = info.getGenQuizLevelUnansweredList(quizType,
					String.valueOf(level));
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		changeQuestion();
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
		Button button = (Button) view;
		btn1.setEnabled(false);
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		btn4.setEnabled(false);
		String answerSelected = (String) button.getText();
		int isCorrect = 0;
		if (answerSelected.equals(genQuiz.options[0])) {
			isCorrect = 1;
			view.setBackgroundResource(R.drawable.bgreen);
		} else {
			view.setBackgroundResource(R.drawable.bred);
			if (btn1.getText().equals(genQuiz.options[0]))
				btn1.setBackgroundResource(R.drawable.bgreen);
			else if (btn2.getText().equals(genQuiz.options[0]))
				btn2.setBackgroundResource(R.drawable.bgreen);
			else if (btn3.getText().equals(genQuiz.options[0]))
				btn3.setBackgroundResource(R.drawable.bgreen);
			else if (btn4.getText().equals(genQuiz.options[0]))
				btn4.setBackgroundResource(R.drawable.bgreen);
		}
		pat = new GenPostAnswerTask();
		pat.execute(isCorrect);
	}

	public void changeQuestion() {
		setScore();
		try {
			info.open();
			int unanswered = unansweredList.size();
			int allUnanswered = info.getGenQuizAllUnansweredCount(quizType);
			info.close();
			if (allUnanswered > 0) {
				if (unanswered > 0) {
					int row;
					do {
						row = randi.nextInt(unanswered);
					} while (row >= unanswered);
					info.open();
					genQuiz = info.getGenQuizQuestion(unansweredList.get(row));
					info.close();
					setScreen();
				} else {
					int prevLevel = level;
					level++;
					info.open();
					info.createInfoEntry(quizType, String.valueOf(level));
					unansweredList = info.getGenQuizLevelUnansweredList(
							quizType, String.valueOf(level));
					final int levelCorrect = info.getLevelCorrectSize(quizType,
							String.valueOf(prevLevel));
					final int levelTotal = info.getLevelSize(quizType,
							String.valueOf(prevLevel));
					info.close();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							GenQuizActivity.this);
					View view = getLayoutInflater().inflate(
							R.layout.level_dialog, null);
					builder.setView(view);
					builder.setTitle("Level " + prevLevel + " Over");
					builder.setNegativeButton("Leave",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
								}
							});
					builder.setPositiveButton("Next Level",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									changeQuestion();
								}
							});
					builder.show();
					((TextView) view.findViewById(R.id.levelScore))
							.setText(String.valueOf(levelCorrect * 10));
					((TextView) view.findViewById(R.id.levelCorrect))
							.setText(String.valueOf(levelCorrect));
					((TextView) view.findViewById(R.id.levelAll))
							.setText(String.valueOf(levelTotal));
				}
			} else if (isPlaying) {
				isPlaying = false;
				info.open();
				final int levelCorrect = info.getLevelCorrectSize(quizType,
						String.valueOf(level));
				final int levelTotal = info.getLevelSize(quizType,
						String.valueOf(level));
				info.close();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						GenQuizActivity.this);
				View view = getLayoutInflater().inflate(R.layout.level_dialog,
						null);
				builder.setView(view);
				builder.setTitle("Level " + level + " Over");
				builder.setNegativeButton("Leave",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								quizOverAlert();
							}
						});
				builder.setPositiveButton("Next Level",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								quizOverAlert();
							}
						});
				builder.show();
				((TextView) view.findViewById(R.id.levelScore)).setText(String
						.valueOf(levelCorrect * 10));
				((TextView) view.findViewById(R.id.levelCorrect))
						.setText(String.valueOf(levelCorrect));
				((TextView) view.findViewById(R.id.levelAll)).setText(String
						.valueOf(levelTotal));
			} else {
				quizOverAlert();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void quizOverAlert() {
		try {
			info.open();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		final int quizCorrect = info.getQuizCorrectSize(quizType);
		final int quizTotal = info.getQuizSize(quizType);
		info.close();

		AlertDialog.Builder builder = new AlertDialog.Builder(
				GenQuizActivity.this);
		View view = getLayoutInflater().inflate(R.layout.quiz_dialog, null);
		builder.setView(view);
		builder.setTitle("Quiz Over");
		builder.setNegativeButton("Leave",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.setPositiveButton("Restart",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							info.open();
							info.resetGenQuizTable(quizType);
							level = 1;
							info.createInfoEntry(quizType,
									String.valueOf(level));
							unansweredList = info
									.getGenQuizLevelUnansweredList(quizType,
											String.valueOf(level));
							info.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						dialog.dismiss();
						changeQuestion();
					}
				});
		builder.setNeutralButton("Share Score",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.election2014quiz";

						String body = "I Scored " + quizCorrect * 10 + "/"
								+ quizTotal * 10 + " in " + quizName + "."
								+ "\nCan you beat my score?"
								+ "\n\n Download this app: " + thisApp;

						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, body);
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent,
								"Fuzzy Labs | Election 2014 Quiz"));
					}
				});
		builder.show();
		((TextView) view.findViewById(R.id.quizScore)).setText(String
				.valueOf(quizCorrect * 10));
		((TextView) view.findViewById(R.id.quizCorrect)).setText(String
				.valueOf(quizCorrect));
		((TextView) view.findViewById(R.id.quizAll)).setText(String
				.valueOf(quizTotal));
	}

	private void setScore() {
		int points = 0;
		String scoreText = "Score : ";
		String levelText = "Level : ";
		try {
			info.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		points = info.getQuizCorrectSize(quizType);

		info.close();
		scoreView.setText(scoreText + points * 10);
		levelView.setText(levelText + level);
	}

	private void setScreen() {
		Set<Integer> set = new HashSet<Integer>();
		int option;
		try {
			info.open();
			String quesNoText = "Question : ";
			int quesNo = info.getGenQuizAnsweredCount(quizType) + 1;
			quesNoView.setText(quesNoText + quesNo);
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean isDone = false;
		quesView.setText(genQuiz.question);
		quesView.scrollTo(0, 0);
		do {
			option = randi.nextInt(4);
			isDone = set.add(option);
		} while (!isDone);
		btn1.setText(genQuiz.options[option]);
		btn1.setBackgroundResource(R.drawable.bnormal);
		btn1.setEnabled(true);
		do {
			option = randi.nextInt(4);
			isDone = set.add(option);
		} while (!isDone);
		btn2.setText(genQuiz.options[option]);
		btn2.setBackgroundResource(R.drawable.bnormal);
		btn2.setEnabled(true);
		do {
			option = randi.nextInt(4);
			isDone = set.add(option);
		} while (!isDone);
		btn3.setText(genQuiz.options[option]);
		btn3.setBackgroundResource(R.drawable.bnormal);
		btn3.setEnabled(true);
		do {
			option = randi.nextInt(4);
			isDone = set.add(option);
		} while (!isDone);
		btn4.setText(genQuiz.options[option]);
		btn4.setBackgroundResource(R.drawable.bnormal);
		btn4.setEnabled(true);
	}

	private class GenPostAnswerTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... values) {
			try {
				info.open();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			ContentValues cv = new ContentValues();
			cv.put(info.GEN_IS_DONE, 1);
			cv.put(info.GEN_IS_CORRECT, values[0]);
			info.ourDatabase.update(info.GEN_QUIZ_TABLE, cv, info.GEN_QUES_ID
					+ " = ?",
					new String[] { String.valueOf(genQuiz.questionId) });
			info.close();
			unansweredList.remove((Integer) genQuiz.questionId);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean bool) {
			isPlaying = true;
			changeQuestion();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			v.setBackgroundResource(R.drawable.bpressed);
			v.invalidate();
			break;
		}
		case MotionEvent.ACTION_UP: {
			v.setBackgroundResource(R.drawable.bnormal);
			v.invalidate();
			break;
		}
		}
		return false;
	}
}
