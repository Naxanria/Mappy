package com.naxanria.mappy.map.waypoint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.gui.DrawableHelperBase;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IconType
{
  private static Map<String, IconType> icons = new HashMap<>();
  private static List<String> iconNames = new ArrayList<>();
  
  public static IconType getIcon(String name)
  {
    return icons.getOrDefault(name, DIAMOND);
  }
  
  public static boolean exists(String name)
  {
    return icons.containsKey(name);
  }
  
  public static List<String> getIconNames()
  {
    return iconNames;
  }
  
  public final String name;
  protected int size;
  
//  DIAMOND(null, 8),
//  SQUARE(null, 8),
//  TRIANGLE(null, 8),
//  SKULL(new ResourceLocation("mappy", "textures/icons/skull.png"), 8),
//  HOUSE(new ResourceLocation("mappy", "textures/icons/house.png"), 8);
  
  IconType(String name, int size)
  {
    this.name = name;
    this.size = size;
    
    icons.put(name, this);
    iconNames.add(name);
  }
  
  public abstract void draw(int x, int y, int color);
 
  public static final IconType DIAMOND = new IconType("diamond", 8)
  {
    @Override
    public void draw(int x, int y, int color)
    {
      DrawableHelperBase.diamond(x, y, size, size, color);
    }
  };
  
  public static final IconType SQUARE = new IconType("square", 8)
  {
    @Override
    public void draw(int x, int y, int color)
    {
      DrawableHelperBase.fill(x, y, x + size, y + size, color);
    }
  };
  
  public static final IconType TRIANGLE = new IconType("triangle", 8)
  {
    @Override
    public void draw(int x, int y, int color)
    {
      DrawableHelperBase.triangle(x, y + size, x + size, y + size, x + size / 2, y, color);
    }
  };
  
  public static class TextureIconType extends IconType
  {
    protected final ResourceLocation location;
    
    TextureIconType(String name, int size, ResourceLocation location)
    {
      super(name, size);
      
      this.location = location;
    }
    
    @Override
    public void draw(int x, int y, int color)
    {
      RenderSystem.enableAlphaTest();
      DrawableHelperBase.renderTexture(x, y, size, size, location);
      RenderSystem.disableAlphaTest();
    }
  }
  
  public static final TextureIconType SKULL = new TextureIconType("skull", 8, new ResourceLocation(Mappy.MODID, "textures/icons/skull.png"));
  public static final TextureIconType HOUSE = new TextureIconType("house", 8, new ResourceLocation(Mappy.MODID, "textures/icons/house.png"));
  
  
  
//
//  public static void draw(IconType type, int x, int y, int color)
//  {
//    int size = type.size;
//    int hsize = size / 2;
//
//    RenderSystem.pushMatrix();
//    switch (type)
//    {
//      case DIAMOND:
//        DrawableHelperBase.diamond(x, y, size, size, color);
//        break;
//
//      case SQUARE:
//        DrawableHelperBase.fill(x - hsize, y -hsize, x + hsize, y + hsize, color);
//        break;
//
//      case TRIANGLE:
//        DrawableHelperBase.triangle(x - hsize, y + hsize, x + hsize, y + hsize, x, y - hsize, color);
//        break;
//
//      default:
//        ResourceLocation texture = type.texture;
//        if (texture == null)
//        {
//          draw(IconType.SQUARE, x - hsize, y - hsize, 0xffff00ff);
//          break;
//        }
//
//        RenderSystem.enableAlphaTest();
//        DrawableHelperBase.renderTexture(x - hsize, y - hsize, size, size, texture);
//        RenderSystem.disableAlphaTest();
//
//        break;
//    }
//    RenderSystem.popMatrix();
//  }
}
