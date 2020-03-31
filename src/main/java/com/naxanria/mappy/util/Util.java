package com.naxanria.mappy.util;

import net.minecraft.util.math.BlockPos;

public class Util
{
  public static String prettyFy(BlockPos pos)
  {
    return "[" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]";
  }
  
  public static boolean isInside(BlockPos pos, int xStart, int zStart, int xEnd, int zEnd)
  {
    return isInside(pos.getX(), pos.getZ(), xStart, zStart, xEnd, zEnd);
  }
  
  public static boolean isInside(int posX, int posY, int xStart, int yStart, int xEnd, int yEnd)
  {
    return posX >= xStart && posX < xEnd
      && posY >= yStart && posY < yEnd;
  }
}
