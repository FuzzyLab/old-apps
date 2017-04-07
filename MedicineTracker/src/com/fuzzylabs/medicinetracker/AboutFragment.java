package com.fuzzylabs.medicinetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ActionBar;
import android.support.v4.app.Fragment;

public class AboutFragment extends Fragment {
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static AboutFragment aboutFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(aboutFragment == null)
			aboutFragment = new AboutFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		aboutFragment.setArguments(args);
		return aboutFragment;
	}

	private AboutFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		return rootView;
	}
}
