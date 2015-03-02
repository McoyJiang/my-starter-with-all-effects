package com.android.Mylauncher2;

import java.util.ArrayList;
import java.util.List;

import com.android.Mylauncher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ThemeSettings extends Activity implements OnItemClickListener {
	
	private PackageManager pm;
	List<ResolveInfo> mApps = null;
	private List<ThemeItem> allThemeApps = new ArrayList<ThemeItem>();
	
	private GridView themeGridView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.theme_settings);
		
		pm = getPackageManager();
		initAppsList();
		
		themeGridView = (GridView) findViewById(R.id.gridview);
		themeGridView.setAdapter(new ThemeItemAdapter(this, allThemeApps));
		themeGridView.setOnItemClickListener(this);
	}
	
	/**private String[] getStrings(List<ResolveInfo> mApps2) {
		String[] labels = new String[mApps2.size()];
		for (int i = 0; i < mApps2.size(); i ++) {
			labels[i] = (String) mApps2.get(i).loadLabel(pm);
		}
		return labels;
	}*/

	private void initAppsList() {
		final Intent themeIntent = new Intent("mcoy.intent.action.THEME", null);
		mApps = pm.queryIntentActivities(themeIntent, 0);
		for(ResolveInfo ri : mApps) {
			String themePackageName = ri.activityInfo.packageName;
			Log.e("themesettings", "the themePackageName is " + themePackageName);
			try {
				Context themeAppContext = getApplicationContext().createPackageContext(
						themePackageName, Context.CONTEXT_IGNORE_SECURITY);
				Resources newResources = themeAppContext.getResources();
				int newIconId = newResources.getIdentifier(
						ri.activityInfo.packageName + ":drawable/theme_preview", null,
						null);
				//allThemeApps.add(new ThemeItem(ri.loadLabel(pm).toString(), ri.loadIcon(pm)));
				ThemeItem ti = new ThemeItem(ri.loadLabel(pm).toString(),
						newResources.getDrawable(newIconId)); 
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String sharedPackageName = sp.getString(
						EffectSettings.CURRENT_THEME_PACKAGE,
						EffectSettings.DEFAULT_THEME_PACKAGE);
				Log.e("themesettings", "the sharedPackageName is " + sharedPackageName);
				if(themePackageName.equals(sharedPackageName)) {
					ti.setCurrentTheme(true);
				}
				allThemeApps.add(ti);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		Log.e("XIN", "the allThemeApps.size() is " + allThemeApps.size());
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
