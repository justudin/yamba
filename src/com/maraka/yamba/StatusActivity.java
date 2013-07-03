package com.maraka.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener,
		TextWatcher, OnSharedPreferenceChangeListener {
	SharedPreferences prefs;
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		// Find views
		editText = (EditText) findViewById(R.id.editText); //
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);
		textCount = (TextView) findViewById(R.id.textCount); //
		textCount.setText(Integer.toString(140)); //
		textCount.setTextColor(Color.GREEN); //
		editText.addTextChangedListener(this); //

		prefs = PreferenceManager.getDefaultSharedPreferences(this); //
		prefs.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemServiceStart:
			startService(new Intent(this, UpdaterService.class)); //
			break;
		case R.id.itemServiceStop:
			stopService(new Intent(this, UpdaterService.class)); //
			break;

		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)); //
			break;
		}
		return true;
		//
	}

	@Override
	public void onClick(View arg0) {
		String status = editText.getText().toString();
		new PostToTwitter().execute(status); //
		Log.d(TAG, "onClicked");
	}

	// Asynchronously posts to twitter
	class PostToTwitter extends AsyncTask<String, Integer, String> { //
		// Called to initiate the background activity
		@Override
		protected String doInBackground(String... statuses) { //
			// Update twitter status
			try {
				YambaApplication yamba = ((YambaApplication) getApplication());
				Twitter.Status status = yamba.getTwitter().setStatus(
						editText.getText().toString());
				return status.text;
			} catch (TwitterException e) {
				Log.e(TAG, "Failed to connect to twitter service", e);
				return "Failed to post";
			}
		}

		// Called when there's a status to be updated
		@Override
		protected void onProgressUpdate(Integer... values) { //
			super.onProgressUpdate(values);
			// Not used in this case
		}

		// Called once the background activity has completed
		@Override
		protected void onPostExecute(String result) { //
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
			editText.setText("");
		}
	}

	// TextWatcher methods
	public void afterTextChanged(Editable statusText) { //
		int count = 140 - statusText.length(); //
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN); //
		if (count < 10)
			textCount.setTextColor(Color.YELLOW);
		if (count < 0)
			textCount.setTextColor(Color.RED);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// TODO Auto-generated method stub
		// invalidate twitter object
		twitter = null;
	}
}
