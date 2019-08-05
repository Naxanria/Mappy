package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.Logger;
import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;

public class ChunkData
{
  public int cx, cz;
  public int[] heightmap;
  public NativeImage image = new NativeImage(NativeImage.PixelFormat.RGBA, 16, 16, false);
  public long time;
  public MapLayer layer;
//  public WorldChunk chunk;
  public Chunk chunk;
  private boolean nether;
  private boolean updating;
  
  private ChunkData()
  { }
  
  public ChunkData(Chunk chunk, MapLayer layer)
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
  
  void setChunk(Chunk chunk)
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
  
//    Mappy.LOGGER.info("Updating " + cx + "," + cz);
    
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
              col = MapLayerProcessor.processTopViewNether(this, x, (int) Minecraft.getInstance().player.posY -1 , z);
            }
            else
            {
              int h = heightmap[x + z * 16];
              
              heightmap[x + z * 16] = MapLayerProcessor.getHeight(chunk.getWorld(), getPosition(x, 0, z), false);// MapLayerProcessor.effectiveHeight(chunk, x, 255, z, false);
              col = MapLayerProcessor.processTopView(this, x, z);
//              if (col == MapLayerProcessor.VOID_COLOR)
//              {
//                col = 0xff00ff00;
//              }
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
        
        if (MappyConfig.shaded)
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
  
  public static ChunkData fromTag(CompoundNBT tag, World world)
  {
    
    
    
    return null;
  }

  public int cancelUpdate()
  {
    updating = false;
    
    return -1;
  }
}
