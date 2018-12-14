package com.topwave.utils;

/**
 * Created by lenovo on 2017/8/17.
 */
public enum EhcacheKey {
  BUILDING_CACHE("building"),
  ROOM_CACHE("room"),
  TENANT_CACHE("tenant"),
  CONTRACT_CACHE("contract"),
  BILL_CACHE("bill"),
  USER_CACHE("user"),
  FLOOR_CACHE("floor");


  private String prefix;

  EhcacheKey(String prefix) {
    this.prefix = prefix;
  }

  /**
   * 拼接key
   * @param args
   * @return
   */
  public String appendStr(String... args) {
    StringBuilder sb = new StringBuilder(prefix);
    for (String arg : args) {
      sb.append(":").append(arg);
    }
    return sb.toString();
  }
}
