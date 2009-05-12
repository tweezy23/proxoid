package com.proxoid;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.mba.proxylight.ProxyLight;
import com.mba.proxylight.Request;
import com.mba.proxylight.RequestFilter;

public class Proxoid extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final String TAG = "proxoid";
	
	private static final String KEY_PREFS		= "proxoidv1";
	
	private static final String KEY_ONOFF		= "onoff";
	private static final String KEY_PORT		= "port";
	private static final String KEY_USERAGENT	= "useragent";
	private static final String KEY_PROXY		= "proxy";
	
	private static final String USERAGENT_ASIS		= "asis";
	private static final String USERAGENT_REPLACE	= "replace";
	private static final String USERAGENT_REMOVE	= "remove";

	private static final String PROXY_NONE			= "none";
	private static final String PROXY_APN			= "apn";

	private String useragent = null;
	private String remoteProxy = null;
	
	private ProxyLight proxy = null;
	
	private class UserAgentRequestFilter implements RequestFilter {
		@Override
		public boolean filter(Request request) {
			if (USERAGENT_REPLACE.equals(useragent)) {
				request.getHeaders().put("User-Agent", "unknown");
			} else
			if (USERAGENT_REMOVE.equals(useragent)) {
				request.getHeaders().remove("User-Agent");
			}
			
			return false;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sp = getSharedPreferences();

		proxy = new ProxyLight();
		proxy.getRequestFilters().add(new UserAgentRequestFilter());
		proxy.setPort(8080);

		// Les valeurs par defaut
		Editor e = sp.edit();
		
		// On ne demarre jamais tout seul ...
		e.putBoolean(KEY_ONOFF, false);
		if (sp.getString(KEY_PORT, null)==null) {
			e.putString(KEY_PORT, "8080");
		}
		if (sp.getString(KEY_USERAGENT, null)==null) {
			e.putString(KEY_USERAGENT, USERAGENT_REPLACE);
		}
//		if (sp.getString(KEY_PROXY, null)==null) {
//			e.putString(KEY_PROXY, PROXY_APN);
//		}
		
		e.commit();
		
		sp.registerOnSharedPreferenceChangeListener(this);
		addPreferencesFromResource(R.xml.prefs);
		

		
		// Initialiser d'apres les prefs actuelle
		onSharedPreferenceChanged(sp, KEY_PORT);
		onSharedPreferenceChanged(sp, KEY_USERAGENT);
//		onSharedPreferenceChanged(sp, KEY_PROXY);
	}
	
	private SharedPreferences getSharedPreferences() {
		return super.getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
	} 
	
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return getSharedPreferences();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		boolean restart = false;
		
		if (KEY_ONOFF.equals(key)) {
			if (pref.getBoolean(key, false)) {
				try {
					proxy.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				proxy.stop();
			}
		} else 
		if (KEY_PORT.equals(key)) {
			proxy.setPort(Integer.parseInt(pref.getString(KEY_PORT, "8080")));
			restart = true;
			getPreferenceScreen().findPreference(KEY_PORT).setSummary("Listening port. Current value : "+proxy.getPort());
		} else 
		if (KEY_USERAGENT.equals(key)) {
			useragent=pref.getString(KEY_USERAGENT, USERAGENT_ASIS);
			getPreferenceScreen().findPreference(KEY_USERAGENT).setSummary("User-Agent filtering. Current value : "+
					(USERAGENT_ASIS.equals(useragent)?"No filtering":
					USERAGENT_REMOVE.equals(useragent)?"Remove":"Replace with dummy")
			);
		} /* else
		if (KEY_PROXY.equals(key)) {
			remoteProxy = pref.getString(KEY_PROXY, PROXY_APN);
			if (PROXY_NONE.equals(remoteProxy)) {
				proxy.setRemoteProxy(null, -1);
			} else {
				Cursor mCursor = getContentResolver().query(Uri.parse("content://telephony/carriers"), new String[] {"proxy", "port"}, "current=1", null, null);
				if (mCursor!=null) {
					try {
				        if (mCursor.moveToFirst()) {
				            String vproxy = mCursor.getString(0);
				            if (vproxy!=null) {
				            	proxy.setRemoteProxy(vproxy, Integer.parseInt(mCursor.getString(1)));
				            }
				        }
					} finally {
						mCursor.close();
					}
				}
			}
			restart = true;
			getPreferenceScreen().findPreference(KEY_PROXY).setSummary("Use a remote proxy. Current value : "+
					(PROXY_NONE.equals(remoteProxy)?"None":
						("Use APN ("+(proxy.getRemoteProxyHost()==null?"none":(proxy.getRemoteProxyHost()+":"+proxy.getRemoteProxyPort()))+")")
					)
			);			
		} */
		
		if (restart && proxy.isRunning()) {
			try {
				proxy.stop();
				proxy.start();
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}
}