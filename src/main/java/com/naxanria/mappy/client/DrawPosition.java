package com.naxanria.mappy.client;

import net.minecraft.util.math.MathHelper;

public enum DrawPosition
{
  TOP_LEFT,
  TOP_CENTER,
  TOP_RIGHT,
  BOTTOM_LEFT,
  BOTTOM_RIGHT;
  
  public static DrawPosition get(Integer i)
  {
    i = MathHelper.clamp(i, 0, values().length - 1);
    return values()[i];
  }
}
