package com.proxoid;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Proxoid extends PreferenceActivity implements OnSharedPreferenceChangeListener, ServiceConnection {

	private static final String TAG = "proxoid";
	
	protected static final String KEY_PREFS		= "proxoidv6";
	
	protected static final String KEY_ONOFF		= "onoff";
	protected static final String KEY_PORT		= "port";
	protected static final String KEY_USERAGENT	= "useragent";
	
	protected static final String USERAGENT_ASIS	= "asis";
	protected static final String USERAGENT_REPLACE	= "replace";
	protected static final String USERAGENT_REMOVE	= "remove";
	
	protected static final String COMMAND_STOP = "stop";
	protected static final String COMMAND_START = "start";
	protected static final String COMMAND_UPDATE = "update";
	
	private IProxoidControl proxoidControl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		addPreferencesFromResource(R.xml.prefs);
		
		// Initialiser d'apres les prefs actuelle
		updateLibelle(KEY_PORT);
		updateLibelle(KEY_USERAGENT);
		
		Intent svc = new Intent(this, ProxoidService.class);
		bindService(svc, this, Context.BIND_AUTO_CREATE);
		
		Toast.makeText(this, "Debuging: "+Settings.System.getString(getContentResolver(), Settings.System.DEBUG_APP), Toast.LENGTH_LONG);
	}
	
	
	private SharedPreferences getSharedPreferences() {
		return super.getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
	} 
	
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return getSharedPreferences();
	}

	private boolean recurse = false;
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

		if (!recurse) {
			Log.d(TAG, "NOTIFICATION ?");
			
			// Notifier
			boolean notifyOk = false;
			try {
				notifyOk = proxoidControl==null || proxoidControl.update();
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
			if (!notifyOk) {
				if (KEY_ONOFF.equals(key) && pref.getBoolean(KEY_ONOFF, false)) {
					try {
						recurse = true;
						SharedPreferences sp = getSharedPreferences();
						Editor e = sp.edit();
						e.putBoolean(KEY_ONOFF, false);
						e.commit();
						Toast.makeText(this, "Error. Proxoid stopped.", Toast.LENGTH_SHORT).show();
					} finally {
						recurse=false;
					}
				}
			}
		}
		
		// Mettre a jour le libelle
		updateLibelle(key);
	}
	
	private void updateLibelle(String key) {
		if (KEY_ONOFF.equals(key)) {
			return;
		}
		String summary = null;
		String value = getSharedPreferences().getString(key, null);
		if (KEY_PORT.equals(key)) {
			summary = "Listening port. Current value : "+(value==null?"8080":value);
		} else
		if (KEY_USERAGENT.equals(key)) {
			summary = "User-Agent filtering. Current value : "+
			(	USERAGENT_ASIS.equals(value)?"No filtering":
				USERAGENT_REMOVE.equals(value)?"Remove":
				"Replace with dummy"
			);
		}
		if (summary!=null) {
			PreferenceScreen ps = getPreferenceScreen();
			if (ps!=null) {
				Preference p = ps.findPreference(key);
				if (p!=null) {
					p.setSummary(summary);
				} 
			}
		}
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId()==R.id.menu_help) {
			AlertDialog d = new AlertDialog.Builder(this).create();
			d.setTitle(null);
			d.setMessage("Please, go to\nhttp://code.google.com/p/proxoid/\nfrom your computer.");
			d.setButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			d.show();			
		}
		return super.onMenuItemSelected(featureId, item);
	}


	@Override
	public void onServiceConnected(ComponentName cn, IBinder binder) {
		proxoidControl=(IProxoidControl)binder;
		if (proxoidControl!=null) {
			try {
				proxoidControl.update();
			} catch (RemoteException e) {
				Log.e(TAG, "", e);
			}
		}
	}


	@Override
	public void onServiceDisconnected(ComponentName cn) {
		proxoidControl=null;
	}
	
	@Override
	protected void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==4 && proxoidControl!=null && getSharedPreferences().getBoolean(KEY_ONOFF, false)) {
			// On ne quitte pas ...
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}