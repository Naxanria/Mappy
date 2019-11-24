package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.util.MappyFileUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
  @author: Naxanria
*/
public class ChunkIOManager
{
  private static int nextID = 0;
  private File subDir;
  
  private static int getID()
  {
    return nextID++;
  }
  
  Thread thread;
  private List<SuperChunk> toSave = new ArrayList<>();
  private List<SuperChunk> nextToSave = new ArrayList<>();
  private boolean saving = false;
  private boolean watching = true;
  
  public final int ID = getID();
  
  public ChunkIOManager(File subDir)
  {
    Mappy.LOGGER.info("Started chunk manager #" + ID);
    
    this.subDir = subDir;
    
    thread = new Thread(this::run);
    thread.start();
  }
  
  public void MarkForSave(SuperChunk chunk)
  {
    if (chunk.toSave)
    {
      return;
    }
    
    chunk.toSave = true;
    toSave.add(chunk);
  }
  
  public void startSave()
  {
    saving = true;
  }
  
  private void run()
  {
    while (watching)
    {
      if (saving)
      {
        saveAll();
        saving = false;
      }
  
      try
      {
        Thread.sleep(10 * 1000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public void stop()
  {
    Mappy.LOGGER.info("Stopping IOManager #" + ID);
    watching = false;
  }
  
  private void saveAll()
  {
    List<SuperChunk> temp = nextToSave;
    nextToSave = toSave;
    toSave = temp;
    
    int s = nextToSave.size();
    if (s == 0)
    {
      return;
    }
    long start = System.currentTimeMillis();
    int n = 0;
    for (int i = 0; i < nextToSave.size() && n < 100; i++, n++)
    {
      SuperChunk superChunk = nextToSave.get(i);
      
      if (superChunk != null)
      {
        save(superChunk);
        superChunk.toSave = false;
      }
  
      nextToSave.remove(i--);
    }
    long time = System.currentTimeMillis() - start;
    
    Mappy.LOGGER.info("Saved " + n + " [" + s + "] chunks in " + time + "ms");
  }
  
  private void save(SuperChunk superChunk)
  {
    File file = getFile(superChunk.getDimension(), superChunk.getX(), superChunk.getZ());
  
    CompoundNBT tag = new CompoundNBT();
    superChunk.writeToNBT(tag);
  
    try
    {
      if (!file.exists())
      {
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
  
  SuperChunk load(File file)
  {
    if (!file.exists())
    {
      return null;
    }
  
    try
    {
      CompoundNBT tag = CompressedStreamTools.read(file);
      if (tag != null)
      {
        Mappy.LOGGER.info("Loading " + tag.getInt("X") + "," + tag.getInt("Z"));
        return new SuperChunk(tag);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
  
  File getFile(int worldId, int cx, int cz)
  {
    String id = "sc_" + cx + "_" + cz + ".dat";
    
    File subDir = this.subDir;
    
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
}
