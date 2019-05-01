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
  
  public static int multiply(int col, float amount)
  {
    int r = (int) MathUtil.clamp(((col >> 16) & 0xff) * amount, 0, 255);
    int g = (int) MathUtil.clamp(((col >> 8) & 0xff) * amount, 0, 255);
    int b = (int) MathUtil.clamp((col & 0xff) * amount, 0, 255);
    
    return col & 0xff000000 | r | g | b;
  }
}
