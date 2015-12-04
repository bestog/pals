package com.bestog.pals;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bestog.pals.provider.LocationProvider;
import com.bestog.pals.provider.MozillaLocation;
import com.bestog.pals.provider.OpenBMapLocation;
import com.bestog.pals.provider.OpenCellLocation;
import com.bestog.pals.provider.OpenMapLocation;
import com.bestog.pals.utils.CellScanner;
import com.bestog.pals.utils.Result;
import com.bestog.pals.utils.Util;
import com.bestog.pals.utils.WifiScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class: LocationService
 * Der Service fuer die Standortbestimmung
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class LocationService extends Service {

  private static final String NOTIFICATION = "com.bestog.pals.receiver";
  private final IBinder binder = new LocationBinder();
  private Thread thread;

  /**
   * Service wird gebinded und startet die Lokalisierung
   */
  @Override
  public IBinder onBind(Intent intent) {
    Notification notification = new Builder(this).setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_text))
        .setSmallIcon(R.drawable.app_icon)
        .setAutoCancel(false)
        .build();
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(Integer.parseInt(getString(R.string.notification_id)), notification);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    TelephonyManager cellManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    thread = new Thread(new Run(preferences, cellManager, wifiManager));
    thread.start();
    return binder;
  }

  /**
   * Service wird geloescht
   */
  @Override
  public void onDestroy() {
    if (thread != null) {
      thread.interrupt();
    }
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(Integer.parseInt(getString(R.string.notification_id)));
    super.onDestroy();
  }

  /**
   * Service wird gestartet
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    Editor editor = preferences.edit();
    if (intent != null) {
      editor.putBoolean(LocationProvider.PROVIDER_MOZILLA, intent.getBooleanExtra(LocationProvider.PROVIDER_MOZILLA, false));
      editor.putBoolean(LocationProvider.PROVIDER_OPENCELL, intent.getBooleanExtra(LocationProvider.PROVIDER_OPENCELL, false));
      editor.putBoolean(LocationProvider.PROVIDER_OPENMAP, intent.getBooleanExtra(LocationProvider.PROVIDER_OPENMAP, false));
      editor.putBoolean(LocationProvider.PROVIDER_OPENBMAP, intent.getBooleanExtra(LocationProvider.PROVIDER_OPENBMAP, false));
      editor.apply();
    }
    return Service.START_STICKY;
  }

  /**
   * Fuehre die Lokalisierung aus
   */
  private class Run implements Runnable {

    private final SharedPreferences preferences;
    private final TelephonyManager cellManager;
    private final WifiManager wifiManager;

    private Run(SharedPreferences pref, TelephonyManager cManager, WifiManager wManager) {
      preferences = pref;
      cellManager = cManager;
      wifiManager = wManager;
    }

    @Override
    public void run() {
      List<Result> list = new ArrayList<>();
      List<HashMap<String, String>> cellTowers = new CellScanner(cellManager).getTowers();
      List<HashMap<String, String>> wifiSpots = new WifiScanner(wifiManager).getSpots();

      if (preferences != null) {
        if (preferences.getBoolean(LocationProvider.PROVIDER_MOZILLA, false)) {
          MozillaLocation mozLocationCell = new MozillaLocation(
              getString(R.string.mozilla_api)
                  + getString(R.string.mozilla_key), cellTowers, wifiSpots);
          mozLocationCell.request();
          list.add(Util.collectResult(mozLocationCell));
        }
        if (preferences.getBoolean(LocationProvider.PROVIDER_OPENCELL, false)) {
          OpenCellLocation openCellLocation = new OpenCellLocation(
              getString(R.string.opencell_api) + getString(R.string.opencell_key), cellTowers);
          openCellLocation.request();
          list.add(Util.collectResult(openCellLocation));
        }
        if (preferences.getBoolean(LocationProvider.PROVIDER_OPENMAP, false)) {
          OpenMapLocation openMapLocation = new OpenMapLocation(getString(R.string.openmap_api), wifiSpots);
          openMapLocation.request();
          list.add(Util.collectResult(openMapLocation));
        }
        if (preferences.getBoolean(LocationProvider.PROVIDER_OPENBMAP, false)) {
          OpenBMapLocation openBMapLocation = new OpenBMapLocation(getString(R.string.openbmap_api), cellTowers, wifiSpots);
          openBMapLocation.request();
          list.add(Util.collectResult(openBMapLocation));
        }
        Result result = Util.bestLocation(list);
        Intent intent = new Intent(LocationService.NOTIFICATION);
        intent.putExtra("lat", (float) Util.round(result.getLatitude(), LocationProvider.COORD_PRECISION));
        intent.putExtra("lon", (float) Util.round(result.getLongitude(), LocationProvider.COORD_PRECISION));
        intent.putExtra("acc", Util.round(result.getAccuracy(), 0));
        sendBroadcast(intent);
      } else {
        Log.e("Fehlermeldung", "PALS muss vorher gestartet werden.");
      }
    }
  }

  /**
   * Custom Binder
   */
  private class LocationBinder extends Binder {
    LocationService getService() {
      return LocationService.this;
    }
  }
}
