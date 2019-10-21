package com.naxanria.mappy.map;

import com.naxanria.mappy.Logger;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.event.KeyHandler;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.chunk.ChunkData;
import com.naxanria.mappy.util.ColorUtil;
import com.naxanria.mappy.util.ImageUtil;
import com.naxanria.mappy.util.RandomUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.lwjgl.glfw.GLFW;

public class WorldMapGUI extends ScreenBase
{
  private BlockPos.MutableBlockPos center;
  private ClientPlayerEntity player;
  private ChunkCache preLoader;
  private NativeImage backingImage;
  private DynamicTexture texture;
  private ResourceLocation textureIdentifier;
  private KeyHandler keyHandler;
  
  public WorldMapGUI(Screen parent)
  {
    super(new StringTextComponent("World Map"), parent);
    
    player = (ClientPlayerEntity) getLocalPlayer();
    
    center = new BlockPos.MutableBlockPos(player.getPosition());
    
    preLoader = ChunkCache.getPreLoader(player.world);
    
    keyHandler = KeyHandler.INSTANCE;
    keyHandler.watch(GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_A);
    keyHandler.watch(GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_D);
    keyHandler.watch(GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_W);
    keyHandler.watch(GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_S);
  }
  
  @Override
  public void renderBackground()
  {
    fill(0, 0, windowWidth, windowHeight, 0xff000000);
    
    // setup
    if (backingImage == null || backingImage.getHeight() != windowHeight || backingImage.getWidth() != windowWidth)
    {
      backingImage = new NativeImage(windowWidth, windowHeight, false);
      if (texture != null)
      {
        texture.deleteGlTexture();
        texture = null;
      }
    }
   
    if (texture == null)
    {
      texture = new DynamicTexture(backingImage);
      update();
      
      textureIdentifier = minecraft.getTextureManager().getDynamicTextureLocation(Mappy.MODID + "world_map_texture", texture);
    }
    
    // render map
    renderTexture(0, 0, windowWidth, windowHeight, textureIdentifier);
    
    // debug: render chunk coords
    if (Screen.hasControlDown())
    {
      renderChunkCoords();
    }
    
    // render icons
    
    
  }
  
  private void renderChunkCoords()
  {
    int centerChunkX = center.getX() / 16;
    int centerChunkZ = center.getZ() / 16;
    int xRadius = windowWidth / 16 / 2 + 1;
    int zRadius = windowHeight / 16 / 2+ 1;
    int chunkXStart = centerChunkX - xRadius;
    int chunkZStart = centerChunkZ - zRadius;
    int chunkXEnd = centerChunkX + xRadius;
    int chunkZEnd = centerChunkZ + zRadius;
  
    int xp = 0;
    int yp = 0;
    for (int cx = chunkXStart; cx <= chunkXEnd; cx++)
    {
      yp = 0;
      for (int cz = chunkZStart; cz < chunkZEnd; cz++)
      {
        drawCenteredString(font, cx + "," + cz, xp + 8, yp + 8, 0xffffffff);
        
        yp += 16;
      }
      xp += 16;
    }
    
  }
  
  @Override
  public void tick()
  {
    boolean update = false;
    if (keyHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT) || keyHandler.isKeyPressed(GLFW.GLFW_KEY_A))
    {
      center.setPos(center.getX() - 16, 0, center.getZ());
      update = true;
    }
    if (keyHandler.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || keyHandler.isKeyPressed(GLFW.GLFW_KEY_D))
    {
      center.setPos(center.getX() + 16, 0, center.getZ());
      update = true;
    }
    if (keyHandler.isKeyPressed(GLFW.GLFW_KEY_UP) || keyHandler.isKeyPressed(GLFW.GLFW_KEY_W))
    {
      center.setPos(center.getX(), 0, center.getZ() - 16);
      update = true;
    }
    if (keyHandler.isKeyPressed(GLFW.GLFW_KEY_DOWN) || keyHandler.isKeyPressed(GLFW.GLFW_KEY_S))
    {
      center.setPos(center.getX(), 0, center.getZ() + 16);
      update = true;
    }
    
    if (update)
    {
      update();
    }
  }
  
  private void update()
  {
    backingImage.fillAreaRGBA(0, 0, windowWidth, windowHeight, 0xff000000);
  
    preLoader = ChunkCache.getPreLoader(player.world);
    
    Mappy.LOGGER.info("Update");
    
    int centerChunkX = center.getX() / 16;
    int centerChunkZ = center.getZ() / 16;
    int xRadius = windowWidth / 16 / 2 + 1;
    int zRadius = windowHeight / 16 / 2+ 1;
    int chunkXStart = centerChunkX - xRadius;
    int chunkZStart = centerChunkZ - zRadius;
    int chunkXEnd = centerChunkX + xRadius;
    int chunkZEnd = centerChunkZ + zRadius;
  
    int xp = 0;
    int yp = 0;
  
    Mappy.LOGGER.info("StartX=" + chunkXStart + " StartZ=" + chunkZStart + " xRadius=" + xRadius + "zRadius=" + zRadius );
    
//    preLoader.update(backingImage ,xRadius > zRadius ? xRadius : zRadius, center.getX(), center.getZ());
    
    for (int cx = chunkXStart; cx <= chunkXEnd; cx++)
    {
      yp = 0;
      for (int cz = chunkZStart; cz < chunkZEnd; cz++)
      {
        ChunkData data = preLoader.getChunk(cx, cz, true);
        
        if (!checkChunkCoords(data, cx, cz))
        {
//          backingImage.fillAreaRGBA(xp, yp, xp + 16, yp + 16, 0xff000000);
        }
        else
        {
          ImageUtil.writeIntoImage(data.image, backingImage, xp, yp);
        }
//        data.image.fillAreaRGBA(0, 0, 1, 16, 0xffffffff);
//        data.image.fillAreaRGBA(0, 0, 16, 1, 0xffffffff);

        Mappy.LOGGER.info("cx=" + cx + ",cz=" + cz + ",xp=" + xp + ",yp=" + yp + ",chunk pos=" + data.chunk.getPos() + ",check=" + checkChunkCoords(data, cx, cz));

        yp += 16;
      }

      xp += 16;
    }
  
    texture.updateDynamicTexture();
  }
  
  private boolean checkChunkCoords(ChunkData data, int cx, int cz)
  {
    ChunkPos pos = data.chunk.getPos();
    return data.cx == cx && data.cz == cz
      && pos.x == data.cx && pos.z == data.cz;
  }
}
