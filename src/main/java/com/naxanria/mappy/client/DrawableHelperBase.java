package com.naxanria.mappy.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

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
  
  public static void diamond(int x, int y, int width, int height, int color)
  {
//    fill(x, y, x + width, y + height, color);
    triangle
    (
      x, y + height / 2,
      x + width, y + height / 2,
      x + width / 2, y,
      color
    );
    
    triangle
    (
      x, y + height / 2,
      x + width / 2, y + height,
      x + width, y + height / 2,
      color
    );
  }
  
  public static void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int color)
  {
    float a = (float)(color >> 24 & 255) / 255.0F;
    float r = (float)(color >> 16 & 255) / 255.0F;
    float g = (float)(color >> 8 & 255) / 255.0F;
    float b = (float)(color & 255) / 255.0F;
  
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBufferBuilder();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.color4f(r, g, b, a);
    builder.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION);
    builder.vertex(x1, y1, 0).next();
    builder.vertex(x2, y2, 0).next();
    builder.vertex(x3, y3, 0).next();
    tessellator.draw();
    GlStateManager.enableTexture();
    GlStateManager.disableBlend();
  }
  
  public static void line(int x1, int y1, int x2, int y2, int color)
  {
    float a = (float)(color >> 24 & 255) / 255.0F;
    float r = (float)(color >> 16 & 255) / 255.0F;
    float g = (float)(color >> 8 & 255) / 255.0F;
    float b = (float)(color & 255) / 255.0F;
  
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBufferBuilder();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.color4f(r, g, b, a);
    builder.begin(GL11.GL_LINES, VertexFormats.POSITION);
    builder.vertex(x1, y1, 0).next();
    builder.vertex(x2, y2, 0).next();
    tessellator.draw();
    GlStateManager.enableTexture();
    GlStateManager.disableBlend();
  }
  
}
