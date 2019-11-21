package com.naxanria.mappy.util;

import net.minecraft.util.math.BlockPos;

/*
  @author: Naxanria
*/
public class Position2D
{
  protected int x;
  protected int y;
  
  public Position2D(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  
  public Position2D(BlockPos pos)
  {
    this.x = pos.getX();
    this.y = pos.getZ();
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
}
