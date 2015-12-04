package com.bestog.pals.provider;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class: OpenCellID
 * Link: http://opencellid.org
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class OpenCellLocation extends LocationProvider {

  private final String _apiUrl;
  private final List<HashMap<String, String>> _cellTowers;
  private final List<String> responseList = new ArrayList<>();
  private int valid = 0, _acc = 0;
  private double _lat = 0.0d, _lon = 0.0d;

  public OpenCellLocation(String apiUrl, List<HashMap<String, String>> cellTowers) {
    _cellTowers = cellTowers;
    _apiUrl = apiUrl;
  }

  /**
   * Fuehrt die Anfrage durch
   */
  public void request() {
    Log.d("AAA", "assadasd");
    for (HashMap<String, String> cellInfo : _cellTowers) {
      Log.d("AAA", cellInfo.toString());
      String url = _apiUrl + "&mcc=" + cellInfo.get("mnc") + "&mnc=" + cellInfo
          .get("mcc") + "&cellid=" + cellInfo.get("cid") + "&lac=" + cellInfo
          .get("lac") + "&format=json";
      try {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        //HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        String result = response.toString();
        Log.d("AAAA", result + responseCode);
        responseList.add(result);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    for (String json : responseList) {
      Log.d("AAA", json);
      double[] doubles = response(json);
      if (doubles[0] > 0.0d) {
        ++valid;
        _lat += doubles[1];
        _lon += doubles[2];
        _acc += doubles[3];
      }
    }

    if (valid > 0) {
      setLatitude(_lat / valid);
      setLongitude(_lon / valid);
      setAccuracy(_acc / valid);
    }
  }

  /**
   * Holt sich die Daten aus dem Response
   *
   * @return String
   */
  private static double[] response(String json) {
    double[] d = { 1.0d, 0.0d, 0.0d, 0.0d };
    try {
      JSONObject out = new JSONObject(json);
      if (out.has("lat")) {
        d[1] = Double.parseDouble(out.get("lat").toString());
        d[2] = Double.parseDouble(out.get("lon").toString());
        d[3] = Double.parseDouble(out.get("range").toString());
      } else {
        d[0] = 0.0d;
      }
    } catch (JSONException e) {
      // @todo better logging
      e.printStackTrace();
    }
    return d;
  }
}
