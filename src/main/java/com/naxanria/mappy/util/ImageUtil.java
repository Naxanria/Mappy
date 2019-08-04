package com.naxanria.mappy.util;

import net.minecraft.client.renderer.texture.NativeImage;

public class ImageUtil
{
  private ImageUtil()
  { }
  
  public static NativeImage writeIntoImage(NativeImage toWrite, NativeImage destination, int x, int y)
  {
    int drawWidth = toWrite.getWidth();
    int drawHeight = toWrite.getHeight();
    int destinationWidth = destination.getWidth();
    int destinationHeight = destination.getHeight();
    
//    if (x < 0)
//    {
//      drawWidth += x;
//      x = 0;
//    }
//    else
    if (x + drawWidth >= destinationWidth)
    {
      drawWidth = destinationWidth - x;
    }
    
//    if (y < 0)
//    {
//      drawHeight += y;
//      y = 0;
//    }
//    else
    if (y + drawHeight >= destinationHeight)
    {
      drawHeight = destinationHeight - y;
    }
  
    for (int xOffset = 0; xOffset < drawWidth; xOffset++)
    {
      int xp = x + xOffset;
      if (xp < 0)
      {
        continue;
      }
  
      for (int yOffset = 0; yOffset < drawHeight; yOffset++)
      {
        int yp = y + yOffset;
        if (yp < 0)
        {
          continue;
        }
        
        destination.setPixelRGBA(xp, yp, toWrite.getPixelRGBA(xOffset, yOffset));
      }
    }
    
    return destination;
  }
  
}
