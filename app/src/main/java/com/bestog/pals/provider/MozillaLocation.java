package com.bestog.pals.provider;

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
 * Class: Mozilla Location Service
 * Link: https://location.services.mozilla.com
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class MozillaLocation extends LocationProvider {

  private final String _apiUrl;
  private final List<HashMap<String, String>> _cellTowers;
  private final List<HashMap<String, String>> _wifiSpots;

  public MozillaLocation(String apiUrl, List<HashMap<String, String>> cellTowers, List<HashMap<String, String>> wifiSpots) {
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
      result.put("mobileCountryCode", Integer.parseInt(cell.get("mnc")));
      result.put("mobileNetworkCode", Integer.parseInt(cell.get("mcc")));
      result.put("locationAreaCode", Integer.parseInt(cell.get("lac")));
      result.put("cellId", Integer.parseInt(cell.get("cid")));
      if (cell.containsKey("dbm")) {
        result.put("signalStrength", Integer.parseInt(cell.get("dbm")));
      }
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
      result.put("channel", Integer.parseInt(wifi.get("channel")));
      result.put("frequency", Integer.parseInt(wifi.get("frequency")));
      result.put("signalStrength", Integer.parseInt(wifi.get("signal")));
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
        if (jResponse.has("location") && jResponse.getDouble("accuracy") < 40000.0d) {
          JSONObject jsonObject = jResponse.getJSONObject("location");
          setLatitude(jsonObject.getDouble("lat"));
          setLongitude(jsonObject.getDouble("lng"));
          setAccuracy(jResponse.getInt("accuracy"));
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
    try {
      if (!_cellTowers.isEmpty()) {
        JSONArray cellArray = new JSONArray();
        for (HashMap<String, String> cell : _cellTowers) {
          if (!cell.get("cid").equals(LocationProvider.UNKNOWN_CELLID)) {
            cellArray.put(convertCell(cell));
          }
        }
        request.put("cellTowers", cellArray);
      }
      if (!_wifiSpots.isEmpty()) {
        JSONArray wifiArray = new JSONArray();
        for (HashMap<String, String> wifi : _wifiSpots) {
          wifiArray.put(convertWifi(wifi));
        }
        request.put("wifiAccessPoints", wifiArray);
      }
    } catch (JSONException e) {
      // @todo better logging
      e.printStackTrace();
    }
    String message = request.toString();
    InputStream inputStream = null;
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(_apiUrl).openConnection();
      conn.setReadTimeout(10000);
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setFixedLengthStreamingMode(message.getBytes().length);
      conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      conn.connect();
      OutputStream os = new BufferedOutputStream(conn.getOutputStream());
      os.write(message.getBytes());
      os.flush();
      inputStream = conn.getInputStream();
      os.close();
    } catch (IOException e) {
      // @todo better logging
      e.printStackTrace();
    }
    return Util.streamToString(inputStream);
  }
}
