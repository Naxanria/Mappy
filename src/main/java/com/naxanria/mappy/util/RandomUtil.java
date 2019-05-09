package com.naxanria.mappy.util;

import java.util.Random;

public class RandomUtil
{
  public static final Random rand = new Random();
  
  public static int getRange(int max)
  {
    return getRange(0, max);
  }
  
  public static int getRange(int min, int max)
  {
    return rand.nextInt(max - min) + min;
  }
  
  
}
