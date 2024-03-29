package com.bestog.pals.utils;

import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class: CellScanner
 * Holt alle Informationen vom Smartphone und bereitet diese auf.
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class CellScanner {

  private final TelephonyManager telephonyManager;

  public CellScanner(TelephonyManager cellManager) {
    this.telephonyManager = cellManager;
  }

  /**
   * CellInfos aus den Informationen vom Smartphone holen und in Liste abspeichern
   *
   * @return ArrayList
   */
  public List<HashMap<String, String>> getTowers() {
    List<HashMap<String, String>> result = new ArrayList<>();
    List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
    for (CellInfo item : cellInfos) {
      HashMap<String, String> cell = new HashMap<>();
      CellInfoGsm cellInfoGsm = (CellInfoGsm) item;
      CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
      CellSignalStrengthGsm cellStrength = cellInfoGsm.getCellSignalStrength();
      cell.put("cid", String.valueOf(parse(cellIdentity.getCid())));
      cell.put("lac", String.valueOf(parse(cellIdentity.getLac())));
      cell.put("mcc", String.valueOf(parse(cellIdentity.getMcc())));
      cell.put("mnc", String.valueOf(parse(cellIdentity.getMnc())));
      cell.put("asu", String.valueOf(parse(cellStrength.getAsuLevel())));
      cell.put("dbm", String.valueOf(parse(cellStrength.getDbm())));
      cell.put("lvl", String.valueOf(parse(cellStrength.getLevel())));
      cell.put("reg", String.valueOf(cellInfoGsm.isRegistered()));
      result.add(cell);
    }
    List<NeighboringCellInfo> cellInfoList = telephonyManager.getNeighboringCellInfo();
    String networkOperator = telephonyManager.getNetworkOperator();
    for (NeighboringCellInfo cellInfo : cellInfoList) {
      HashMap<String, String> cell = new HashMap<>();
      cell.put("lac", String.valueOf(cellInfo.getLac()));
      cell.put("cid", String.valueOf(cellInfo.getCid()));
      cell.put("mcc", networkOperator.substring(3));
      cell.put("mnc", networkOperator.substring(0, 3));
      cell.put("dbm", String.valueOf(-1 * 113 + 2 * cellInfo.getRssi()));
      cell.put("radio", getTypeAsString(cellInfo.getNetworkType()));
      result.add(cell);
    }
    return result;
  }

  /**
   * Parst die Werte richtig
   *
   * @param nr Wert
   * @return int
   */
  private static int parse(int nr) {
    return nr < Integer.MAX_VALUE ? nr : -1;
  }

  /**
   * Typ der CellInfo richtig zuordnen
   *
   * @param type Typ
   * @return String
   */
  private static String getTypeAsString(int type) {
    String out;
    switch (type) {
      case TelephonyManager.NETWORK_TYPE_GPRS:
      case TelephonyManager.NETWORK_TYPE_EDGE:
        out = "gsm";
        break;
      case TelephonyManager.NETWORK_TYPE_UMTS:
      case TelephonyManager.NETWORK_TYPE_HSPA:
      case TelephonyManager.NETWORK_TYPE_HSDPA:
      case TelephonyManager.NETWORK_TYPE_HSPAP:
      case TelephonyManager.NETWORK_TYPE_HSUPA:
        out = "umts";
        break;
      case TelephonyManager.NETWORK_TYPE_LTE:
        out = "lte";
        break;
      case TelephonyManager.NETWORK_TYPE_1xRTT:
      case TelephonyManager.NETWORK_TYPE_CDMA:
      case TelephonyManager.NETWORK_TYPE_EHRPD:
      case TelephonyManager.NETWORK_TYPE_EVDO_0:
      case TelephonyManager.NETWORK_TYPE_EVDO_A:
      case TelephonyManager.NETWORK_TYPE_EVDO_B:
        out = "cmda";
        break;
      default:
        out = "";
    }
    return out;
  }
}
