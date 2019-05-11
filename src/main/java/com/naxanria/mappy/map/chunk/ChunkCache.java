package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import com.naxanria.mappy.util.BiValue;
import com.naxanria.mappy.util.ImageUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChunkCache
{
  private static final NativeImage BLACK_IMAGE = new NativeImage(NativeImage.Format.RGBA, 16, 16, false);
  private static MapLayer currentLayer = MapLayer.TOP_VIEW;
  private static HashMap<Integer, HashMap<MapLayer, ChunkCache>> instances = new HashMap<>();
  
  static
  {
    BLACK_IMAGE.fillRGBA(0, 0, 16, 16, 0xff000000);
  }
  
  public static void setCurrentLayer(MapLayer layer)
  {
    currentLayer = layer;
  }
  
  public static MapLayer getCurrentLayer()
  {
    return currentLayer;
  }
  
  public static ChunkCache getPreLoader(World world, MapLayer layer)
  {
    MapLayer temp = currentLayer;
    currentLayer = layer;
    ChunkCache loader = getPreLoader(world);
    currentLayer = temp;
    return loader;
  }
  
  public static ChunkCache getPreLoader(World world)
  {
    int dimId = world.dimension.getType().getRawId();
    
    HashMap<MapLayer, ChunkCache> layers = getLayers(dimId);
    
    if (layers.containsKey(currentLayer))
    {
      ChunkCache loader = layers.get(currentLayer);
      if (loader.world != world)
      {
        loader.world = world;
        NativeImage img = Mappy.map.getImage();
        img.fillRGBA(0, 0, img.getWidth(), img.getHeight(), MapLayerProcessor.BLACK);
        
        System.out.println("Updated world");
      }
      
      return loader;
    }
    
    ChunkCache loader = new ChunkCache(currentLayer, world);
    layers.put(currentLayer, loader);
    
    return loader;
  }
  
  private static HashMap<MapLayer, ChunkCache> getLayers(int dimId)
  {
    if (instances.containsKey(dimId))
    {
      return instances.get(dimId);
    }
    
    HashMap<MapLayer, ChunkCache> layers = new HashMap<>();
    instances.put(dimId, layers);
    
    return layers;
  }
  
  public final MapLayer layer;
  public World world;
  
  private HashMap<BiValue<Integer, Integer>, ChunkData> data = new HashMap<>();
  
  private int updateIndex = 0;
  private int updatePerCycle = 10;
  private long lastPrune = 0;
  private long pruneDelay = 1000;
  private int pruneAmount = 500;
  
  private ChunkCache(MapLayer layer, World world)
  {
    this.layer = layer;
    this.world = world;
  }
  
  public void update(Map map, int x, int z)
  {
    Config config = Config.instance;
    
    updatePerCycle = config.getUpdatePerCycle();
    pruneDelay = config.getPruneDelay() * 1000;
    pruneAmount = config.getPruneAmount();
    
    int size = map.getSize();
    int chunksSize = size / 16 + 4;
    int cxStart = x / 16 - 2;
    int cxEnd = cxStart + chunksSize;
    int czStart = z / 16 - 2;
    int czEnd = czStart + chunksSize;
  
    int xOff = cxStart * 16 - x;
    int zOff = czStart * 16 - z;
    
    long now = System.currentTimeMillis();
    
    int i = 0;
    
    int px = 0;
    for (int cx = cxStart; cx < cxEnd; cx++)
    {
      int pz = 0;
      for (int cz = czStart; cz < czEnd; cz++)
      {
        i++;

        ChunkData chunkData = getChunk(cx, cz);

        
        if (i >= updateIndex && i <= updateIndex + updatePerCycle && now - chunkData.time >= 100)
        {
          if (chunkData.chunk.isEmpty())
          {
            WorldChunk chunk = world.getWorldChunk(new BlockPos(cx * 16, 10, cz * 16));
            if (!chunk.isEmpty())
            {
//              System.out.println("updated empty chunk! " + chunk.getPos().toString());
              chunkData.chunk = chunk;
            }
          }
          
          if (!chunkData.chunk.isEmpty())
          {
            chunkData.update();
          }
        }
        
        ImageUtil.writeIntoImage(chunkData.image, map.getImage(), px * 16 + xOff, pz * 16 + zOff);
        pz++;
      }
      
      px++;
    }
    
    updateIndex += updatePerCycle;
    if (updateIndex >= chunksSize * chunksSize)
    {
      updateIndex = 0;
    }
    
    if (now - lastPrune > pruneDelay)
    {
      prune(pruneAmount);
      lastPrune = now;
    }
  }
  
  private void prune(int max)
  {
    int p = 0;
    long now = System.currentTimeMillis();
  
    List<BiValue<Integer, Integer>> toRemove = new ArrayList<>();
    for (BiValue<Integer, Integer> key :
      data.keySet())
    {
      ChunkData chunkData = data.get(key);
      if (now - chunkData.time >= 10000)
      {
        toRemove.add(key);
        p++;
        if (p >= max)
        {
          break;
        }
      }
    }
  
    for (BiValue<Integer, Integer> key :
      toRemove)
    {
      data.remove(key);
    }
    
    if (p > 0)
    {
      System.out.println("Purged " + p + " chunks from cache");
    }
  }
  
  private ChunkData getChunk(int cx, int cz)
  {
    BiValue<Integer, Integer> key = new BiValue<>(cx, cz);
    if (data.containsKey(key))
    {
      return data.get(key);
    }

    // todo: load from disk.

    WorldChunk chunk = world.getWorldChunk(new BlockPos(cx * 16, 10, cz * 16));
    
    ChunkData chunkData = new ChunkData(chunk, currentLayer);

    chunkData.update();
    data.put(key, chunkData);
    
    return chunkData;
  }
}
