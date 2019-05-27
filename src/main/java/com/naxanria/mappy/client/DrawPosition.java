package com.naxanria.mappy.client;

public enum DrawPosition
{
  TOP_LEFT,
  TOP_CENTER,
  TOP_RIGHT,
  BOTTOM_LEFT,
  BOTTOM_RIGHT;
  
  public static DrawPosition get(Integer i)
  {
    return values()[i];
  }
}
