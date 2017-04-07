package com.fuzzylabs.election2014quiz;

import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VoteShowActivity extends Activity {

	private TextView modiText, kejriText, nandanText, rahulText, mulayamText;
	private TextView modiPer, kejriPer, nandanPer, rahulPer, mulayamPer;
	private TextView statusText;
	private ProgressBar modiProgress, kejriProgress, nandanProgress,
			rahulProgress, mulayamProgress;
	private boolean flagModi = false, flagKejri = false, flagNandan = false,
			flagRahul = false, flagMulayam = false;

	private static String[] standings;
	private static int percent[] = new int[5];
	private static int sum = 0;
	private static DecimalFormat df = new DecimalFormat("0");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voteshow);

		setTitle("Current Standings");
		df.setRoundingMode(RoundingMode.HALF_UP);

		modiText = (TextView) findViewById(R.id.modiText);
		kejriText = (TextView) findViewById(R.id.kejriText);
		nandanText = (TextView) findViewById(R.id.nandanText);
		rahulText = (TextView) findViewById(R.id.rahulText);
		mulayamText = (TextView) findViewById(R.id.mulayamText);
		statusText = (TextView) findViewById(R.id.statusText);

		modiPer = (TextView) findViewById(R.id.modiPer);
		kejriPer = (TextView) findViewById(R.id.kejriPer);
		nandanPer = (TextView) findViewById(R.id.nandanPer);
		rahulPer = (TextView) findViewById(R.id.rahulPer);
		mulayamPer = (TextView) findViewById(R.id.mulayamPer);

		modiProgress = (ProgressBar) findViewById(R.id.modiProgress);
		kejriProgress = (ProgressBar) findViewById(R.id.kejriProgress);
		nandanProgress = (ProgressBar) findViewById(R.id.nandanProgress);
		rahulProgress = (ProgressBar) findViewById(R.id.rahulProgress);
		mulayamProgress = (ProgressBar) findViewById(R.id.mulayamProgress);
		fetchVotes();
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

			String body = "---Election 2014 Quiz---\n --Fuzzy Labs--"
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
	
	public void onShare(View view) {
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
	}

	private void fetchVotes() {

		modiProgress.setProgress(0);
		modiPer.setText("0%");
		kejriProgress.setProgress(0);
		kejriPer.setText("0%");
		nandanProgress.setProgress(0);
		nandanPer.setText("0%");
		rahulProgress.setProgress(0);
		rahulPer.setText("0%");
		mulayamProgress.setProgress(0);
		mulayamPer.setText("0%");
		standings = null;
		sum = 0;

		if (!isOnline()) {
			statusText.setText("No Network Connection");
		} else {
			statusText.setText("Loading..");
			String urlStr = new String(
					"http://www.fuzzylabselectionquiz.appspot.com/votefor?type=show");
			GetVotesTask task = new GetVotesTask();
			task.execute(urlStr);
		}
	}

	private boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private class GetVotesTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urls[0]);
			try {
				HttpResponse execute = client.execute(httpGet);
				HttpEntity r_entity = execute.getEntity();

				String xmlString = EntityUtils.toString(r_entity);
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = factory.newDocumentBuilder();
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(xmlString));
				Document doc = db.parse(inStream);
				String modi = "", kejri = "", nandan = "", rahul = "", mulayam = "";
				NodeList nl = doc.getElementsByTagName("leader");
				if (nl.item(0).getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nl.item(0);
					modi = element.getElementsByTagName("modi").item(0)
							.getTextContent();
					kejri = element.getElementsByTagName("kejri").item(0)
							.getTextContent();
					nandan = element.getElementsByTagName("nandan").item(0)
							.getTextContent();
					rahul = element.getElementsByTagName("rahul").item(0)
							.getTextContent();
					mulayam = element.getElementsByTagName("mulayam").item(0)
							.getTextContent();
				}
				response = modi + "," + kejri + "," + nandan + "," + rahul
						+ "," + mulayam;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			standings = result.split(",");
			modiText.setText(modiText.getText() + standings[0]);
			kejriText.setText(kejriText.getText() + standings[1]);
			nandanText.setText(nandanText.getText() + standings[2]);
			rahulText.setText(rahulText.getText() + standings[3]);
			mulayamText.setText(mulayamText.getText() + standings[4]);
			statusText.setText("");
			sum = Integer.parseInt(standings[0])
					+ Integer.parseInt(standings[1])
					+ Integer.parseInt(standings[2])
					+ Integer.parseInt(standings[3])
					+ Integer.parseInt(standings[4]);
			percent[0] = Integer.parseInt(df.format(Integer.parseInt(standings[0]) * 100D / sum));
			percent[1] = Integer.parseInt(df.format(Integer.parseInt(standings[1]) * 100D / sum));
			percent[2] = Integer.parseInt(df.format(Integer.parseInt(standings[2]) * 100D / sum));
			percent[3] = Integer.parseInt(df.format(Integer.parseInt(standings[3]) * 100D / sum));
			percent[4] = Integer.parseInt(df.format(Integer.parseInt(standings[4]) * 100D / sum));
			
			AnimateProgressTask animateTask = new AnimateProgressTask();
			animateTask.execute();
		}
	}

	private class AnimateProgressTask extends AsyncTask<Void, Integer, Void> {
		boolean flagModi = false, flagKejri = false, flagNandan = false,
				flagRahul = false, flagMulayam = false;

		@Override
		protected Void doInBackground(Void... leader) {
			while (!(flagModi & flagKejri & flagNandan & flagRahul & flagMulayam)) {
				if (modiProgress.getProgress() < percent[0]) {
					publishProgress(0);
					flagModi = false;
				} else {
					flagModi = true;
				}

				if (kejriProgress.getProgress() < percent[1]) {
					publishProgress(1);
					flagKejri = false;
				} else {
					flagKejri = true;
				}

				if (nandanProgress.getProgress() < percent[2]) {
					publishProgress(2);
					flagNandan = false;
				} else {
					flagNandan = true;
				}

				if (rahulProgress.getProgress() < percent[3]) {
					publishProgress(3);
					flagRahul = false;
				} else {
					flagRahul = true;
				}

				if (mulayamProgress.getProgress() < percent[4]) {
					publishProgress(4);
					flagMulayam = false;
				} else {
					flagMulayam = true;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... leader) {
			super.onProgressUpdate(leader);
			synchronized (this) {
				switch (leader[0]) {
				case 0:
					modiProgress.incrementProgressBy(1);
					modiPer.setText(modiProgress.getProgress() + "%");
					break;
				case 1:
					kejriProgress.incrementProgressBy(1);
					kejriPer.setText(kejriProgress.getProgress() + "%");
					break;
				case 2:
					nandanProgress.incrementProgressBy(1);
					nandanPer.setText(nandanProgress.getProgress() + "%");
					break;
				case 3:
					rahulProgress.incrementProgressBy(1);
					rahulPer.setText(rahulProgress.getProgress() + "%");
					break;
				case 4:
					mulayamProgress.incrementProgressBy(1);
					mulayamPer.setText(mulayamProgress.getProgress() + "%");
					break;

				default:
					break;
				}
			}
		}
		@Override
		protected void onPostExecute(Void result) {
			modiProgress.setProgress(percent[0]);
			modiPer.setText(percent[0] + "%");
			kejriProgress.setProgress(percent[1]);
			kejriPer.setText(percent[1] + "%");
			nandanProgress.setProgress(percent[2]);
			nandanPer.setText(percent[2] + "%");
			rahulProgress.setProgress(percent[3]);
			rahulPer.setText(percent[3] + "%");
			mulayamProgress.setProgress(percent[4]);
			mulayamPer.setText(percent[4] + "%");
			super.onPostExecute(result);
		}
	}
}
