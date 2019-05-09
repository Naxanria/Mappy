package com.naxanria.mappy.map;

import com.naxanria.mappy.util.StateUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MapLayerProcessor
{
  public static final int BLACK = 0xff000000;
  
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
