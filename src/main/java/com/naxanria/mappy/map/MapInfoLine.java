package com.naxanria.mappy.map;

import com.naxanria.mappy.client.Alignment;
import com.naxanria.mappy.client.DrawableHelperBase;
import net.minecraft.client.font.TextRenderer;


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
    TextRenderer textRenderer = client.textRenderer;
    
    int width = client.window.getScaledWidth();
    
    switch (alignment)
    {
      default:
      case Left:
        drawString(textRenderer, text, x, y, color);
        break;
      case Center:
        drawStringCenteredBound(textRenderer, text, x, y, 0, width - 2, color);
        break;
      case Right:
        drawRightAlignedString(textRenderer, text, x, y, color);
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
