package com.naxanria.mappy.client.widget;

import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorWidget extends AbstractButtonWidget
{
  private static Identifier widgetID;
  private static NativeImage widgetImage;// = new NativeImage(NativeImage.Format.RGBA, 128, 128, false);
  private static NativeImageBackedTexture widgetTexture;
  
  private static boolean initialized = false;
  
  private static void init()
  {
    if (initialized)
    {
      return;
    }
    
    widgetImage = new NativeImage(NativeImage.Format.RGBA, 128, 128, false);
    widgetTexture = new NativeImageBackedTexture(widgetImage);
    widgetID = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("color_widget", widgetTexture);
  }
  
  private float h, s, v;
  
  public ColorWidget(int x, int y, String message)
  {
    super(x, y, message);
    
    s = 0.7f;
    
    init();
  }
  
  @Override
  public void renderButton(int int_1, int int_2, float float_1)
  {
//    super.renderButton(int_1, int_2, float_1);
  }
  
  public void update()
  {
    for (int xp = 0; xp < 128; xp++)
    {
      float ph = (xp / 128f) * 360;
      for (int yp = 0; yp < 128; yp++)
      {
        float pv = (yp / 128f);
        int col = Color.HSBtoRGB(ph, s, pv);
        widgetImage.setPixelRGBA(xp, yp, col);
      }
    }
    
    widgetTexture.upload();
  }
  
  @Override
  public void render(int int_1, int int_2, float float_1)
  {
    super.render(int_1, int_2, float_1);
  
    MinecraftClient.getInstance().getTextureManager().bindTexture(widgetID);
  
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBufferBuilder();
    builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
  
    double z = 0.09;
    int iw = width;
    int ih = height;
    
    builder.vertex(x, y + ih, z).texture(0, 1).color(255, 255, 255, 255).next();
    builder.vertex(x + iw, y + ih, z).texture(1, 1).color(255, 255, 255, 255).next();
    builder.vertex(x + iw, y, z).texture(1, 0).color(255, 255, 255, 255).next();
    builder.vertex(x, y, z).texture(0, 0).color(255, 255, 255, 255).next();
    tessellator.draw();
  }
  
  public int getCurrentColor()
  {
    return Color.HSBtoRGB(h, s, v);//ColorUtil.hsvToRgbInt(h, s, v);
  }
  
  public float getH()
  {
    return h;
  }
  
  public ColorWidget setH(float h)
  {
    this.h = h;
    return this;
  }
  
  public float getS()
  {
    return s;
  }
  
  public ColorWidget setS(float s)
  {
    this.s = s;
    return this;
  }
  
  public float getV()
  {
    return v;
  }
  
  public ColorWidget setV(float v)
  {
    this.v = v;
    return this;
  }
  
  public void setHeight(int height)
  {
    this.height = height;
  }
  
  public void setColor(int color)
  {
    int[] rgb = ColorUtil.toInts(color);
    float[] hsv = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);//ColorUtil.rgbToHsv(color);
    h = hsv[0];
    s = hsv[1];
    v = hsv[2];
  }
}
