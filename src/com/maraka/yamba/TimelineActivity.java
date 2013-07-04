package com.maraka.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TimelineActivity extends Activity {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor; //
	ListView listTimeline; //
	TimelineAdapter adapter;//
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
	DbHelper.C_TEXT }; //
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText }; //


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline); //
		// Connect to database
		dbHelper = new DbHelper(this); //
		db = dbHelper.getReadableDatabase();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		db.close(); //
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Get the data from the database
		cursor = db.query(DbHelper.TABLE, null, null, null, null, null,
				DbHelper.C_CREATED_AT + " DESC"); //
		startManagingCursor(cursor); //

		// Create the adapter
		adapter = new TimelineAdapter(this, cursor); //
		listTimeline.setAdapter(adapter); //

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

}
