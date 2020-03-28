package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import com.naxanria.mappy.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;

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
  
  public boolean update()
  {
    if (updating)
    {
      return false;
    }
  
    MappyConfig.Client config = MappyConfig.getConfig();
  
    long now = System.currentTimeMillis();
    updating = true;
    boolean change = false;
//    Mappy.LOGGER.info("Updating " + cx + "," + cz);
    
    for (int x = 0; x < 16; x++)
    {
      for (int z = 0; z < 16; z++)
      {
        int col = MapLayerProcessor.BLACK;
        int oldCol = image.getPixelRGBA(x, z);
        int oldHeight = heightmap[x + z * 16];
        switch (layer)
        {
          case TOP_VIEW:
            if (nether)
            {
              col = MapLayerProcessor.processTopViewNether(this, x, (int) Minecraft.getInstance().player.getPositionVec().y - 1 , z);
            }
            else
            {
//              int h = heightmap[x + z * 16];
              if (config.forceHeightmapUse.get())
              {
                heightmap[x + z * 16] = chunk.getWorld().getHeight(Heightmap.Type.WORLD_SURFACE, cx * 16 + x, cz * 16 + z) - 1;
              }
              else
              {
                heightmap[x + z * 16] = MapLayerProcessor.getHeight(chunk.getWorld(), getPosition(x, 0, z), false);// MapLayerProcessor.effectiveHeight(chunk, x, 255, z, false);
              }
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
            if (heightmap[x + z * 16] != oldHeight || col != oldCol)
            {
              change = true;
            }
            break;
          case CAVES:
            break;
        }
        
        if (!updating)
        {
          time = now;
          return false;
        }
        
        image.setPixelRGBA(x, z, col);
        
        if (config.shaded.get())
        {
          image.blendPixel(x, z, MapLayerProcessor.shadeTopView(this, x, z));
        }
      }
    }
    
    time = now;
    updating = false;
    return change;
  }
  
  public BlockPos getPosition(int xOff, int y, int zOff)
  {
    ChunkPos chunkPos = chunk.getPos();
    return new BlockPos(chunkPos.x * 16 + xOff, y, chunkPos.z * 16 + zOff);
  }
  
  public static ChunkData fromTag(CompoundNBT tag, World world)
  {
    ChunkData data = new ChunkData();
    data.cx = tag.getInt("CX");
    data.cz = tag.getInt("CZ");
    data.layer = MapLayer.values()[MathUtil.clamp(tag.getInt("MAP_LAYER"), 0, MapLayer.values().length - 1)];
    
    if (tag.contains("PIXELS") && tag.contains("HEIGHT_MAP"))
    {
      byte[] pixels = tag.getByteArray("PIXELS");
      byte[] heightMap = tag.getByteArray("HEIGHT_MAP");

      data.image = new NativeImage(NativeImage.PixelFormat.RGBA, 16, 16, true);
      data.heightmap = new int[256];

      for(int i = 0; i < 256; i++)
      {
        data.heightmap[i] = heightMap[i] & 0xFF;
        data.image.setPixelRGBA(i % 16, i / 16, NativeImage.getCombined(255, pixels[i * 3 + 2], pixels[i * 3 + 1], pixels[i * 3 + 0]));
      }
    }
    else
    {
      return null;
    }
    
    return data;
  }
  
  public static CompoundNBT toTag(ChunkData data)
  {
    return toTag(data, null);
  }
  
  public static CompoundNBT toTag(ChunkData data, CompoundNBT tag)
  {
    if (tag == null)
    {
      tag = new CompoundNBT();
    }
    
    tag.putInt("CX", data.cx);
    tag.putInt("CZ", data.cz);
    
    tag.putInt("MAP_LAYER", data.layer.ordinal());

    byte[] pixels = new byte[256 * 3];
    byte[] heightMap = new byte[256];

    for (int i = 0; i < 256; i++)
    {
      int col = data.image.getPixelRGBA(i %16, i / 16);
      pixels[i * 3 + 0] = (byte)NativeImage.getRed(col);
      pixels[i * 3 + 1] = (byte)NativeImage.getGreen(col);
      pixels[i * 3 + 2] = (byte)NativeImage.getBlue(col);
      heightMap[i] = (byte)Math.min(255, data.heightmap[i]);
    }
    
    tag.putByteArray("PIXELS", pixels);
    tag.putByteArray("HEIGHT_MAP", heightMap);
    
    return tag;
  }
  
  public int cancelUpdate()
  {
    updating = false;
    
    return -1;
  }
}
