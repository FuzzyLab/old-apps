package com.fuzzylabs.financialcalculator;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import com.fuzzylabs.financialcalculator.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RecurringFragment extends Fragment implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static RecurringFragment recurringFragment = null;
	private ActionBar actionBar;
	
	public static Fragment newInstance(int sectionNumber) {
		if(recurringFragment == null)
			recurringFragment = new RecurringFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		recurringFragment.setArguments(args);
		return recurringFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_recurring, container, false);
		actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.recurringdepositcalculator);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		mViewPager = (ViewPager) rootView.findViewById(R.id.pagerrecurring);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		actionBar.selectTab(actionBar.getTabAt(0));
		return rootView;
	}
	
	@Override
	public void onDestroy() {
	    actionBar.removeAllTabs();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    super.onDestroy();
	}
	
	@Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = null;
			if (position == 0) {
				fragment = SimpleRecurringFragment.newInstance();
			} else if (position == 1) {
				fragment = SpecialRecurringFragment.newInstance();
			}
			return fragment;

		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_simplerecurring);
			case 1:
				return getString(R.string.title_specialrecurring);
			}
			return null;
		}
	}

	private static class SimpleRecurringFragment extends Fragment {
		private EditText instalmentView;
		private EditText rateView;
		private EditText yearView;
		private EditText monthView;
		private TextView totalView;
		private TextView maturityView;
		private TextView interestView;
		private Button calculate;
		private Button reset;
		private static SimpleRecurringFragment simpleFragment = null;
		
		public static Fragment newInstance() {
			if(simpleFragment == null)
				simpleFragment = new SimpleRecurringFragment();
			return simpleFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.simple_recurring,
					container, false);
			instalmentView = (EditText) (rootView
					.findViewById(R.id.instalment_recurring_s));
			rateView = (EditText) (rootView.findViewById(R.id.rate_recurring_s));
			yearView = (EditText) (rootView.findViewById(R.id.year_recurring_s));
			monthView = (EditText) (rootView
					.findViewById(R.id.month_recurring_s));
			totalView = (TextView) (rootView
					.findViewById(R.id.totalDeposit_recurring_s));
			maturityView = (TextView) (rootView
					.findViewById(R.id.maturity_recurring_s));
			interestView = (TextView) (rootView
					.findViewById(R.id.interest_recurring_s));
			calculate = (Button) rootView
					.findViewById(R.id.calculate_recurring_s);
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					String instalmentText = instalmentView.getText().toString();
					String rateText = rateView.getText().toString();
					String yearText = yearView.getText().toString();
					String monthText = monthView.getText().toString();

					double instalment;
					if (instalmentText.equals(""))
						instalment = 0;
					else
						instalment = Double.parseDouble(instalmentText);

					double rate;
					if (rateText.equals(""))
						rate = 0;
					else
						rate = Double.parseDouble(rateText);

					int year;
					if (yearText.equals(""))
						year = 0;
					else
						year = Integer.parseInt(yearText);

					int month;
					if (monthText.equals(""))
						month = 0;
					else
						month = Integer.parseInt(monthText);

					double r = rate / 400;
					int m = (year * 12) + month;
					int n = m / 3;

					double maturity = 0.0;
					double totalDeposit = 0.0;
					double interest = 0.0;

					if (instalment == 0) {
						r = 0;
						n = 0;
						maturity = 0;
					} else if (n == 0) {
						instalment = 0;
						r = 0;
						maturity = 0;
					} else if (r == 0) {
						maturity = instalment * m;
					} else {
						double x = 1.00;

						for (int i = 0; i < n; i++) {
							x = x * (1 + r);
						}

						double numo = x - 1.0;
						double pow = Math.pow((1 + r), (1.0 / 3.0));
						double neg = 1.0 / pow;
						double prematurity = instalment * numo / (1.0 - neg);
						double extra = m % 3;
						maturity = prematurity * (1 + extra * rate / 1200);
						for (int i = 1; i <= extra; i++) {
							maturity += instalment * (1 + i * rate / 1200);
						}

						totalDeposit = instalment * m;
						interest = maturity - totalDeposit;

						DecimalFormat df = new DecimalFormat("#.##");
						maturityView.setText(df.format(maturity).toString());
						interestView.setText(df.format(interest).toString());
						totalView.setText(df.format(totalDeposit).toString());
					}
				}
			});

			reset = (Button) rootView.findViewById(R.id.reset_recurring_s);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					instalmentView.setText("");
					rateView.setText("");
					yearView.setText("");
					monthView.setText("");
					maturityView.setText("");
					totalView.setText("");
					interestView.setText("");
				}

			});

			return rootView;
		}
	}

	private static class SpecialRecurringFragment extends Fragment {

		private EditText instalmentView;
		private EditText rateView;
		private EditText yearView;
		private EditText monthView;
		private TextView cautionView;
		private TextView totalView;
		private TextView maturityView;
		private TextView interestView;
		private Button calculate;
		private Button reset;
		private static SpecialRecurringFragment specialFragment = null;
		
		public static Fragment newInstance() {
			if(specialFragment == null)
				specialFragment = new SpecialRecurringFragment();
			return specialFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.special_recurring,
					container, false);
			instalmentView = (EditText) (rootView
					.findViewById(R.id.instalment_recurring_p));
			rateView = (EditText) (rootView.findViewById(R.id.rate_recurring_p));
			yearView = (EditText) (rootView.findViewById(R.id.year_recurring_p));
			monthView = (EditText) (rootView
					.findViewById(R.id.month_recurring_p));
			cautionView = (TextView) (rootView
					.findViewById(R.id.caution_recurring_p));
			totalView = (TextView) (rootView
					.findViewById(R.id.totalDeposit_recurring_p));
			maturityView = (TextView) (rootView
					.findViewById(R.id.maturity_recurring_p));
			interestView = (TextView) (rootView
					.findViewById(R.id.interest_recurring_p));

			calculate = (Button) rootView
					.findViewById(R.id.calculate_recurring_p);
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					cautionView.setText("");
					String instalmentText = instalmentView.getText().toString();
					String rateText = rateView.getText().toString();
					String yearText = yearView.getText().toString();
					String monthText = monthView.getText().toString();

					double instalment;
					if (instalmentText.equals(""))
						instalment = 0;
					else
						instalment = Double.parseDouble(instalmentText);

					double rate;
					if (rateText.equals(""))
						rate = 0;
					else
						rate = Double.parseDouble(rateText);

					int year;
					if (yearText.equals(""))
						year = 0;
					else
						year = Integer.parseInt(yearText);

					int month;
					if (monthText.equals(""))
						month = 0;
					else
						month = Integer.parseInt(monthText);

					double r = rate / 400;
					int n = ((year * 12) + month) / 3;

					double maturity;
					double totalDeposit;
					double interest;

					if (month % 3 != 0) {
						cautionView
								.setText("!! Month must be a multiple of 3 !!");
						maturityView.setText("");
						interestView.setText("");
						totalView.setText("");
					} else {
						if (instalment == 0) {
							r = 0;
							n = 0;
							maturity = 0;
						} else if (n == 0) {
							instalment = 0;
							r = 0;
							maturity = 0;
						} else if (r == 0) {
							maturity = instalment * n;
						} else {
							double x = 1.00;

							for (int i = 0; i < n; i++) {
								x = x * (1 + r);
							}

							double numo = x - 1.0;

							maturity = instalment * (1 + r) * numo / r;

						}

						totalDeposit = instalment * n;
						interest = maturity - totalDeposit;

						DecimalFormat df = new DecimalFormat("#.##");
						maturityView.setText(df.format(maturity).toString());
						interestView.setText(df.format(interest).toString());
						totalView.setText(df.format(totalDeposit).toString());
					}
				}
			});

			reset = (Button) rootView.findViewById(R.id.reset_recurring_p);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					instalmentView.setText("");
					rateView.setText("");
					yearView.setText("");
					monthView.setText("");
					maturityView.setText("");
					totalView.setText("");
					interestView.setText("");
				}

			});

			return rootView;
		}
	}

}
