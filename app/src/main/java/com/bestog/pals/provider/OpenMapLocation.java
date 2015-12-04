package com.bestog.pals.provider;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Class: Open Wlan Map
 * Link: https://openwifi.su
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class OpenMapLocation extends LocationProvider {

  private final String _apiUrl;
  private final List<HashMap<String, String>> _wifiSpots;

  public OpenMapLocation(String apiUrl, List<HashMap<String, String>> wifiSpots) {
    _wifiSpots = wifiSpots;
    _apiUrl = apiUrl;
  }

  /**
   * Fuehrt die Anfrage durch
   */
  public void request() {
    String request = "";
    // prepare request
    for (HashMap<String, String> wifi : _wifiSpots) {
      request += wifi.get("key") + "\r\n";
    }
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(_apiUrl).openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded, *.*");
      connection.addRequestProperty("Content-Length", String.valueOf(request.length()));
      BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
      outputStream.write(request.getBytes(), 0, request.length());
      outputStream.flush();
      outputStream.close();
      DataInputStream inputStream = new DataInputStream(connection.getInputStream());
      if (inputStream.available() > 0) {
        Properties properties = new Properties();
        properties.load(inputStream);
        if (!properties.isEmpty()) {
          setLatitude(Double.parseDouble(properties.getProperty("lat", LocationProvider.NULL)));
          setLongitude(Double.parseDouble(properties.getProperty("lon", LocationProvider.NULL)));
        }
      }
    } catch (IOException e) {
      // @todo better logging
      e.printStackTrace();
    }
  }

}
