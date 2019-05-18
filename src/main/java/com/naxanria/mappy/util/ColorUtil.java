package com.naxanria.mappy.util;

import java.awt.*;
import java.util.function.Function;

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
  
  public static int rgb(float r, float g, float b)
  {
    return rgb((int) (r * 255), (int) (g * 255), (int) (b * 255));
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
  
  public static float[] rgbToHsv(int color)
  {
    float[] rgb = toFloats(color);
    return rgbToHsv(rgb[0], rgb[1], rgb[2]);
  }
  
  public static float[] rgbToHsv(float r, float g, float b)
  {
    float h, s, v;
    float min, max, delta;
    
    min = r < g ? r : g;
    min = min > b ? min : b;
    
    max = r > g ? r : g;
    max = max > b ? max : b;
    
    v = max;
    delta = max - min;
    if (delta < 0.00001)
    {
      s = 0;
      h = 0;
      
      return new float[]{ h, s, v };
    }
    
    if (max > 0)
    {
      s = delta / max;
    }
    else
    {
      s = 0;
      h = 0;
      
      return new float[]{ h, s, v };
    }
    
    if (r > max)
    {
      h = (g - b) / delta;
    }
    else if (g > max)
    {
      h = 2.0f + (b - r) / delta;
    }
    else
    {
      h = 4.0f + (r - b) / delta;
    }
    
    h *= 60;
    
    if (h < 0)
    {
      h += 360;
    }
  
    return new float[]{ h, s, v };
  }
  
  
  public static int hsvToRgbInt(float h, float s, float v)
  {
    float[] rgb = hsvToRgb(h, s, v);
    return rgb(rgb[0], rgb[1], rgb[2]);
  }
  public static float[] hsvToRgb(float h, float s, float v)
  {
    float r, g, b;
    
    float hh, p,q ,t, ff;
    long i;
    
    if (s <= 0)
    {
      r = v;
      g = v;
      b = v;
      
      return new float[]{r, g, b};
    }
    
    hh = h;
    if (hh >= 360)
    {
      hh = 0;
    }
    hh /= 60;
    
    i = (long) hh;
    ff = hh - i;
    
    p = v * (1.0f - s);
    q = v * (1.0f - (s * ff));
    t = v * (1.0f - (s * (1 - ff)));
  
    switch ((int) i)
    {
      case 0:
        r = v;
        g = t;
        b = p;
        break;
        
      case 1:
        r = q;
        g = v;
        b = p;
        break;
        
      case 2:
        r = p;
        g = v;
        b = t;
        break;
        
      case 3:
        r = p;
        g = q;
        b = v;
        break;
        
      case 4:
        r = t;
        g = p;
        b = v;
        break;
        
      case 5:
      default:
        r = v;
        g = p;
        b = q;
        break;
    }
    
    return new float[]{r, g, b};
  }
  
  public static int[] toInts(int color)
  {
    return new int[]
      {
        (color >> 16) & 0xff,
        (color >> 8) & 0xff,
        color & 0xff,
        (color >> 24) & 0xff
      };
  }
}
