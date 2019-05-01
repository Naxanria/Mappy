package com.naxanria.mappy.util;

public class MathUtil
{
  public static int clamp(int val, int min, int max)
  {
    return val < min ? min : val > max ? max : val;
  }
  
  public static float clamp(float val, float min, float max)
  {
    return val < min ? min : val > max ? max : val;
  }
}
