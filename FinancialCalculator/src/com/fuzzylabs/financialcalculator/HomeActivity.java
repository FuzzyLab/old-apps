package com.fuzzylabs.financialcalculator;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;

public class HomeActivity extends FragmentActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch(position) {
		case 0:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					BondFragment.newInstance(position)).commit();
			break;
		case 1:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					CcipFragment.newInstance(position)).commit();
			break;
		case 2:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					CurrencyFragment.newInstance(position)).commit();
			break;
		case 3:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					DiscountLostFragment.newInstance(position)).commit();
			break;
		case 4:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					EmiFragment.newInstance(position)).commit();
			break;
		case 5:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					FixedFragment.newInstance(position)).commit();
			break;
		case 6:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					FcnrFragment.newInstance(position)).commit();
			break;
		case 7:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					LumpsumFragment.newInstance(position)).commit();
			break;
		case 8:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					RecurringFragment.newInstance(position)).commit();
			break;
		case 9:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					ReinvestmentFragment.newInstance(position)).commit();
			break;
		case 10:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					TaxFragment.newInstance(position)).commit();
			break;
		case 11:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					YieldToMaturityFragment.newInstance(position)).commit();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.home, menu);
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.financialcalculator";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.financialcalculator";

			String body = "---Financial Calculator---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Financial Calculator"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
