package com.naxanria.mappy.map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.client.Alignment;
import com.naxanria.mappy.client.DrawPosition;
import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.util.TriValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
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
  
  private Config config;
  
  public MapGUI(Map map, int offset, DrawPosition position)
  {
    config = Config.instance;
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
    MinecraftClient client = MinecraftClient.getInstance();
    
    if (client.player == null)
    {
      return;
    }
    
    if (client.currentScreen != null)
    {
      return;
    }
  
    offset = config.getOffset();
    drawPosition = config.getPosition();
    
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
    
    int iw = backingImage.getWidth();
    int ih = backingImage.getHeight();
  
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
  
    for (MapIcon.Waypoint waypoint :
      map.getWaypoints())
    {
      waypoint.draw(x, y);
    }
    GlStateManager.enableDepthTest();

    // draw info for the map
    manager.draw();
//    int s = 4;
//    fill(x + iw / 2 - s, y + ih / 2 - s, x + iw / 2 + s, y + ih / 2 + s, 0xff00ff00);
//    BlockPos playerPos = client.player.getBlockPos();
//    String details =  playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ();
//    drawCenteredString(client.textRenderer, details, x + iw / 2, y + ih + 2 + border, 0xffffffff);
  
    //todo: check what info to show or not.
    
    
//    Biome biome = map.getBiome();
//
//    if (biome != null)
//    {
//      String p = I18n.translate(biome.getTranslationKey());
//
//      drawStringCenteredBound(client.textRenderer, p, x + iw / 2, y + ih + 8 + 2 + border, 0, client.window.getScaledWidth(), WHITE);
//    }
    
  
  }
  
  private void drawMap(MinecraftClient client, int x, int y, int iw, int ih)
  {
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
  }
}
