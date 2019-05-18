package com.naxanria.mappy.map;

import com.naxanria.mappy.util.StateUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MapLayerProcessor
{
  public static final int BLACK = 0xff000000;
  

  // Returns an rgba color?
  public static int shadeTopView(WorldChunk chunk, int x, int z)
  {
    //World world = chunk.getWorld();

    if (x==15 || z==0) {
      return 0;
    }

    Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
    int y_here = heightmap.get(x, z);
    int y_east = heightmap.get(x+1, z);
    int y_south = heightmap.get(x, z-1);

    // https://en.wikipedia.org/wiki/Terrain_cartography#Shaded_relief states that shading convention is that the light is from the top-left corner
    // of the map.
    int y_diff_east = y_east - y_here;
    int y_diff_south = -(y_south - y_here);

    int y_diff = y_diff_east + y_diff_south;

    int base_color;
    if (y_diff < 0) {
      // Black
      base_color = 0;
      y_diff = -y_diff;
    } else {
      // White
      base_color = 0xFFFFFF;
    }
    
    if (y_diff > 8) {
      y_diff = 8;
    }

    int alpha = (int)(255.0 * y_diff/8.0);

    return (alpha << 24) | base_color;
  }

  public static int processTopView(WorldChunk chunk, int x, int z)
  {
    World world = chunk.getWorld();
 
    Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
    int y = heightmap.get(x, z) - 1;

    BlockPos worldPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
  
    BlockState state = world.getBlockState(worldPos);
  
    if (!StateUtil.isAir(state))
    {
      // handle special cases?
      if (world.getBlockState(worldPos.up()).getBlock() == Blocks.SNOW)
      {
        return 0xffffffff; // white
      }
      return state.getTopMaterialColor(chunk.getWorld(), worldPos).getRenderColor(2);
    }
  
    return BLACK;
  }
  
  public static int processTopViewNether(WorldChunk chunk, int x, int y, int z)
  {
    World world = chunk.getWorld();
    BlockPos worldPos = new BlockPos(x, y, z);
    boolean up = !StateUtil.isAir(world.getBlockState(worldPos));
    
    return processTopViewNether(chunk, x, y, z, up);
  }
  
  private static int processTopViewNether(WorldChunk chunk, int x, int y, int z, boolean up)
  {
    World world = chunk.getWorld();
    
    if (up)
    {
      y++;
    }
    else
    {
      y--;
    }
    
    if (y > world.getHeight() || y < 0)
    {
      return BLACK;
    }

    BlockPos worldPos = new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
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
    
    return processTopViewNether(chunk, x, y, z, up);
  }
  
  private static int color(World world, BlockState state, BlockPos pos)
  {
    return state.getTopMaterialColor(world, pos).getRenderColor(2);
  }
}
