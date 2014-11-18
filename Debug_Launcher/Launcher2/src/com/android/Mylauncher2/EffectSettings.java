package com.android.Mylauncher2;


import com.android.Mylauncher.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.preference.ListPreference; // Added by robson

public class EffectSettings extends PreferenceActivity
            implements OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_WORKSPACE_EFFECT = "workspace_effect";
    

    /* Workspace effects values --- workspace_effect_values */
    public static final String WORKSPACE_EFFECT_DEFAULT = "Default";
    public static final String WORKSPACE_EFFECT_CUBE = "Cube";
    public static final String WORKSPACE_EFFECT_FLIP = "Flip";
    public static final String WORKSPACE_EFFECT_WAVE = "Wave";
    public static final String WORKSPACE_EFFECT_WINDMILLS = "Windmills";
    public static final String WORKSPACE_EFFECT_EXTRUSION = "Extrusion";
    
	// added by robson
	String mNewestWorkspaceEffect;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.effect_settings);
        
        /* Set Default */
        PreferenceManager.setDefaultValues(this, R.xml.effect_settings, false);
        /* display summary */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        updateSummary(this, sp, KEY_PREF_WORKSPACE_EFFECT);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(this, sharedPreferences, key);
		
		final Intent intent = new Intent(LauncherModel.EFFECT_CHANGED_ACTION);
		sendBroadcast(intent);
		finish();
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
		
		// added by robson begin
		Log.d("robson", "EffectSettings -> updateSummary(), value = " + value + ", mNewestWorkspaceEffect = " + mNewestWorkspaceEffect);
        pref.setSummary(effects[idx]);
		// added by robson end
    }
    
    public static  String getWorkspaeEffect(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String effect = sp.getString(KEY_PREF_WORKSPACE_EFFECT, WORKSPACE_EFFECT_DEFAULT);

        return effect;
    }

	public PreferenceManager getEffectPreferenceManager() {
		return getPreferenceManager();
	}
}
