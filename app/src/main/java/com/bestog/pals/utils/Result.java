package com.bestog.pals.utils;

/**
 * Class: Result
 * Wird fuer den Broadcast und der Speicherung des Resultates verwendet.
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class Result {

  private double latitude = 0.0d;
  private double longitude = 0.0d;
  private int accuracy = 0;

  public Result() {}

  public Result(Double lat, Double lon, int acc) {
    latitude = lat;
    longitude = lon;
    accuracy = acc;
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

  @Override
  public String toString() {
    return "Result{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", accuracy=" + accuracy +
        '}';
  }
}
