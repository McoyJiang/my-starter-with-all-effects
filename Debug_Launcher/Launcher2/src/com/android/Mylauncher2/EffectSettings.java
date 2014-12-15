package com.android.Mylauncher2;


import com.android.Mylauncher.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.preference.ListPreference;

public class EffectSettings extends PreferenceActivity
            implements OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_WORKSPACE_EFFECT = "workspace_effect";
    //mcoy add for apps sort settings begin
    public static final String KEY_APPS_SORT = "apps_sort";
    public static final String APPS_SORT_EFFECT_SHORT_NAME = "0";
    public static final String APPS_SORT_EFFECT_INSTASLLED_TIME = "1";
    //mcoy add end
    

    /* Workspace effects values --- workspace_effect_values */
    public static final String WORKSPACE_EFFECT_DEFAULT = "Default";
    public static final String WORKSPACE_EFFECT_CUBE = "Cube";
    public static final String WORKSPACE_EFFECT_FLIP = "Flip";
    public static final String WORKSPACE_EFFECT_WAVE = "Wave";
    public static final String WORKSPACE_EFFECT_WINDMILLS = "Windmills";
    public static final String WORKSPACE_EFFECT_EXTRUSION = "Extrusion";
    
	// added by mcoy for workspace effects
	String mNewestWorkspaceEffect;
	
	//mcoy add for apps sort settings begin
	private ListPreference mComparatorChangePreference;
	//mcoy add end
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.effect_settings);
        
        //mcoy add for apps sort settings begin
        mComparatorChangePreference = (ListPreference) findPreference(KEY_APPS_SORT);
        String str = getAppSortSettignsValue(getApplicationContext());
        Log.e("JIANG", "the str is " + str);
        mComparatorChangePreference.setValue(str);
        //mcoy add end
        
        /* Set Default */
        PreferenceManager.setDefaultValues(this, R.xml.effect_settings, false);
        /* display summary */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        updateSummary(this, sp, KEY_PREF_WORKSPACE_EFFECT);
        updateSummary(this, sp, KEY_APPS_SORT);
    }

    @Override
    protected void onPause() {        
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.e("JIANG", "onSharedPreferenceChanged");
		if (KEY_PREF_WORKSPACE_EFFECT.equals(key)) {
			updateSummary(this, sharedPreferences, key);

			final Intent intent = new Intent(
					LauncherModel.EFFECT_CHANGED_ACTION);
			sendBroadcast(intent);
			finish();
		} 
		//mcoy add for apps sort settings begin
		else if (KEY_APPS_SORT.equals(key)) {
			updateSummary(this, sharedPreferences, key);
			final Intent intent = new Intent(LauncherModel.APPS_SORT_BY_ACTION);
			String value = sharedPreferences.getString(KEY_APPS_SORT, APPS_SORT_EFFECT_SHORT_NAME);
			intent.putExtra(LauncherModel.APPS_SORT_BY_COMPARATOR_ID,
					Integer.parseInt(value));
			sendBroadcast(intent);
			finish();
		}
		//mcoy add end
	}
    
    private void updateSummary(Context ctx, SharedPreferences sp, String key) {
        String []effects = null;
        String []effectValues = null;
        String defaultValue = null;
        Preference pref = findPreference(key);
        String value = sp.getString(key, "");
        Log.e("JIANG", "EffectSettings---updateSummary--key is " + key + " value is " + value);
        int idx;
        
        if (key.equals(KEY_PREF_WORKSPACE_EFFECT)) {
            effects = ctx.getResources().getStringArray(R.array.workspace_effect_entries);
            effectValues = ctx.getResources().getStringArray(R.array.workspace_effect_values);
            defaultValue = WORKSPACE_EFFECT_DEFAULT;
        } else if(KEY_APPS_SORT.equals(key)) {
        	effects = ctx.getResources().getStringArray(R.array.entry_apps_sort);
            effectValues = ctx.getResources().getStringArray(R.array.entryvalues_apps_sort);
            defaultValue = APPS_SORT_EFFECT_SHORT_NAME;
        }

        if (value.equals("")) { 
            value = defaultValue;
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(key, value);
            edit.commit();
        }
        
        for (idx =0; idx < effectValues.length; idx++) {
            if (value.equals(effectValues[idx]))
                break;
        }
        
        if (idx == effectValues.length) {
            return; //ERROR
        }
		
		Log.d("JIANG", "EffectSettings -> updateSummary(), value = " + value + ", mNewestWorkspaceEffect = " + mNewestWorkspaceEffect);
        pref.setSummary(effects[idx]);
    }
    
    public static  String getWorkspaeEffect(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String effect = sp.getString(KEY_PREF_WORKSPACE_EFFECT, WORKSPACE_EFFECT_DEFAULT);
        return effect;
    }
    
    //mcoy add for apps sort settings begin
    public String getAppSortSettignsValue(Context ctx) {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String effect = sp.getString(KEY_APPS_SORT, APPS_SORT_EFFECT_SHORT_NAME);
        return effect;
    }
    //mcoy add end

	public PreferenceManager getEffectPreferenceManager() {
		return getPreferenceManager();
	}

}
