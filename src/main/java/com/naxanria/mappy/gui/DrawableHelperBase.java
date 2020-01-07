package com.naxanria.mappy.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.AbstractGui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class DrawableHelperBase extends AbstractGui
{
  public static final int WHITE = 0xffffffff;
  public static final int RED = 0xffff0000;
  public static final int GREEN = 0xff00ff00;
  public static final int BLUE = 0xff0000ff;
  public static final int BLACK = 0xff000000;
  
  public static final Minecraft client = Minecraft.getInstance();
  
  public void fillNoDepth(int x, int y, int right, int left, int color)
  {
    RenderSystem.disableDepthTest();
    fill(x, y, right, left, color);
    RenderSystem.enableDepthTest();
  }
  
  public void drawStringCenteredBound(FontRenderer font, String string, int x, int y, int leftBound, int rightBound, int color)
  {
    if (string == null)
    {
      return;
    }
    
    int stringWidth = font.getStringWidth(string);
    int drawX = x - stringWidth / 2;
    if (drawX < leftBound)
    {
      drawX = leftBound;
    }
    else if (drawX + stringWidth > rightBound)
    {
      drawX = rightBound - stringWidth;
    }

    drawString(font, string, drawX, y, color);
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
    BufferBuilder builder = tessellator.getBuffer();
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderSystem.color4f(r, g, b, a);
    builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
    builder.func_225582_a_(x1, y1, 0).endVertex();
    builder.func_225582_a_(x2, y2, 0).endVertex();
    builder.func_225582_a_(x3, y3, 0).endVertex();
    tessellator.draw();
    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }
  
  public static void line(int x1, int y1, int x2, int y2, int color)
  {
    float a = (float)(color >> 24 & 255) / 255.0F;
    float r = (float)(color >> 16 & 255) / 255.0F;
    float g = (float)(color >> 8 & 255) / 255.0F;
    float b = (float)(color & 255) / 255.0F;
  
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderSystem.color4f(r, g, b, a);
    builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
    builder.func_225582_a_(x1, y1, 0).endVertex();
    builder.func_225582_a_(x2, y2, 0).endVertex();
    tessellator.draw();
    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }
  
  public static void rect(int x, int y, int size, int color)
  {
    rect(x, y, size, size, color);
  }
  
  public static void rect(int x, int y, int width, int height, int color)
  {
    float a = (float)(color >> 24 & 255) / 255.0F;
    float r = (float)(color >> 16 & 255) / 255.0F;
    float g = (float)(color >> 8 & 255) / 255.0F;
    float b = (float)(color & 255) / 255.0F;
  
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
   RenderSystem.enableBlend();
   RenderSystem.disableTexture();
   RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   RenderSystem.color4f(r, g, b, a);
    builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
    builder.func_225582_a_(x, y, 0).endVertex();
    builder.func_225582_a_(x + width, y, 0).endVertex();
    
    builder.func_225582_a_(x + width, y, 0).endVertex();
    builder.func_225582_a_(x + width, y + height, 0).endVertex();
    
    builder.func_225582_a_(x + width, y + height, 0).endVertex();
    builder.func_225582_a_(x, y + height, 0).endVertex();
  
    builder.func_225582_a_(x, y, 0).endVertex();
    builder.func_225582_a_(x, y + height, 0).endVertex();
    
    tessellator.draw();
    
    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }
  
}
