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
  private List<SuperChunk> toSave = new ArrayList<>();
  
  public void MarkForSave(SuperChunk chunk)
  {
    if (chunk.toSave)
    {
      return;
    }
    
    chunk.toSave = true;
    toSave.add(chunk);
  }
  
  public void saveAll()
  {
    int s = toSave.size();
    if (s == 0)
    {
      return;
    }
    int n = 0;
    for (int i = 0; i < toSave.size() && n < 100; i++, n++)
    {
      SuperChunk superChunk = toSave.get(i);
      
      if (superChunk != null)
      {
        save(superChunk);
        superChunk.toSave = false;
      }
      
      toSave.remove(i--);
    }
    
    Mappy.LOGGER.info("Saved " + n + " [ " + s + "] chunks");
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
}
