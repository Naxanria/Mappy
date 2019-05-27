package com.naxanria.mappy.map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.client.DrawPosition;
import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.config.Settings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;


@Environment(EnvType.CLIENT)
public class MapGUI extends DrawableHelperBase
{
  public static MapGUI instance;
  
  protected DrawPosition drawPosition;
  protected int border = 2;
  protected int borderColor = 0xff888888;
  
  private int offset;

  private final Map map;
  
  private NativeImage backingImage;
  private NativeImageBackedTexture texture;
  private Identifier textureIdentifier;
  
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
      texture.upload();
    }
  }
  
  public void draw()
  {
    if (!Mappy.showMap)
    {
      return;
    }
    
    MinecraftClient client = MinecraftClient.getInstance();
    
    if (client.player == null)
    {
      return;
    }
    
    if (client.currentScreen != null)
    {
      return;
    }
  
    offset = Settings.offset;
    drawPosition = Settings.drawPosition;
    
    if (texture == null)
    {
      backingImage = map.getImage();
      texture = new NativeImageBackedTexture(backingImage);
      texture.upload();
      textureIdentifier = client.getTextureManager().registerDynamicTexture(Mappy.MODID + "_map_texture", texture);
    }
    
    int x = offset;
    int y = offset;
    int w = client.window.getScaledWidth();
    int h = client.window.getScaledHeight();
  
    int scale = Settings.scale;
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
        if (Settings.moveMapForEffects)
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
    
    manager.setPosition(x, y + (direction == MapInfoLineManager.Direction.DOWN ? ih + border + 2 : -border - 2));
    manager.setDirection(direction);
    manager.setSpacing(12);
  
    fill(x - border, y - border, x + iw + border, y + ih + border, borderColor);
    
    // draw the map
    drawMap(client, x, y, iw, ih);
    
    // draw the icons
    GlStateManager.disableDepthTest();
    for (MapIcon.Player player:
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
    
    
    GlStateManager.enableDepthTest();

    // draw info for the map
    manager.draw();
  }
  
  private void drawMap(MinecraftClient client, int x, int y, int iw, int ih)
  {
    
//    fillNoDepth(x, y, x + iw, y + ih, 0xffff00ff);
    
    client.getTextureManager().bindTexture(textureIdentifier);
    
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBufferBuilder();
    builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
    
    double z = 0.09;
    
    builder.vertex(x, y + ih, z).texture(0, 1).color(255, 255, 255, 255).next();
    builder.vertex(x + iw, y + ih, z).texture(1, 1).color(255, 255, 255, 255).next();
    builder.vertex(x + iw, y, z).texture(1, 0).color(255, 255, 255, 255).next();
    builder.vertex(x, y, z).texture(0, 0).color(255, 255, 255, 255).next();
    tessellator.draw();
    if (Settings.drawChunkGrid)
    {
      drawGrid(client, x, y, iw, ih);
    }
  }
  
  private void drawGrid(MinecraftClient client, int x, int y, int iw, int ih)
  {
    int col = 0x88444444;
    int px = client.player.getBlockPos().getX();
    int pz = client.player.getBlockPos().getZ();
    int xOff = ((px / 16) * 16) - px;
    int yOff = ((pz / 16) * 16) - pz;
    
    GlStateManager.disableDepthTest();
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
    GlStateManager.enableDepthTest();
  }
}
