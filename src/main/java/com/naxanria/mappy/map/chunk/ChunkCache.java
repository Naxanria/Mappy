package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapLayer;
import com.naxanria.mappy.map.MapLayerProcessor;
import com.naxanria.mappy.util.BiInteger;
import com.naxanria.mappy.util.ImageUtil;
import com.naxanria.mappy.util.MappyFileUtil;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ChunkCache
{
  private static final NativeImage BLACK_IMAGE = new NativeImage(NativeImage.PixelFormat.RGBA, 16, 16, false);
  private static MapLayer currentLayer = MapLayer.TOP_VIEW;
  private static HashMap<Integer, HashMap<MapLayer, ChunkCache>> instances = new HashMap<>();
  private static ChunkIOManager ioManager = new ChunkIOManager();
  
  static
  {
    BLACK_IMAGE.fillAreaRGBA(0, 0, 16, 16, 0xff000000);
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
    int dimId = world.dimension.getType().getId();
    
    HashMap<MapLayer, ChunkCache> layers = getLayers(dimId);
    
    if (layers.containsKey(currentLayer))
    {
      ChunkCache loader = layers.get(currentLayer);
      if (loader.world != world)
      {
//        loader.world = world;
        loader.world = world;
        loader.clear();
        NativeImage img = Mappy.map.getImage();
        img.fillAreaRGBA(0, 0, img.getWidth(), img.getHeight(), MapLayerProcessor.BLACK);
        
        System.out.println("Updated world " + world + " " + dimId);
      }
      
      return loader;
    }
    
    ChunkCache loader = new ChunkCache(currentLayer, world);
    layers.put(currentLayer, loader);
    
    return loader;
  }
  
  private void clear()
  {
    data.clear();
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
  
//  private HashMap<BiValue<Integer, Integer>, ChunkData> data = new HashMap<>();
  private HashMap<BiInteger, SuperChunk> data = new HashMap<>();
  
  
  private int updateIndex = 0;
  private int updatePerCycle = 10;
  private long lastPrune = 0;
  private long lastSave = 0;
  private long pruneDelay = 1000;
  private int pruneAmount = 500;
  
  private ChunkCache(MapLayer layer, World world)
  {
    this.layer = layer;
    this.world = world;
  }
  
  public void update(Map map, int x, int z)
  {
    update(map.getImage(), map.getSize(), x, z);
  }
  
  public void update(NativeImage image, int size, int x, int z)
  {
    updatePerCycle = MappyConfig.updatePerCycle;
    pruneDelay = MappyConfig.pruneDelay * 1000;
    pruneAmount = MappyConfig.pruneAmount;
    
//    int size = map.getSize();
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
            Chunk chunk = world.getChunk(cx, cz);
            if (!chunk.isEmpty())
            {
//              System.out.println("updated empty chunk! " + chunk.getPos().toString());
              chunkData.chunk = chunk;
              chunkData.cx = chunk.getPos().x;
              chunkData.cz = chunk.getPos().z;
            }
          }
          
          if (!chunkData.chunk.isEmpty())
          {
            chunkData.update();
          }
        }
        
        ImageUtil.writeIntoImage(chunkData.image, image, px * 16 + xOff, pz * 16 + zOff);
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
    
    if (now - lastSave > 1000 * 120)
    {
//      Mappy.LOGGER.info("Saving...");
      ioManager.saveAll();
      lastSave = now;
    }
  }
  
  private void prune(int max)
  {
//    int p = 0;
//    long now = System.currentTimeMillis();
//
//    List<BiValue<Integer, Integer>> toRemove = new ArrayList<>();
//    for (BiValue<Integer, Integer> key :
//      data.keySet())
//    {
//      ChunkData chunkData = data.get(key);
//      if (now - chunkData.time >= 10000)
//      {
//        toRemove.add(key);
//        p++;
//        if (p >= max)
//        {
//          break;
//        }
//      }
//    }
//
//    for (BiValue<Integer, Integer> key :
//      toRemove)
//    {
//      save(data.get(key));
//      data.remove(key);
//    }
//
//    if (p > 0)
//    {
////      System.out.println("Purged " + p + " chunks from cache");
//    }
  }
  
  public SuperChunk getSuperChunk(int cx, int cz)
  {
    BiInteger key = new BiInteger(cx / 16, cz / 16);
    if (data.containsKey(key))
    {
      return data.get(key);
    }
    
    // load from disk
    SuperChunk loaded = ioManager.load(ioManager.getFile(world.dimension.getType().getId(), key.A, key.B));
    if (loaded == null)
    {
      loaded = new SuperChunk(key.A, key.B);
      ioManager.MarkForSave(loaded);
    }
  
    data.put(key, loaded);
    
    return loaded;
  }
  
  public ChunkData getChunk(int cx, int cz)
  {
    return getChunk(cx, cz, true);
  }
  
  public ChunkData getChunk(int cx, int cz, boolean update)
  {
    SuperChunk superChunk = getSuperChunk(cx, cz);
  
    ChunkData data = superChunk.getChunk(cx, cz);
    
    if (data != null)
    {
      if (update)
      {
        ChunkPos pos = data.chunk.getPos();
        if (pos.x == cx && pos.z == cz)
        {
//          save(data);
//
          return data;
        }
    
        data.cx = cx;
        data.cz = cz;
//        Mappy.LOGGER.info("Chunk pos not correct! [" + cx + "," + cz + "] != [" + pos.x + "," + pos.z + "]");
    
        // save it
    
      }
      else
      {
        return data;
      }
    }

//    // todo: load from disk.
//
    ChunkData chunkData = new ChunkData(world.getChunk(cx, cz), layer);
    superChunk.setChunk(chunkData);
//
//    // look if on disk
//    File dataFile = getFile(world.dimension.getType().getId(), cx, cz);
//
//    chunkData = load(dataFile);
//
//    if (chunkData == null)
//    {
//      Chunk chunk = world.getChunk(cx, cz);
//
//      chunkData = new ChunkData(chunk, currentLayer);
//
//      if (update)
//      {
//        chunkData.update();
//        save(chunkData);
//      }
//
//    }
    
//    if (chunkData != null)
//    {
//      data.put(key, chunkData);
//    }
    
    return chunkData;
  }
  
  protected File getFile(int worldId, int cx, int cz)
  {
    String id = cx + "_" + cz + ".dat";
    File subDir = MappyFileUtil.createSubDir(MappyFileUtil.getSaveDirectory(), "/data/");
    if (!subDir.exists())
    {
      Mappy.LOGGER.info("Creating folder 'data'");
      subDir.mkdir();
    }
    subDir = new File(subDir, worldId + "/");
    if (!subDir.exists())
    {
      Mappy.LOGGER.info("Creating folder '" + worldId + "'");
      subDir.mkdir();
    }
    return new File(subDir, id);
  }
  
  private ChunkData load(File file)
  {
    ChunkData chunkData = null;
    
    if (!file.exists())
    {
      return null;
    }
    
    try
    {
      CompoundNBT tag = CompressedStreamTools.read(file);
      if (tag != null)
      {
        chunkData = ChunkData.fromTag(tag , world);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return chunkData;
  }
  
  private void save(ChunkData data)
  {
//    Mappy.LOGGER.info("Trying to save chunk " + data.cx + "," + data.cz);
    
    File file = getFile(world.dimension.getType().getId(), data.cx, data.cz);
    
    CompoundNBT tag = ChunkData.toTag(data);
    try
    {
      if (!file.exists())
      {
//        Mappy.LOGGER.info("Creating new file for '" + file + "'");
        file.createNewFile();
      }
      CompressedStreamTools.safeWrite(tag, file);
    }
    catch (IOException e)
    {
      Mappy.LOGGER.error("Failed to write chunk data to '" + file + "'");
      e.printStackTrace();
    }
  }
}
