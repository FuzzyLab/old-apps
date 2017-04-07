package com.fuzzylabs.medicinetracker;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class SettingsFragment extends Fragment {
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static String toneUri = "";
	SettingsDB settingsInfo;
	TextView toneName;
	CheckBox vibrateCheck;
	CheckBox lightCheck;
	private static SettingsFragment settingsFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(settingsFragment == null)
			settingsFragment = new SettingsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		settingsFragment.setArguments(args);
		return settingsFragment;
	}

	public SettingsFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.settings);
		
		toneName = (TextView) getActivity().findViewById(R.id.toneName);
		vibrateCheck = (CheckBox) getActivity().findViewById(R.id.vibrateCheck);
		lightCheck = (CheckBox) getActivity().findViewById(R.id.lightCheck);
		
		settingsInfo = new SettingsDB(getActivity().getApplicationContext());
		final Spinner snoozeList = (Spinner) getActivity().findViewById(R.id.snoozeList);
		Button pickTone = (Button) getActivity().findViewById(R.id.pickTone);
		Button saveSettings = (Button) getActivity().findViewById(R.id.saveSettings);
		
		int snoozeTime = 10;
		int vibrate = 0;
		int light = 0;
		try {
			settingsInfo.open();
			toneUri = settingsInfo.getTone();
			snoozeTime = settingsInfo.getSnoozeTime();
			vibrate = settingsInfo.getVibrate();
			light = settingsInfo.getLight();
			settingsInfo.close();
		} catch (Exception e) {
		}
		
		String[] arr = getResources().getStringArray(R.array.snoozelist);
		for(int i = 0; i < arr.length; i++) {
			if(Integer.parseInt(arr[i]) == snoozeTime) {
				snoozeList.setSelection(i);
				break;
			}
		}
		
		Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(toneUri));
		String title = ringtone.getTitle(getActivity());
		toneName.setText(title);
		
		if(vibrate == 1)
			vibrateCheck.setChecked(true);
		else
			vibrateCheck.setChecked(false);
		
		if(light == 1)
			lightCheck.setChecked(true);
		else
			lightCheck.setChecked(false);
		
		pickTone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(toneUri));
				startActivityForResult(intent, 5);
			}
		});
		
		saveSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int vibrate = 0;
					int light = 0;
					if(vibrateCheck.isChecked())
						vibrate = 1;
					if(lightCheck.isChecked())
						light = 1;
					
					settingsInfo.open();
					ContentValues cv = new ContentValues();
					cv.put(SettingsDB.SNOOZE_TIME, Integer.parseInt(snoozeList.getSelectedItem().toString()));
					cv.put(SettingsDB.TONE, toneUri);
					cv.put(SettingsDB.VIBRATE, vibrate);
					cv.put(SettingsDB.LIGHT, light);
					settingsInfo.ourDatabase.update(SettingsDB.SETTINGS_TABLE, cv, null, null);
					settingsInfo.close();
					
					Toast.makeText(getActivity(), "Settings Saved", Toast.LENGTH_SHORT).show();
					
				} catch (Exception e) {
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	 {
	     if (resultCode == Activity.RESULT_OK && requestCode == 5)
	     {
	          Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

	          if (uri != null)
	          {
	              this.toneUri = uri.toString();
	              Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(toneUri));
	      		  String title = ringtone.getTitle(getActivity());
	      		  toneName.setText(title);
	          }
	          else
	          {
	              this.toneUri = "";
	              toneName.setText("");
	          }
	      }            
	  }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		return rootView;
	}
}
