package com.naxanria.mappy.util;

public class ColorUtil
{
  public static int BGRtoRGB(int col)
  {
    int r = col & 0xff;
    int g = (col >> 8) & 0xff;
    int b = (col >> 16) & 0xff;
    
    return r << 16 | g << 8 << b;
  }
  
}
