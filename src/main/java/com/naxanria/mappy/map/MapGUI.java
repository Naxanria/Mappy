package com.naxanria.mappy.map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.config.MappyConfig;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class MapGUI extends DrawableHelperBase
{
  public static MapGUI instance;
  
  protected DrawPosition drawPosition;
  protected int border = 2;
  protected int borderColor = 0xff888888;
  
  private int offset;

  private final Map map;
  
  private NativeImage backingImage;
  private DynamicTexture texture;
  private ResourceLocation textureIdentifier;
  
  private MapInfoLineManager manager;
  
  public MapGUI(Map map, int offset, DrawPosition position)
  {
    this.map = map;
    instance = this;
    this.offset = offset;
    drawPosition = position;
    
    manager = map.getManager();
  }
  
  public void markDirty()
  {
    NativeImage img = map.getImage();
    if (img != backingImage)
    {
      backingImage = img;
      if (texture != null)
      {
        texture.close();
      }
      texture = null;
    }
    
    if (texture != null)
    {
      texture.updateDynamicTexture();
    }
  }
  
  public void draw()
  {
    if (!Mappy.showMap)
    {
      return;
    }
    
    boolean canShowMap = map.canShowMap();
    
    Minecraft client = Minecraft.getInstance();
    
    if (client.player == null)
    {
      return;
    }
    
    if (client.currentScreen != null)
    {
      if (!(MappyConfig.showInChat && client.currentScreen instanceof ChatScreen))
      {
        return;
      }
    }
  
    offset = MappyConfig.offset;
    drawPosition = MappyConfig.drawPosition;
    
    if (texture == null)
    {
      backingImage = map.getImage();
      texture = new DynamicTexture(backingImage);
      texture.updateDynamicTexture();
      textureIdentifier = client.getTextureManager().getDynamicTextureLocation(Mappy.MODID + "_map_texture", texture);
    }
    
    int x = offset;
    int y = offset;
    MainWindow mainWindow = client.getMainWindow();
    int w = mainWindow.getScaledWidth();
    int h = mainWindow.getScaledHeight();
  
    int scale = MappyConfig.scale;
    int iw = backingImage.getWidth() / scale;
    int ih = backingImage.getHeight() / scale;
    
  
    MapInfoLineManager.Direction direction = MapInfoLineManager.Direction.DOWN;
    
    switch (drawPosition)
    {
      case TOP_LEFT:
        break;
      case TOP_CENTER:
        x = w / 2 - iw / 2;
        break;
      case TOP_RIGHT:
        x = w - offset - iw;
        if (MappyConfig.moveMapForEffects)
        {
          /*
           * Based on code by ThexXTURBOXx in pull request #5
           * */
          EffectState effects = map.getEffects();
          if (effects != EffectState.NONE)
          {
            y += effects == EffectState.HARMFUL ? 48 : 24;
          }
        }
        break;
      case BOTTOM_LEFT:
        direction = MapInfoLineManager.Direction.UP;
        y = h - offset - ih;
        break;
      case BOTTOM_RIGHT:
        direction = MapInfoLineManager.Direction.UP;
        x = w - offset - iw;
        y = h - offset - ih;
        break;
    }
    
    manager.setPosition(x, y + (direction == MapInfoLineManager.Direction.DOWN && canShowMap ? ih + border + 2 : -border - 2));
    manager.setDirection(direction);
    manager.setSpacing(12);
    
    if (canShowMap)
    {
      fill(x - border, y - border, x + iw + border, y + ih + border, borderColor);
  
      // draw the map
      drawMap(client, x, y, iw, ih);
  
      // draw the icons
      RenderSystem.disableDepthTest();
      for (MapIcon.Player player :
        map.getPlayerIcons())
      {
        player.draw(x, y);
      }
  
      for (MapIcon.Entity entity :
        map.getEntities())
      {
        entity.draw(x, y);
      }
  
      for (MapIcon.Waypoint waypoint :
        map.getWaypoints())
      {
        waypoint.draw(x, y);
      }
  
  
      RenderSystem.enableDepthTest();
    }
    // draw info for the map
    manager.draw();
  }
  
  private void drawMap(Minecraft client, int x, int y, int iw, int ih)
  {
    
//    fillNoDepth(x, y, x + iw, y + ih, 0xffff00ff);
    
//    client.getTextureManager().bindTexture(textureIdentifier);
    
    DrawableHelperBase.renderTexture(x, y, iw, ih, textureIdentifier);
    
//    client.getTextureManager().bindTexture(textureIdentifier);
//
//
//
//    Tessellator tessellator = Tessellator.getInstance();
//    BufferBuilder builder = tessellator.getBuffer();
//    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//
//    double z = 0.09;
//
//    builder.func_225582_a_(x, y + ih, z).func_225583_a_(0, 1).func_227885_a_(255, 255, 255, 255).endVertex();
//    builder.func_225582_a_(x + iw, y + ih, z).func_225583_a_(1, 1).func_227885_a_(255, 255, 255, 255).endVertex();
//    builder.func_225582_a_(x + iw, y, z).func_225583_a_(1, 0).func_227885_a_(255, 255, 255, 255).endVertex();
//    builder.func_225582_a_(x, y, z).func_225583_a_(0, 0).func_227885_a_(255, 255, 255, 255).endVertex();
//    tessellator.draw();
    if (MappyConfig.drawChunkGrid)
    {
      drawGrid(client, x, y, iw, ih);
    }
  }
  
  private void drawGrid(Minecraft client, int x, int y, int iw, int ih)
  {
    int col = 0x88444444;
    int px = client.player.getPosition().getX();
    int pz = client.player.getPosition().getZ();
    int xOff = ((px / 16) * 16) - px;
    int yOff = ((pz / 16) * 16) - pz;
    
    RenderSystem.disableDepthTest();
    for (int h = yOff; h < ih; h += 16)
    {
      int yp = y + h;
      if (yp < y || yp > y + ih)
      {
        continue;
      }
      line(x, yp, x + iw, yp, col);
    }
  
    for (int v = xOff; v < iw; v += 16)
    {
      int xp = x + v;
      if (xp < x || xp >= x + iw)
      {
        continue;
      }
      
      line(xp, y, xp, y + ih, col);
    }
    RenderSystem.enableDepthTest();
  }
}
