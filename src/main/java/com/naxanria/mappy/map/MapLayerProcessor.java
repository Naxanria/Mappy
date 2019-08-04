package com.naxanria.mappy.map;

import com.naxanria.mappy.config.Settings;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.chunk.ChunkData;
import com.naxanria.mappy.util.StateUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class MapLayerProcessor
{
  public static final int BLACK = 0xff000000;
  
  // Get effective height for shading purposes.
  public static int effectiveHeight(Chunk chunk, int x, int yStart, int z, boolean skipLiquid)
  {
    World world = chunk.getWorld();

    BlockPos worldPos = new BlockPos(x + chunk.getPos().x * 16, 42, z + chunk.getPos().z * 16);

    Chunk realChunk;
    if (x < 0 || x > 15 || z < 0 || z > 15)
    {
      // We were passed in coordinates not actually in the chunk we were passed, so fiddle the numbers to get a chunk that actually matches the coords,
      // and coords that are correct rel to that chunk.
      realChunk = world.getChunkAt(worldPos);
     
      if (x < 0)
      {
        x += 16;
      }
      else if (x > 15)
      {
        x -= 16;
      }
      
      if (z < 0)
      {
        z += 16;
      }
      else if (z > 15)
      {
        z -= 16;
      }
    }
    else
    {
      realChunk = chunk;
    }
  
    ChunkData data = ChunkCache.getPreLoader(chunk.getWorld()).getChunk(realChunk.getPos().x, realChunk.getPos().z, false);
//    Heightmap heightmap = realChunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
    int y = data.heightmap[x + z * 16];
    
    // if chunk is not loaded, return
    if (chunk.isEmpty() || !world.getChunkProvider().isChunkLoaded(realChunk.getPos()))
    {
      return y == -1 ? yStart : y;
    }

    // Right, now that we know the y, update worldPos with it, so we can do whatever extra junk we need to do to get a final y we like.
    worldPos = new BlockPos(worldPos.getX(), y - 1, worldPos.getZ());


    BlockState state;
    // Correct y level if the top block we found was water(ish).
    
    if (y < 0)
    {
      y = yStart;
    }
    
    boolean loop = false;
    do
    {
      worldPos = new BlockPos(worldPos.getX(), y - 1, worldPos.getZ());
      state = world.getBlockState(worldPos);
      y--;
      if (skipLiquid)
      {
        loop = state.getMaterial().isLiquid();
      }
      if (!loop)
      {
        if (state.getMaterial() == Material.OCEAN_PLANT)
        {
          loop = true;
        }
      }

      loop &= y > 0;
    }
    while (loop);
    
    return y;// < 0 ? getHeight(world, worldPos, skipLiquid) : y;

//    return y;
  }

  public static int shadeTopView(ChunkData chunkData, int x, int z)
  {
    Chunk chunk = chunkData.chunk;

    int y_here = effectiveHeight(chunk, x, - 1, z, true);
    int y_east = effectiveHeight(chunk, x + 1, y_here, z, true);
    int y_south = effectiveHeight(chunk, x, y_here, z - 1, true);

    // https://en.wikipedia.org/wiki/Terrain_cartography#Shaded_relief states that shading convention is that the light is from the top-left corner
    // of the map.
    int y_diff_east = y_east - y_here;
    int y_diff_south = -(y_south - y_here);

    int y_diff = y_diff_east + y_diff_south;

    int base_color;
    if (y_diff < 0)
    {
      base_color = 0x222222;
      y_diff = -y_diff;
    }
    else
    {
      base_color = 0xDDDDDD;
    }

    int maxDiff = Settings.maxDifference;
    
    if (y_diff > maxDiff)
    {
      y_diff = maxDiff;
    }

    int alpha = (int)(255.0 * y_diff / (double) maxDiff);

    return (alpha << 24) | base_color;
  }

  public static int processTopView(ChunkData chunk, int x, int z)
  {
    Chunk worldChunk = chunk.chunk;
    World world = worldChunk.getWorld();
 
    int y = chunk.heightmap[x + z * 16];
    
    if (y < 0)
    {
      y = effectiveHeight(chunk.chunk, x, 255, z, false);
    }


    BlockPos worldPos = new BlockPos(x + worldChunk.getPos().x * 16, y, z + worldChunk.getPos().z * 16);
  
    BlockState state = world.getBlockState(worldPos);
  
    if (!StateUtil.isAir(state))
    {
      // handle special cases?
      if (world.getBlockState(worldPos.up()).getBlock() == Blocks.SNOW)
      {
        return 0xffffffff; // white
      }
      return color(world, state, worldPos);
    }
  
    // return the cached pixel
    return 0xff676676;
  }
  
  public static int processTopViewNether(ChunkData chunk, int x, int y, int z)
  {
    if (y >= 128)
    {
      return processTopView(chunk, x, z);
    }
    Chunk worldChunk = chunk.chunk;
    World world = worldChunk.getWorld();
    BlockPos worldPos = new BlockPos(x, y, z);
    boolean up = !StateUtil.isAir(world.getBlockState(worldPos));
    
    return processTopViewNether(chunk, x, y, z, up);
  }
  
  private static int processTopViewNether(ChunkData chunk, int x, int y, int z, boolean up)
  {
    Chunk worldChunk = chunk.chunk;
    World world = worldChunk.getWorld();
    
    do
    {
      if (up)
      {
        y++;
      }
      else
      {
        y--;
      }
      
      BlockPos worldPos = new BlockPos(x + worldChunk.getPos().x * 16, y, z + worldChunk.getPos().z * 16);
      BlockState state = world.getBlockState(worldPos);
  
      boolean air = StateUtil.isAir(state);
      if (up && air)
      {
        worldPos = worldPos.down();
        state = world.getBlockState(worldPos);
        return color(world, state, worldPos);
      }
      else if (!up && !air)
      {
        worldPos = worldPos.up();
        state = world.getBlockState(worldPos);
        return color(world, state, worldPos);
      }
    }
    while (y < world.getHeight() && y > 0);
    
    return BLACK;
  }
  
  private static int color(World world, BlockState state, BlockPos pos)
  {
    return state.getBlock().getMaterialColor(state, world, pos).getMapColor(2);
  }
  
  public static int getHeight(World world, BlockPos pos, boolean ignoreLiquid)
  {
    return getHeight(world, pos, ignoreLiquid, world.getHeight());
  }
  
  public static int getHeight(World world, BlockPos pos, boolean ignoreLiquid, int startHeight)
  {

    BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), startHeight, pos.getZ());
    
    while (checkPos.getY() > 0)
    {
      BlockState state = world.getBlockState(checkPos);
      if (StateUtil.isAir(state) || (ignoreLiquid && state.getMaterial().isLiquid()))
      {
        checkPos.setY(checkPos.getY() - 1);
        continue;
      }
      
      return checkPos.getY();
    }
    
    return 0;
  }
}
