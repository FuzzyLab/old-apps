
package com.fuzzylabs.financialcalculator;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

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

public class FcnrFragment extends Fragment implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static FcnrFragment fcnrFragment = null;
	private ActionBar actionBar;
	
	public static Fragment newInstance(int sectionNumber) {
		if(fcnrFragment == null)
			fcnrFragment = new FcnrFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fcnrFragment.setArguments(args);
		return fcnrFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fcnr, container, false);
		actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(R.string.fcnrcalculator);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		mViewPager = (ViewPager) rootView.findViewById(R.id.pagerfcnr);
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
				fragment = CcipSectionFragment.newInstance();
			} else if (position == 1) {
				fragment = MonthlySectionFragment.newInstance();
			} else if (position == 2) {
				fragment = QuarterlySectionFragment.newInstance();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_ccipfcnr);
			case 1:
				return getString(R.string.title_monthlyfcnr);
			case 2:
				return getString(R.string.title_quarterlyfcnr);
			}
			return null;
		}
	}

	private static class CcipSectionFragment extends Fragment {

		private EditText faceView;
		private EditText rateView;
		private EditText yearView;
		private EditText monthView;
		private TextView issueView;
		private TextView interestView;
		private TextView caution;
		private Button calculate;
		private Button reset;
		private static CcipSectionFragment ccipFragment = null;
		
		public static Fragment newInstance() {
			if(ccipFragment == null)
				ccipFragment = new CcipSectionFragment();
			return ccipFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.ccip_fcnr, container,
					false);
			faceView = (EditText) rootView.findViewById(R.id.face_ccipfcnr);
			rateView = (EditText) rootView.findViewById(R.id.rate_ccipfcnr);
			yearView = (EditText) rootView.findViewById(R.id.year_ccipfcnr);
			monthView = (EditText) rootView.findViewById(R.id.month_ccipfcnr);
			caution = (TextView) rootView.findViewById(R.id.caution_ccipfcnr);
			issueView = (TextView) rootView.findViewById(R.id.issue_ccipfcnr);
			interestView = (TextView) rootView
					.findViewById(R.id.interest_ccipfcnr);
			calculate = (Button) rootView.findViewById(R.id.calculate_ccipfcnr);
			reset = (Button) rootView.findViewById(R.id.reset_ccipfcnr);
			
			calculate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					caution.setText("");
					String faceText = faceView.getText().toString();
					String rateText = rateView.getText().toString();
					String yearText = yearView.getText().toString();
					String monthText = monthView.getText().toString();

					double face;
					if (faceText.equals(""))
						face = 0;
					else
						face = Double.parseDouble(faceText);

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

					double r = rate / 200;
					int n = ((year * 12) + month) / 6;
					double issue = 0.0;
					double interest = 0.0;

					if (month % 6 != 0) {
						caution.setText("!! Month must be a multiple of 6 !!");
						issueView.setText("");
						interestView.setText("");
					} else {
						if (face == 0) {
							r = 0;
							n = 0;
							issue = 0;
						} else if (n == 0) {
							face = 0;
							r = 0;
							issue = 0;
						} else if (r == 0) {
							issue = face;
						} else {
							double x = 1.00;

							for (int i = 0; i < n; i++) {
								x = x * (1 + r);
							}
							issue = face / x;
							interest = face - issue;
						}

						DecimalFormat df = new DecimalFormat("#.###");
						issueView.setText(df.format(issue).toString());
						interestView.setText(df.format(interest).toString());
					}
				}
			});
			
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					faceView.setText("");
					rateView.setText("");
					yearView.setText("");
					monthView.setText("");
					issueView.setText("");
					interestView.setText("");					
				}
			});

			return rootView;
		}
	}

	private static class MonthlySectionFragment extends Fragment {

		private EditText principalView;
		private EditText rateView;
		private TextView discrateView;
		private Button calculate;
		private Button reset;
		private static MonthlySectionFragment monthlyFragment = null;
		
		public static Fragment newInstance() {
			if(monthlyFragment == null)
				monthlyFragment = new MonthlySectionFragment();
			return monthlyFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.monthly_fcnr, container,
					false);
			principalView = (EditText) rootView.findViewById(R.id.principal_monthlyfcnr);
			rateView = (EditText) rootView.findViewById(R.id.rate_monthlyfcnr);
			discrateView = (TextView) rootView.findViewById(R.id.discinterest_monthlyfcnr);
			calculate = (Button) rootView.findViewById(R.id.calculate_monthlyfcnr);
			reset = (Button) rootView.findViewById(R.id.reset_monthlyfcnr);
			
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String principalText = principalView.getText().toString();
					String rateText = rateView.getText().toString();

					double principal;
					if (principalText.equals(""))
						principal = 0;
					else
						principal = Double.parseDouble(principalText);

					double rate;
					if (rateText.equals(""))
						rate = 0;
					else
						rate = Double.parseDouble(rateText);
					
					double r = rate/200;
					double s = rate/1200;
					
					double deno = Math.pow((1+s), 6) - 1;
					double disc = principal*r*s/deno;
					
					DecimalFormat df = new DecimalFormat("#.###");
					discrateView.setText(String.valueOf(df.format(disc)));
				}
			});
			
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					principalView.setText("");
					rateView.setText("");
					discrateView.setText("");
				}
			});
			
			return rootView;
		}
	}

	private static class QuarterlySectionFragment extends Fragment {

		private EditText principalView;
		private EditText rateView;
		private TextView discrateView;
		private Button calculate;
		private Button reset;
		private static QuarterlySectionFragment quarterlyFragment = null;
		
		public static Fragment newInstance() {
			if(quarterlyFragment == null)
				quarterlyFragment = new QuarterlySectionFragment();
			return quarterlyFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.quarterly_fcnr,
					container, false);
			principalView = (EditText) rootView.findViewById(R.id.principal_quarterlyfcnr);
			rateView = (EditText) rootView.findViewById(R.id.rate_quarterlyfcnr);
			discrateView = (TextView) rootView.findViewById(R.id.discinterest_quarterlyfcnr);
			calculate = (Button) rootView.findViewById(R.id.calculate_quarterlyfcnr);
			reset = (Button) rootView.findViewById(R.id.reset_quarterlyfcnr);
			
			calculate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String principalText = principalView.getText().toString();
					String rateText = rateView.getText().toString();

					double principal;
					if (principalText.equals(""))
						principal = 0;
					else
						principal = Double.parseDouble(principalText);

					double rate;
					if (rateText.equals(""))
						rate = 0;
					else
						rate = Double.parseDouble(rateText);
					
					double r = rate/200;
					double s = rate/400;
					
					double disc = principal*r/(s+2);
					
					DecimalFormat df = new DecimalFormat("#.###");
					discrateView.setText(String.valueOf(df.format(disc)));
				}
				
			});
			
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					principalView.setText("");
					rateView.setText("");
					discrateView.setText("");
				}
			});
			
			return rootView;
		}
	}

}
