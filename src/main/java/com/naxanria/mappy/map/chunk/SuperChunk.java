package com.naxanria.mappy.map.chunk;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.util.NBTIds;
import com.naxanria.mappy.util.Position2D;
import com.naxanria.mappy.util.Serializable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

/*
  @author: Naxanria
*/
public class SuperChunk implements Serializable<SuperChunk>
{
  private Position2D position;
  private ChunkData[] chunks;
  private int dimension;
  boolean toSave = false;
  
  public SuperChunk(int x, int z)
  {
    position = new Position2D(x, z);
    prepareChunksList();
  }
  
  public SuperChunk(CompoundNBT tag)
  {
    prepareChunksList();
    readFromNBT(tag);
  }
  
  public ChunkData getChunk(int chunkX, int chunkZ)
  {
    int x = chunkX & 15;
    int z = chunkZ & 15;
    
    int pos = x + z * 16;
    return chunks[pos];
  }
  
  void setChunk(ChunkData data)
  {
    chunks[getListPosition(data)] = data;
  }
  
  @Override
  public SuperChunk writeToNBT(CompoundNBT tag)
  {
    tag.putInt("X", position.getX());
    tag.putInt("Z", position.getY());
    tag.putInt("Dimension", dimension);
    
    ListNBT nbtList = new ListNBT();
    for (int i = 0; i < chunks.length; i++)
    {
      ChunkData chunkData = chunks[i];
      if (chunkData != null)
      {
        nbtList.add(ChunkData.toTag(chunkData));
      }
    }
//    Mappy.LOGGER.info("List tag type: " + nbtList.getTagType());
    tag.put("Chunks", nbtList);
  
    return this;
  }
  
  
  @Override
  public SuperChunk readFromNBT(CompoundNBT tag)
  {
    int x = tag.getInt("X");
    int z = tag.getInt("Z");
    int dim = tag.getInt("Dimension");
    
    position = new Position2D(x, z);
    dimension = dim;
  
    ListNBT chunksList = tag.getList("Chunks", NBTIds.COMPOUND);
    prepareChunksList();
    
    for (int i = 0; i < chunksList.size(); i++)
    {
      CompoundNBT chunkCompound = chunksList.getCompound(i);
      ChunkData chunkData = ChunkData.fromTag(chunkCompound, null);
      if (chunkData != null)
      {
//        Mappy.LOGGER.info("Loaded chunk " + chunkData.cx + " ," + chunkData.cz);
        setChunk(chunkData);
//        chunks[getListPosition(chunkData)] = chunkData;
      }
    }
    
    return this;
  }
  
  public int getDimension()
  {
    return dimension;
  }
  
  private void prepareChunksList()
  {
    if (chunks == null)
    {
      chunks = new ChunkData[256];
    }
    
    for (int i = 0; i < 16 * 16; i++)
    {
      chunks[i] = null;
    }
  }
  
  private int getListPosition(ChunkData data)
  {
    return getListPosition(data.cx, data.cz);
  }
  
  private int getListPosition(int cx, int cz)
  {
    return (cx & 15) + (cz & 15) * 16;
  }
  
  public int getX()
  {
    return position.getX();
  }
  
  public int getZ()
  {
    return position.getY();
  }
  
  public static Position2D getSuperChunkPosition(BlockPos pos)
  {
    int cx = pos.getX() / 16;
    int cz = pos.getZ() / 16;
    
    return new Position2D(cx / 16, cz / 16);
  }
  
  public static Position2D getSuperChunkPosition(ChunkPos pos)
  {
    return new Position2D(pos.x / 16, pos.z / 16);
  }
  
  public static Position2D getSuperChunkPosition(int chunkX, int chunkZ)
  {
    return new Position2D(chunkX / 16, chunkZ / 16);
  }
}
