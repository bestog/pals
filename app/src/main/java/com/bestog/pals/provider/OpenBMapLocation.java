package com.bestog.pals.provider;

import android.util.Log;

import com.bestog.pals.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Class: Openbmap
 * Link: http://www.radiocells.org
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class OpenBMapLocation extends LocationProvider {

  private final String _apiUrl;
  private final List<HashMap<String, String>> _cellTowers;
  private final List<HashMap<String, String>> _wifiSpots;

  public OpenBMapLocation(String apiUrl, List<HashMap<String, String>> cellTowers, List<HashMap<String, String>> wifiSpots) {
    _cellTowers = cellTowers;
    _wifiSpots = wifiSpots;
    _apiUrl = apiUrl;
  }

  /**
   * Konvertiert eine CellInfo in das entsprechende Format um
   *
   * @param cell HashMap CellInfo
   * @return JSONObject
   */
  private static JSONObject convertCell(HashMap<String, String> cell) {
    JSONObject result = new JSONObject();
    try {
      result.put("cellId", cell.get("cid"));
      result.put("locationAreaCode", cell.get("lac"));
      result.put("mobileCountryCode", cell.get("mcc"));
      result.put("mobileNetworkCode", cell.get("mnc"));
    } catch (JSONException e) {
      // @todo better logging
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Konvertiert ein Wifi in das entsprechende Format um
   *
   * @param wifi HashMap Wifi
   * @return JSONObject
   */
  private static JSONObject convertWifi(HashMap<String, String> wifi) {
    JSONObject result = new JSONObject();
    try {
      result.put("macAddress", wifi.get("key"));
      result.put("signalStrength", "-54");
    } catch (JSONException e) {
      // @todo better logging
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Fuehrt die Anfrage durch
   */
  public void request() {
    try {
      String response = response();
      JSONObject jResponse = new JSONObject(response);
      if (!jResponse.has("error")) {
        if (jResponse.has("location") && jResponse.getJSONObject("location").has("lat")) {
          if (jResponse.has("location") && jResponse.getDouble("accuracy") < 30000.0d) {
            JSONObject jsonObject = jResponse.getJSONObject("location");
            setLatitude(jsonObject.getDouble("lat"));
            setLongitude(jsonObject.getDouble("lng"));
            setAccuracy(jResponse.getInt("accuracy"));
          }
        }
      }
    } catch (JSONException e) {
      // @todo better logging
      e.printStackTrace();
    }
  }

  /**
   * Bereitet die Anfrage vor
   *
   * @return String
   */
  private String response() {
    JSONObject request = new JSONObject();
    if (!_wifiSpots.isEmpty()) {
      JSONArray wifiArray = new JSONArray();
      for (HashMap<String, String> wifi : _wifiSpots) {
        wifiArray.put(convertWifi(wifi));
      }
      try {
        request.put("wifiAccessPoints", wifiArray);
      } catch (JSONException e) {
        // @todo better logging
        e.printStackTrace();
      }
    } else if (!_cellTowers.isEmpty()) {
      JSONArray cellArray = new JSONArray();
      for (HashMap<String, String> cell : _cellTowers) {
        if (!cell.get("cid").equals(LocationProvider.UNKNOWN_CELLID)) {
          cellArray.put(convertCell(cell));
        }
      }
      try {
        request.put("cellTowers", cellArray);
      } catch (JSONException e) {
        // @todo better logging
        e.printStackTrace();
      }
    }
    String message = request.toString();
    Log.d("OpenBMap", message);
    InputStream inputStream = null;
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(_apiUrl).openConnection();
      conn.setReadTimeout(10000);
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setFixedLengthStreamingMode(message.getBytes().length);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.connect();
      OutputStream os = new BufferedOutputStream(conn.getOutputStream());
      os.write(message.getBytes());
      os.flush();
      inputStream = conn.getInputStream();
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Util.streamToString(inputStream);
  }
}
