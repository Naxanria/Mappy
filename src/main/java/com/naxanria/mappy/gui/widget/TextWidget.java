package com.naxanria.mappy.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;

public class TextWidget extends Widget
{
  private final FontRenderer font = Minecraft.getInstance().fontRenderer;
  private String msg;
  private int color = 0xffffffff;
  
  public TextWidget(int x, int y, String msg)
  {
    super(x, y, msg);
    
    this.msg = msg;
    
    width = font.getStringWidth(msg);
    height = font.FONT_HEIGHT;
  }
  
  public TextWidget setColor(int color)
  {
    this.color = color;
    return this;
  }
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    if (this.visible)
    {
      isHovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
      
      drawString(font, msg != null ? msg : "NULL", x, y, color);
    }
  }
}
