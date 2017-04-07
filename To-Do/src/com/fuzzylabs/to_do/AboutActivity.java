package com.fuzzylabs.to_do;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuzzylabs.to_do.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ListView list = (ListView) findViewById(R.id.helpList);
		ArrayAdapter<String> listAdapter = new CustomListAdapter(this,
				R.layout.help_item);
		listAdapter
				.add("Add a new job. Provide the name, urgency (Very Urgent/Urgent/Meduim/Low) and due date of the job.");
		listAdapter.add("Search a job in your list on the basis of job name.");
		listAdapter
				.add("Set notification to remind you at a particular time on the due date.");
		listAdapter.add("Cancel set notification of the job.");
		listAdapter
				.add("Set Alarm to remind you at a particular time on the due date.");
		listAdapter.add("Cancel Set Alarm of the job.");
		listAdapter.add("Set Alarm Button disabled as the job is marked as done.");
		listAdapter
				.add("Set Notification Button disabled as the job is marked as done.");
		listAdapter.add("Edit the job. Resets if alarm/notification is set.");
		listAdapter.add("Maintain notes/checklist on the job.");
		listAdapter
				.add("Delete the job. Cancels any alarm/notification set for the job.");
		listAdapter
				.add("Mark the job as done. It clears the set alarm/notification for the job. Also mark checklist item done.");
		listAdapter
				.add("Mark those job as not done which have due-date for a future date(including today) and set as done. Also mark checklist item as not done.");
		listAdapter
		.add("Delete checklist item.");
		listAdapter.add("Very Urgent");
		listAdapter.add("Urgent");
		listAdapter.add("Medium");
		listAdapter.add("Low");
		list.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class CustomListAdapter extends ArrayAdapter<String> {

		public CustomListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.help_item,
						null);
			}

			((TextView) convertView.findViewById(R.id.helpText))
					.setText(getItem(position));

			if (position == 0)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.add);
			else if (position == 1)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.search);
			else if (position == 2)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.notification);
			else if (position == 3)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.notificationdark);
			else if (position == 4)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.alarm);
			else if (position == 5)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.alarmdark);
			else if (position == 6)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.alarmdisabled);
			else if (position == 7)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.notificationdisabled);
			else if (position == 8)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.edit);
			else if (position == 9)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.notes);
			else if (position == 10)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.delete);
			else if (position == 11)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.done);
			else if (position == 12)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.donedark);
			else if (position == 13)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundResource(R.drawable.reddelete);
			else if (position == 14)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundColor(0xffee0000);
			else if (position == 15)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundColor(0xff0000ee);
			else if (position == 16)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundColor(0xff00ee00);
			else if (position == 17)
				((ImageView) convertView.findViewById(R.id.helpImage))
						.setBackgroundColor(0xffeeee00);

			return convertView;
		}
	}
}
