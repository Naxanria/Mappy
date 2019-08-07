package com.naxanria.mappy.map.waypoint;

import com.mojang.blaze3d.platform.GlStateManager;
import com.naxanria.mappy.client.DrawableHelperBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public enum IconType
{
  DIAMOND(null, 8),
  SQUARE(null, 8),
  TRIANGLE(null, 8),
  SKULL(new ResourceLocation("mappy", "textures/icons/skull.png"), 8),
  HOUSE(new ResourceLocation("mappy", "textures/icons/house.png"), 8);
  
  ResourceLocation texture;
  int size;
  
  IconType(ResourceLocation texture, int size)
  {
    this.texture = texture;
    this.size = size;
  }
  
  public void draw(int x, int y, int color)
  {
    draw(this, x, y, color);
  }
  
  public static void draw(IconType type, int x, int y, int color)
  {
    int size = type.size;
    int hsize = size / 2;
    
    GlStateManager.pushMatrix();
    switch (type)
    {
      case DIAMOND:
        DrawableHelperBase.diamond(x - hsize, y - hsize, size, size, color);
        break;
        
      case SQUARE:
        DrawableHelperBase.fill(x - hsize, y -hsize, x + hsize, y + hsize, color);
        break;
        
      case TRIANGLE:
        DrawableHelperBase.triangle(x - hsize, y + hsize, x + hsize, y + hsize, x, y - hsize, color);
        break;
        
      default:
        ResourceLocation texture = type.texture;
        if (texture == null)
        {
          draw(IconType.SQUARE, x, y, 0xffff00ff);
          break;
        }
        
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        
        double z = 0;
        x -= hsize;
        y -= hsize;
  
        builder.pos(x, y + size, z).tex(0, 1).color(255, 255, 255, 255).endVertex();
        builder.pos(x + size, y + size, z).tex(1, 1).color(255, 255, 255, 255).endVertex();
        builder.pos(x + size, y, z).tex(1, 0).color(255, 255, 255, 255).endVertex();
        builder.pos(x, y, z).tex(0, 0).color(255, 255, 255, 255).endVertex();
        
        tessellator.draw();
        
        break;
    }
    GlStateManager.popMatrix();
  }
}
