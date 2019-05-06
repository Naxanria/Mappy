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
    
//    return col;
    
    return (col & 0xff000000) + (r << 16) + (g << 8) + b;
  }
  
  public static int rgb(int r, int g, int b)
  {
    return 0xff000000 | r << 16 | g << 8 | b;
  }
  
  public static float[] toFloats(int color)
  {
    float[] floats = new float[3];
    floats[0] = ((color >> 16 & 0xff) / 255f);
    floats[1] = ((color >> 8 & 0xff) / 255f);
    floats[2] = ((color & 0xff) / 255f);
    
    return floats;
  }
}
