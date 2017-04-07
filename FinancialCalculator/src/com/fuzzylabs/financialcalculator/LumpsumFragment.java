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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LumpsumFragment extends Fragment implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static LumpsumFragment lumpsumFragment = null;
	private ActionBar actionBar;
	
	public static Fragment newInstance(int sectionNumber) {
		if(lumpsumFragment == null)
			lumpsumFragment = new LumpsumFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		lumpsumFragment.setArguments(args);
		return lumpsumFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lumpsum, container, false);
		actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.lumpsumannuitydepositcalculator);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		mViewPager = (ViewPager) rootView.findViewById(R.id.pagerlumpsum);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position == 0) {
				fragment = MonthlyLumpsumFragment.newInstance();
			} else if (position == 1) {
				fragment = QuarterlyLumpsumFragment.newInstance();
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
				return getString(R.string.title_monthlylumpsum);
			case 1:
				return getString(R.string.title_quarterlylumpsum);
			}
			return null;
		}
	}

	private static class MonthlyLumpsumFragment extends Fragment {
		
		private EditText lumpsumView;
		private EditText rateView;
		private EditText yearView;
		private EditText monthView;
		private TextView cautionView;
		private TextView repaymentView;
		private Button calculate;
		private Button reset;
		private static MonthlyLumpsumFragment monthlyFragment = null;
		
		public static Fragment newInstance() {
			if(monthlyFragment == null)
				monthlyFragment = new MonthlyLumpsumFragment();
			return monthlyFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.monthly_lumpsum,
					container, false);
			lumpsumView = (EditText) (rootView
					.findViewById(R.id.lumpsum_lumpsum_m));
			rateView = (EditText) (rootView.findViewById(R.id.rate_lumpsum_m));
			yearView = (EditText) (rootView.findViewById(R.id.year_lumpsum_m));
			monthView = (EditText) (rootView
					.findViewById(R.id.month_lumpsum_m));
			cautionView = (TextView) (rootView
					.findViewById(R.id.caution_lumpsum_m));
			repaymentView = (TextView) (rootView
					.findViewById(R.id.repayment_lumpsum_m));
			calculate = (Button) rootView
					.findViewById(R.id.calculate_lumpsum_m);
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					cautionView.setText("");
					String lumpsumText = lumpsumView.getText().toString();
					String rateText = rateView.getText().toString();
					String yearText = yearView.getText().toString();
					String monthText = monthView.getText().toString();

					double lumpsum;
					if (lumpsumText.equals(""))
						lumpsum = 0;
					else
						lumpsum = Double.parseDouble(lumpsumText);

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

					double repayment;

					if (month % 3 != 0) {
						cautionView
								.setText("!! Month must be a multiple of 3 !!");
						repaymentView.setText("");
					} else {
						if (lumpsum == 0) {
							r = 0;
							n = 0;
							repayment = 0;
						} else if (n == 0) {
							lumpsum = 0;
							r = 0;
							repayment = 0;
						} else if (r == 0) {
							repayment = 0;
							n = 0;
							lumpsum = 0;
						} else {
							double deno = Math.pow((1+r), (-1)*n);
							repayment = lumpsum*(Math.pow((1+r), 1.0/3)-1)/(1-deno);
						}

						DecimalFormat df = new DecimalFormat("#.##");
						repaymentView.setText(df.format(repayment).toString());
					}
				}
			});

			reset = (Button) rootView.findViewById(R.id.reset_lumpsum_m);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					lumpsumView.setText("");
					rateView.setText("");
					yearView.setText("");
					monthView.setText("");
					repaymentView.setText("");
				}

			});
						
			return rootView;
		}
	}

	private static class QuarterlyLumpsumFragment extends Fragment {
		
		private EditText lumpsumView;
		private EditText rateView;
		private EditText yearView;
		private EditText monthView;
		private TextView cautionView;
		private TextView repaymentView;
		private Button calculate;
		private Button reset;
		private static QuarterlyLumpsumFragment quarterlyFragment = null;
		
		public static Fragment newInstance() {
			if(quarterlyFragment == null)
				quarterlyFragment = new QuarterlyLumpsumFragment();
			return quarterlyFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.quarterly_lumpsum,
					container, false);
			lumpsumView = (EditText) (rootView
					.findViewById(R.id.lumpsum_lumpsum_q));
			rateView = (EditText) (rootView.findViewById(R.id.rate_lumpsum_q));
			yearView = (EditText) (rootView.findViewById(R.id.year_lumpsum_q));
			monthView = (EditText) (rootView
					.findViewById(R.id.month_lumpsum_q));
			cautionView = (TextView) (rootView
					.findViewById(R.id.caution_lumpsum_q));
			repaymentView = (TextView) (rootView
					.findViewById(R.id.repayment_lumpsum_q));
			
			calculate = (Button) rootView
					.findViewById(R.id.calculate_lumpsum_q);
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					cautionView.setText("");
					String lumpsumText = lumpsumView.getText().toString();
					String rateText = rateView.getText().toString();
					String yearText = yearView.getText().toString();
					String monthText = monthView.getText().toString();

					double lumpsum;
					if (lumpsumText.equals(""))
						lumpsum = 0;
					else
						lumpsum = Double.parseDouble(lumpsumText);

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

					double repayment;

					if (month % 3 != 0) {
						cautionView
								.setText("!! Month must be a multiple of 3 !!");
						repaymentView.setText("");
					} else {
						if (lumpsum == 0) {
							r = 0;
							n = 0;
							repayment = 0;
						} else if (n == 0) {
							lumpsum = 0;
							r = 0;
							repayment = 0;
						} else if (r == 0) {
							repayment = 0;
							n = 0;
							lumpsum = 0;
						} else {
							double numo = Math.pow((1+r), (-1)*n);
							repayment = lumpsum*r/(1-numo);
						}

						DecimalFormat df = new DecimalFormat("#.##");
						repaymentView.setText(df.format(repayment).toString());
					}
				}
			});

			reset = (Button) rootView.findViewById(R.id.reset_lumpsum_q);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					lumpsumView.setText("");
					rateView.setText("");
					yearView.setText("");
					monthView.setText("");
					repaymentView.setText("");
				}

			});
			
			return rootView;
		}
	}

}
