package com.fuzzylabs.expensemanager;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuzzylabs.expensemanager.R;

public class ExpenseFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static ExpenseFragment newInstance(int sectionNumber) {
		ExpenseFragment fragment = new ExpenseFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ExpenseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_expense,
				container, false);
		TextView expenseLabel = (TextView)rootView.findViewById(R.id.expense_label);
		expenseLabel.setText("Expense Changed");
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ExpenseActivity) activity).onSectionAttached(getArguments()
				.getInt(ARG_SECTION_NUMBER));
	}
}
