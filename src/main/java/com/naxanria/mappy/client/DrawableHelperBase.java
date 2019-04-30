package com.naxanria.mappy.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

public class DrawableHelperBase extends DrawableHelper
{
  public static final int WHITE = 0xffffffff;
  public static final int RED = 0xffff0000;
  public static final int GREEN = 0xff00ff00;
  public static final int BLUE = 0xff0000ff;
  public static final int BLACK = 0xff000000;
  
  public static final MinecraftClient client = MinecraftClient.getInstance();
  
  public void fillNoDepth(int x, int y, int right, int left, int color)
  {
    GlStateManager.disableDepthTest();
    fill(x, y, right, left, color);
    GlStateManager.enableDepthTest();
  }
  
  public void drawStringCenteredBound(TextRenderer textRenderer, String string, int x, int y, int leftBound, int rightBound, int color)
  {
    if (string == null)
    {
      return;
    }
    
    int stringWidth = textRenderer.getStringWidth(string);
    int drawX = x - stringWidth / 2;
    if (drawX < leftBound)
    {
      drawX = leftBound;
    }
    else if (drawX + stringWidth > rightBound)
    {
      drawX = rightBound - stringWidth;
    }

    drawString(textRenderer, string, drawX, y, color);
  }
}
