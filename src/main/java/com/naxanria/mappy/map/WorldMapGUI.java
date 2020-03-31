package com.naxanria.mappy.map;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.event.KeyHandler;
import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.chunk.ChunkData;
import com.naxanria.mappy.map.waypoint.WayPoint;
import com.naxanria.mappy.map.waypoint.WayPointManager;
import com.naxanria.mappy.util.ImageUtil;
import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.Util;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldMapGUI extends ScreenBase
{
  private BlockPos.Mutable center;
  private ClientPlayerEntity player;
  private ChunkCache preLoader;
  private NativeImage backingImage;
  private DynamicTexture texture;
  private ResourceLocation textureIdentifier;
  private KeyHandler keyHandler;
  private int prevMouseX = -1;
  private int prevMouseY = -1;
  
  private List<PlayerEntity> players = new ArrayList<>();
  private List<WayPoint> wayPoints = new ArrayList<>();
  private int chunkXStart;
  private int chunkZStart;
  private int chunkXEnd;
  private int chunkZEnd;
  
  private int mouseWorldPosX;
  private int mouseWorldPosZ;
  
  public WorldMapGUI(Screen parent)
  {
    super(new StringTextComponent("World Map"), parent);
    
    player = (ClientPlayerEntity) getLocalPlayer();
    
    center = new BlockPos.Mutable(player.getPosition());
    
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
    DrawableHelperBase.renderTexture(0, 0, windowWidth, windowHeight, textureIdentifier);
    
    
    // render icons
    int xStart = chunkXStart << 4;
    int zStart = chunkZStart << 4;
    int xEnd = chunkXEnd << 4;
    int zEnd = chunkZEnd << 4;
    
    for (WayPoint wayPoint : wayPoints)
    {
      wayPoint.iconType.draw(getScaledX(wayPoint.pos.getX()), getScaledY(wayPoint.pos.getZ()), wayPoint.color);
    }
    
    for (PlayerEntity playerEntity : players)
    {
      PlayerHeadIcon.drawHead(playerEntity, getScaledX(playerEntity.getPosition().getX()), getScaledY(playerEntity.getPosition().getZ()));
    }
    
    if (Util.isInside(player.getPosition(), xStart, zStart, xEnd, zEnd))
    {
      PlayerHeadIcon.drawHead(player, getScaledX(player.getPosition().getX()), getScaledY(player.getPosition().getZ()));
    }
    
    drawString(font, "X: " + mouseWorldPosX + ", Z:" + mouseWorldPosZ, 0 + 5, windowHeight - 20, 0xffffffff);
    
    // render WIP
    drawRightAlignedString(font, "WIP", windowWidth - 5,windowHeight - 20, 0xffffffff);
  }
  
  private int getScaledX(int x)
  {
    return MathUtil.clamp(MapIcon.getScaled(x, chunkXStart << 4, chunkXEnd << 4, windowWidth), 0, windowWidth);
  }
  
  private int getScaledY(int y)
  {
    return MathUtil.clamp(MapIcon.getScaled(y, chunkZStart << 4, chunkZEnd << 4, windowHeight), 0, windowHeight);
  }
  
  @Override
  public boolean isPauseScreen()
  {
    return false;
  }
  

  
  @Override
  public void tick()
  {
    boolean update = false;
    if(prevMouseX == -1 || prevMouseY == -1)
    {
      prevMouseX = mouseX;
      prevMouseY = mouseY;
    }
    else if(prevMouseX != mouseX || prevMouseY != mouseY)
    {
      if(GLFW.glfwGetMouseButton(getMinecraft().getMainWindow().getHandle(), 0) == 1)
      {
        center.setPos(center.getX() + (prevMouseX - mouseX), 0, center.getZ() + (prevMouseY - mouseY));
        update = true;
      }
      prevMouseX = mouseX;
      prevMouseY = mouseY;
    }
    
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
    
    updatePlayers();
    
    updateMouseWorldPos();
  }
  
  private void updateMouseWorldPos()
  {
    int xRange = (chunkXEnd - chunkXStart) << 4;
    int zRange = (chunkZEnd - chunkZStart) << 4;
    
    float xScale = windowWidth / (float) xRange;
    float zScale = windowHeight / (float) zRange;
  
    mouseWorldPosX = (int) (mouseX * xScale + (chunkXStart << 4));
    mouseWorldPosZ = (int) (mouseY * zScale + (chunkZStart << 4));
  }
  
  private void update()
  {
    backingImage.fillAreaRGBA(0, 0, windowWidth, windowHeight, 0xff000000);
  
    preLoader = ChunkCache.getPreLoader(player.world);
    
    int centerChunkX = center.getX() / 16;
    int centerChunkZ = center.getZ() / 16;
    int xRadius = windowWidth / 16 / 2 + 1;
    int zRadius = windowHeight / 16 / 2+ 1;
    chunkXStart = centerChunkX - xRadius;
    chunkZStart = centerChunkZ - zRadius;
    chunkXEnd = centerChunkX + xRadius;
    chunkZEnd = centerChunkZ + zRadius;
  
    int xp = 0;
    int yp = 0;
    
    for (int cx = chunkXStart; cx <= chunkXEnd; cx++)
    {
      yp = 0;
      for (int cz = chunkZStart; cz < chunkZEnd; cz++)
      {
        ChunkData data = preLoader.getChunk(cx, cz, false);
        
        if (!checkChunkCoords(data, cx, cz))
        {
//          backingImage.fillAreaRGBA(xp, yp, xp + 16, yp + 16, 0xff000000);
        }
        else
        {
          ImageUtil.writeIntoImage(data.image, backingImage, xp, yp);
        }

        yp += 16;
      }

      xp += 16;
    }
  
    updateWaypoints(chunkXStart << 4, chunkZStart << 4, chunkXEnd << 4, chunkZEnd << 4);
    
    texture.updateDynamicTexture();
  }
  
  private void updateWaypoints(int xStart, int zStart, int xEnd, int zEnd)
  {
    wayPoints = WayPointManager.INSTANCE.getWaypointsToRender(player.world.dimension.getType().getId())
      .stream().filter(wp -> Util.isInside(wp.pos.getX(), wp.pos.getZ(), xStart, zStart, xEnd, zEnd)).collect(Collectors.toList());
  }
  
  private void updatePlayers()
  {
    players = player.world.getPlayers().stream()
      .filter(p -> Util.isInside(p.getPosition(), chunkXStart << 4, chunkZStart << 4, chunkXEnd << 4, chunkZEnd << 4))
      .collect(Collectors.toList());
  }
  
  private boolean checkChunkCoords(ChunkData data, int cx, int cz)
  {
    if (data == null)
    {
      return false;
    }
    
    if (data.chunk == null)
    {
//      data.chunk = minecraft.player.world.getChunk(cx, cz);
//      if (data.chunk == null)
//      {
//        return false;
//      }
      
      return true;
    }
    
    ChunkPos pos = data.chunk.getPos();
    return data.cx == cx && data.cz == cz
      && pos.x == data.cx && pos.z == data.cz;
  }
}
