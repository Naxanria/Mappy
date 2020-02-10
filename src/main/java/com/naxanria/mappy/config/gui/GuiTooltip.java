package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.util.BiValue;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class GuiTooltip extends DrawableHelperBase
{
  public static final FontRenderer FONT = Minecraft.getInstance().fontRenderer;
  
  protected int border = 2;
  protected int borderColor = 0xff676767;
  protected int bgColor = 0xff333333;
  protected int padding = 4;
  protected int spacing = 2;
  
  public int x, y;
  public int width, height;
  
  private List<BiValue<String, Integer>> tooltipInfo = new ArrayList<>();
  
  public GuiTooltip addInfo(String info)
  {
    return addInfo(info, 0xffffffff);
  }
  
  public GuiTooltip addInfo(String info, int color)
  {
    int w = FONT.getStringWidth(info);
    if (w + 2 * padding + 2 * border > width)
    {
      width = w + 2 * padding + 2 * border;
    }
    
    tooltipInfo.add(new BiValue<>(info, color));
    
    height = 2 * padding + 2 * border + (FONT.FONT_HEIGHT + spacing) * tooltipInfo.size() - spacing;
    
    return this;
  }
  
  public GuiTooltip range(int min, int max)
  {
    return addInfo("Range: [" + min + " - " + max + "]", 0xff33aaee);
  }
  
  public GuiTooltip def(boolean val)
  {
    return def(val ? "True" : "False");
  }
  
  public GuiTooltip def(int val)
  {
    return def("" + val);
  }
  
  public GuiTooltip def(String val)
  {
    return addInfo("Default: " + val, 0xff008888);
  }
  
  public GuiTooltip line()
  {
    return addInfo("---", 0xff88dd00);
  }
  
  public void render(int x, int y)
  {
    Minecraft minecraft = Minecraft.getInstance();
    MainWindow mainWindow = minecraft.getMainWindow();

    int windowWidth = mainWindow.getScaledWidth();
    int windowHeight = mainWindow.getScaledHeight();
    
    if (x + width > windowWidth)
    {
      x = windowWidth - width;
    }
    if (x < 0)
    {
      x = 0;
    }
    
    if (y + height > windowHeight)
    {
      y = windowHeight - height;
    }
    
    if (y < 0)
    {
      y = 0;
    }
  
    fill(x, y, x + width, y + height, borderColor);
    fill(x + border, y + border, x + width - border, y + height - border, bgColor);
    
    int yp = y + border + padding;
    int xp = x + border + padding;
    
    for (BiValue<String, Integer> info :
      tooltipInfo)
    {
      drawCenteredString(FONT, info.A, x + width / 2, yp, info.B);
      yp += FONT.FONT_HEIGHT + spacing;
    }
  }
  
  public boolean isEmpty()
  {
    return tooltipInfo.isEmpty();
  }
}
