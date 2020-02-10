package com.naxanria.mappy.map;

import com.naxanria.mappy.gui.Alignment;
import com.naxanria.mappy.gui.DrawableHelperBase;

import net.minecraft.client.gui.FontRenderer;


public class MapInfoLine extends DrawableHelperBase
{
  protected Alignment alignment = Alignment.Center;
  protected String text;
  protected int color = WHITE;
  protected int x, y;
  
  public MapInfoLine(String text)
  {
    this.text = text;
  }
  
  public MapInfoLine(Alignment alignment, String text)
  {
    this.alignment = alignment;
    this.text = text;
  }
  
  public MapInfoLine(String text, int color)
  {
    this.text = text;
    this.color = color;
  }
  
  public MapInfoLine(Alignment alignment, String text, int color)
  {
    this.alignment = alignment;
    this.text = text;
    this.color = color;
  }
  
  public void draw()
  {
    FontRenderer font = client.fontRenderer;
    
    int width = client.getMainWindow().getScaledWidth();
    
    switch (alignment)
    {
      default:
      case Left:
        drawString(font, text, x, y, color);
        break;
      case Center:
        drawStringCenteredBound(font, text, x, y, 0, width - 2, color);
        break;
      case Right:
        drawRightAlignedString(font, text, x, y, color);
        break;
    }
  }
  
  public MapInfoLine setAlignment(Alignment alignment)
  {
    this.alignment = alignment;
    return this;
  }
  
  public MapInfoLine setText(String text)
  {
    this.text = text;
    return this;
  }
  
  public MapInfoLine setColor(int color)
  {
    this.color = color;
    return this;
  }
}
