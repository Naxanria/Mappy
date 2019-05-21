package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.config.Settings;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import com.naxanria.mappy.util.StateUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

public class ChunkData
{
  public int cx, cz;
//  public int[] heightmap;
  public NativeImage image = new NativeImage(NativeImage.Format.RGBA, 16, 16, false);
  public long time;
  public MapLayer layer;
  WorldChunk chunk;
  private boolean nether;
  private boolean updating;
  
  private ChunkData()
  { }
  
  public ChunkData(WorldChunk chunk, MapLayer layer)
  {
    ChunkPos pos = chunk.getPos();
    cx = pos.x;
    cz = pos.z;
    time = System.currentTimeMillis();
    this.layer = layer;
    this.chunk = chunk;
    nether = (chunk.getWorld().getDimension().getType() == DimensionType.THE_NETHER);
  }
  
  void setChunk(WorldChunk chunk)
  {
    this.chunk = chunk;
  }
  
  public void update()
  {
    long now = System.currentTimeMillis();
    updating = true;
    
    for (int x = 0; x < 16; x++)
    {
      for (int z = 0; z < 16; z++)
      {
        int col = MapLayerProcessor.BLACK;
        switch (layer)
        {
          case TOP_VIEW:
            if (nether)
            {
              col = MapLayerProcessor.processTopViewNether(this, x, (int) MinecraftClient.getInstance().player.y -1 , z);
            }
            else
            {
              col = MapLayerProcessor.processTopView(this, x, z);
            }
            break;
          case CAVES:
            break;
        }
        
        if (!updating)
        {
          time = now;
          return;
        }
        
        image.setPixelRGBA(x, z, col);
        
        if (Settings.shaded)
        {
          image.blendPixel(x, z, MapLayerProcessor.shadeTopView(this, x, z));
        }
      }
    }
    
    time = now;
  }
  
  public static ChunkData fromTag(CompoundTag tag, World world)
  {
    
    
    
    return null;
  }
  
  public WorldChunk getChunk()
  {
    return chunk;
  }
  
  public int cancelUpdate()
  {
    updating = false;
    
    return -1;
  }
}
