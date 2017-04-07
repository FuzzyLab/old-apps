package com.fuzzylabs.expensemanager;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import com.fuzzylabs.expensemanager.R;

public class ExpenseActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private ExpensesDB expenseDb = new ExpensesDB(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		expenseDb.addCategory("Foods & Beverages");
		expenseDb.addCategory("Travel");
		expenseDb.addCategory("Leisure");
		expenseDb.addCategory("Household");
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		switch(position) {
		case 0:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					ExpenseFragment.newInstance(position + 1)).commit();
			break;
		case 1:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					GroupFragment.newInstance(position + 1)).commit();
			break;
		case 2:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					HistoryFragment.newInstance(position + 1)).commit();
			break;
		case 3:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					CategoriesFragment.newInstance(position + 1)).commit();
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.expenses);
			break;
		case 2:
			mTitle = getString(R.string.groups);
			break;
		case 3:
			mTitle = getString(R.string.history);
			break;
		case 4:
			mTitle = getString(R.string.categories);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.expense, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
