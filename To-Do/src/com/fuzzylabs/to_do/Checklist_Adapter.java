package com.fuzzylabs.to_do;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Checklist_Adapter extends SimpleCursorAdapter {

	private Context mContext;
	private Context appContext;
	private int layout;
	private Cursor cr;
	private AlertDialog cad;
	private final LayoutInflater inflater;
	private final TodoDb info;

	public Checklist_Adapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
		this.mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.cr = c;
		this.info = new TodoDb(context);
	}
	
	public Checklist_Adapter(Context context, int layout, AlertDialog cad, Cursor c,
			String[] from, int[] to) {
		this(context, layout, c, from, to);
		this.cad = cad;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(final View view, final Context context,
			final Cursor cursor) {
		super.bindView(view, context, cursor);
		final String id = cursor.getString(0);
		final String jobId = cursor.getString(1);
		final int is_checked = cursor.getInt(3);

		Button done = (Button) view.findViewById(R.id.checkDone);
		Button delete = (Button) view.findViewById(R.id.checkDelete);
		if (is_checked == 1) {
			done.setBackgroundResource(R.drawable.donedark);
		} else {
			done.setBackgroundResource(R.drawable.done);
		}

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View btn) {
				try {
					info.open();
					String[] columns = new String[] { info.CHECK_ID,
							info.JOB_ID, info.ITEM_NAME,
							info.IS_CHECKED };
					Cursor cur = info.ourDatabase.query(
							info.CHECKLIST_TABLE, columns,
							info.CHECK_ID + " like ?",
							new String[] { id }, null, null, null);
					cur.moveToFirst();
					int check = cur.getInt(3);
					ContentValues cv = new ContentValues();
					if (is_checked == 0) {
						cv.put(info.IS_CHECKED, 1);
						//btn.setBackgroundResource(R.drawable.donedark);
					} else {
						cv.put(info.IS_CHECKED, 0);
						//btn.setBackgroundResource(R.drawable.done);
					}
					info.ourDatabase
							.update(info.CHECKLIST_TABLE, cv,
									info.CHECK_ID + " like ?",
									new String[] { id });
					
					Cursor curse = info
							.getAllRows(jobId);
					String[] fromColumns = { info.ITEM_NAME };
					int[] toViews = { R.id.checkText };
					SimpleCursorAdapter mAdapter;
					mAdapter = new Checklist_Adapter(
							mContext,
							R.layout.checklist_item, cad,
							curse,
							fromColumns,
							toViews);
					ListView myList = (ListView) cad.findViewById(R.id.checklistView);
					myList.setAdapter(mAdapter);
					
					info.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View btn) {
				try {
					info.open();
					info.ourDatabase
							.delete(info.CHECKLIST_TABLE,
									info.CHECK_ID + " like ?",
									new String[] { id });

					Cursor curse = info
							.getAllRows(jobId);
					String[] fromColumns = { info.ITEM_NAME };
					int[] toViews = { R.id.checkText };
					SimpleCursorAdapter mAdapter;
					mAdapter = new Checklist_Adapter(
							mContext,
							R.layout.checklist_item, cad,
							curse,
							fromColumns,
							toViews);
					ListView myList = (ListView) cad.findViewById(R.id.checklistView);
					myList.setAdapter(mAdapter);
					
					info.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
