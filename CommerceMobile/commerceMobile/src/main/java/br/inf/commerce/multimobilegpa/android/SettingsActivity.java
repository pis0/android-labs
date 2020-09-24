package br.inf.commerce.multimobilegpa.android;

import javax.annotation.Nullable;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ED7D31")));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)//API de nível 18 mínimo
			bar.setHomeAsUpIndicator(R.drawable.ic_button_left_arrow);
		bar.setDisplayHomeAsUpEnabled(true);

		if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, new SettingsFragment()).commit();
		}
	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return true;
	}

	public class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}