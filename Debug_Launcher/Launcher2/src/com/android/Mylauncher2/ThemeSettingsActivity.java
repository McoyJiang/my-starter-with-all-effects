package com.android.Mylauncher2;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class ThemeSettingsActivity extends ListActivity implements OnItemClickListener {
	private PackageManager pm;
	List<ResolveInfo> mApps = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		pm = getPackageManager();
		initAppsList();

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getStrings(mApps)));
		
		getListView().setOnItemClickListener(this);
	}

	private String[] getStrings(List<ResolveInfo> mApps2) {
		String[] labels = new String[mApps2.size()];
		for (int i = 0; i < mApps2.size(); i ++) {
			labels[i] = (String) mApps2.get(i).loadLabel(pm);
		}
		return labels;
	}

	private void initAppsList() {
		final Intent themeIntent = new Intent("mcoy.intent.action.THEME", null);
		mApps = pm.queryIntentActivities(themeIntent, 0);
		Log.e("XIN", "the apps.size() is " + mApps.size());
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
		String packageName = mApps.get(position).activityInfo.packageName;
		Log.e("XIN", "the packageName is " + packageName);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sp.edit().putString(EffectSettings.CURRENT_THEME_PACKAGE, packageName).commit();
		
		Intent intent = new Intent(LauncherModel.MYLAUNCHER_THEME_CHANGED);
		sendBroadcast(intent);
		finish();
	}
}
