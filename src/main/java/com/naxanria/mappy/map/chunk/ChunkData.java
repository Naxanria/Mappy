package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.config.Settings;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import com.naxanria.mappy.util.ColorUtil;
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

import java.util.Arrays;

public class ChunkData
{
  public int cx, cz;
  public int[] heightmap;
  public NativeImage image = new NativeImage(NativeImage.Format.RGBA, 16, 16, false);
  public long time;
  public MapLayer layer;
  public WorldChunk chunk;
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
    
    heightmap = new int[16 * 16];
    Arrays.fill(heightmap, -1);
  }
  
  void setChunk(WorldChunk chunk)
  {
    this.chunk = chunk;
  }
  
  public void update()
  {
    if (updating)
    {
      return;
    }
    
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
              int h = heightmap[x + z * 16];
              heightmap[x + z * 16] = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z);
//              heightmap[x + z * 16] = MapLayerProcessor.getHeight(chunk.getWorld(), getPosition(x, 0, z), false);// MapLayerProcessor.effectiveHeight(chunk, x, 255, z, false);
              col = MapLayerProcessor.processTopView(this, x, z);
//              float c = (heightmap[x + z * 16]) / 255f;
//              float[] cols = ColorUtil.toFloats(col);
//
//              col = ColorUtil.rgb(cols[0] * c, cols[1] * c, cols[2] * c);
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
    updating = false;
  }
  
  public BlockPos getPosition(int xOff, int y, int zOff)
  {
    ChunkPos chunkPos = chunk.getPos();
    return new BlockPos(chunkPos.x * 16 + xOff, y, chunkPos.z * 16 + zOff);
  }
  
  public static ChunkData fromTag(CompoundTag tag, World world)
  {
    
    
    
    return null;
  }

  public int cancelUpdate()
  {
    updating = false;
    
    return -1;
  }
}
