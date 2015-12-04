package com.bestog.pals.provider;

/**
 * Class: Location Provider - abstract
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public abstract class LocationProvider {

  public static final String PROVIDER_MOZILLA = "MOZILLA";
  public static final String PROVIDER_OPENCELL = "OPENCELL";
  public static final String PROVIDER_OPENMAP = "OPENMAP";
  public static final String PROVIDER_OPENBMAP = "OPENBMAP";
  public static final int COORD_PRECISION = 5;
  static final String UNKNOWN_CELLID = "-1";
  static final String NULL = "0.0d";
  private double latitude = 0.0d;
  private double longitude = 0.0d;
  private int accuracy = 0;

  LocationProvider() {
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }
}
