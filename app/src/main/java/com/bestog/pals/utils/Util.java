package com.bestog.pals.utils;

import com.bestog.pals.provider.LocationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Class: Util
 * Wichtige Funktionen fuer die Applikation
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public final class Util {
  /**
   * Alle Koordinaten werden zusammengerechnet und daraus entsteht das Endergebnis (rudimentaer)
   * http://www.geomidpoint.com/calculation.html > Point A
   *
   * @param positions locations
   * @return Result
   */
  public static Result bestLocation(List<Result> positions) {
    Result result = new Result();
    Double x = 0.0d, y = 0.0d, z = 0.0d;
    int acc = 0;
    int validPositions = 0;
    for (Result item : positions) {
      if (item.getLatitude() != 0.0d && item.getLongitude() != 0.0d) {
        Double latitude = item.getLatitude() * Math.PI / 180;
        Double longitude = item.getLongitude() * Math.PI / 180;
        x += Math.cos(latitude) * Math.cos(longitude);
        y += Math.cos(latitude) * Math.sin(longitude);
        z += Math.sin(latitude);
        acc += item.getAccuracy();
        ++validPositions;
      }
    }
    if (validPositions > 0) {
      x /= validPositions;
      y /= validPositions;
      z /= validPositions;
      Double lon = Math.atan2(y, x);
      Double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
      result.setLatitude(lat * 180 / Math.PI);
      result.setLongitude(lon * 180 / Math.PI);
      result.setAccuracy(acc / validPositions);
    }
    return result;
  }

  /**
   * Speicher die Informationen in das Result-Objekt
   *
   * @param provider Welcher Provider?
   * @return Result
   */
  public static Result collectResult(LocationProvider provider) {
    return new Result(provider.getLatitude(), provider.getLongitude(), provider.getAccuracy());
  }

  /**
   * Runde double-Wert mit geringen Fehler
   *
   * @param value Wert
   * @param places Nachkommastellen
   * @return double
   */
  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException("Nachkommastelle muss groesser 0 sein");
    }
    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  /**
   * Konvertiere InputStream zu String
   *
   * @param stream inputStream
   * @return string
   */
  public static String streamToString(InputStream stream) {
    if (stream == null) {
      return "";
    }
    StringWriter writer = null;
    try {
      InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
      writer = new StringWriter();
      int n;
      char[] buffer = new char[1024 * 4];
      while (-1 != (n = reader.read(buffer))) {
        writer.write(buffer, 0, n);
      }
    } catch (IOException e) {
      // @todo better logging
      e.printStackTrace();
    }
    return writer != null ? writer.toString() : "";
  }
}
