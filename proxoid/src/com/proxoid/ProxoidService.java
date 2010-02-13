package com.proxoid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.mba.proxylight.ProxyLight;
import com.mba.proxylight.Request;
import com.mba.proxylight.RequestFilter;

public class ProxoidService extends Service {
	private static final String TAG = "ProxoidService";
	
	private static int ID = R.drawable.icon;
	
	private ProxyLight proxy = null;
	private String useragent = null;
	
	private class UserAgentRequestFilter implements RequestFilter {
		@Override
		public boolean filter(Request request) {
			if (Proxoid.USERAGENT_REPLACE.equals(useragent)) {
				request.getHeaders().put("User-Agent", "unknown");
			} else
			if (Proxoid.USERAGENT_REMOVE.equals(useragent)) {
				request.getHeaders().remove("User-Agent");
			}
			
			return false;
		}
	};
	
	private SharedPreferences getSharedPreferences() {
		return super.getSharedPreferences(Proxoid.KEY_PREFS, MODE_PRIVATE);
	} 
	
	
	@Override
	public IBinder onBind(Intent binder) {
		return new IProxoidControl.Stub() {
			@Override
			public boolean update() throws RemoteException {
				
				SharedPreferences sp = getSharedPreferences();
				
				boolean start = sp.getBoolean(Proxoid.KEY_ONOFF, false);
				int port = Integer.parseInt(sp.getString(Proxoid.KEY_PORT, "8080"));
				useragent=sp.getString(Proxoid.KEY_USERAGENT, Proxoid.USERAGENT_ASIS);
				
				if (!start) {
					doStop();
				} else {
					if (proxy==null) {
						proxy = new ProxyLight() {
							@Override
							public void debug(String message) {
								/*if (Log.isLoggable(TAG, Log.DEBUG)) {
									Log.d(TAG, message);
								}*/
								Log.e(TAG, message);
							}
							@Override
							public void error(String message, Throwable t) {
								Log.e(TAG, message, t);
							}
						};
						proxy.getRequestFilters().add(new UserAgentRequestFilter());
						proxy.setPort(port);						
					}
					
					if (proxy.getPort()!=port) {
						proxy.setPort(port);
						// C'est le seul cas ou on doit redémarrer.
						proxy.stop();
						try {
							proxy.start();
						} catch (Exception e) {
							Log.e(TAG, "", e);
							proxy.stop();
							proxy=null;
							return false;
						}
						Toast.makeText(ProxoidService.this, getResources().getString(R.string.service_restarted), Toast.LENGTH_SHORT).show();
					}
					
					if (!proxy.isRunning()) {
						// Demarrage initial.
						try {
							proxy.start();
						} catch (Exception e) {
							Log.e(TAG, "", e);
							proxy.stop();
							proxy=null;
							return false;
						}
						
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						int icon = R.drawable.icon;
						Notification notification = new Notification(icon, getResources().getString(R.string.service_running), System.currentTimeMillis());
				
						Context context = getApplicationContext();
						CharSequence contentTitle = "Proxoid";
						CharSequence contentText = getResources().getString(R.string.service_text);
						Intent notificationIntent = new Intent(ProxoidService.this, Proxoid.class);
						PendingIntent contentIntent = PendingIntent.getActivity(ProxoidService.this, 0, notificationIntent, 0);
				
						notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
						notification.flags |= Notification.FLAG_ONGOING_EVENT;
				
						mNotificationManager.notify(ID, notification);
				
						Toast.makeText(ProxoidService.this, getResources().getString(R.string.service_started), Toast.LENGTH_SHORT).show();
					}
				}
				
				return true;
			}
		};
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		setForeground(true);
	}
	
	@Override
	public void onDestroy() {
		doStop();
		super.onDestroy();
	}
	
	private void doStop() {
		if (proxy!=null && proxy.isRunning()) {
			Log.d(TAG, "stopping");
			proxy.stop();
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(ID);
			Toast.makeText(this, getResources().getString(R.string.service_stopped), Toast.LENGTH_SHORT).show();	
			// Mettre a jour la conf
			SharedPreferences sp = getSharedPreferences();
			Editor e = sp.edit();
			e.putBoolean(Proxoid.KEY_ONOFF, false);
			e.commit();
		}
	}

}
