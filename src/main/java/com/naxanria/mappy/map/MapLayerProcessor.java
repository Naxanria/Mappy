package com.naxanria.mappy.map;

import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.chunk.ChunkData;
import com.naxanria.mappy.util.ColorUtil;
import com.naxanria.mappy.util.StateUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;

public class MapLayerProcessor
{
  public static final int BLACK = 0xff000000;
  public static final int VOID_COLOR = 0xff676676;
  
  public static final HashMap<Block, Integer> colorMap = new HashMap<>();
  
  static
  {
    addColor(Blocks.LAVA, 0xffff8800);
    
    addColor(Blocks.END_ROD, DyeColor.WHITE);
    addColor(Blocks.TORCH, DyeColor.YELLOW);
    
    addColor(Blocks.REDSTONE_TORCH, DyeColor.RED);
    addColor(Blocks.REDSTONE_WALL_TORCH, DyeColor.RED);
    addColor(Blocks.LEVER, DyeColor.GRAY);
    addColor(Blocks.REDSTONE_WIRE, DyeColor.RED);
    addColor(Blocks.COMPARATOR, DyeColor.RED);
    addColor(Blocks.REPEATER, DyeColor.RED);
    
    addColor(Blocks.RAIL, DyeColor.GRAY);
    addColor(Blocks.ACTIVATOR_RAIL, DyeColor.GRAY);
    addColor(Blocks.POWERED_RAIL, DyeColor.GRAY);
    addColor(Blocks.DETECTOR_RAIL, DyeColor.GRAY);
    
    addColor(Blocks.CAKE, DyeColor.WHITE);
    
    addColor(Blocks.DANDELION, DyeColor.YELLOW);
    addColor(Blocks.POPPY, DyeColor.RED);
    addColor(Blocks.BLUE_ORCHID, DyeColor.BLUE);
    addColor(Blocks.ALLIUM, DyeColor.PINK);
    addColor(Blocks.AZURE_BLUET, DyeColor.WHITE);
    addColor(Blocks.RED_TULIP, DyeColor.RED);
    addColor(Blocks.ORANGE_TULIP, DyeColor.ORANGE);
    addColor(Blocks.WHITE_TULIP, DyeColor.WHITE);
    addColor(Blocks.PINK_TULIP , DyeColor.PINK);
    addColor(Blocks.OXEYE_DAISY, DyeColor.LIGHT_GRAY);
    addColor(Blocks.CORNFLOWER , DyeColor.LIGHT_BLUE);
    addColor(Blocks.WITHER_ROSE, DyeColor.BLACK);
    addColor(Blocks.LILY_OF_THE_VALLEY, DyeColor.WHITE);
    addColor(Blocks.BROWN_MUSHROOM, DyeColor.BROWN);
    addColor(Blocks.RED_MUSHROOM, DyeColor.RED);
    addColor(Blocks.NETHER_WART, DyeColor.RED);
    addColor(Blocks.PEONY, DyeColor.PINK);
    addColor(Blocks.LILAC, DyeColor.MAGENTA);
    addColor(Blocks.ROSE_BUSH, DyeColor.RED);
    addColor(Blocks.SUNFLOWER, DyeColor.YELLOW);
    
    addColor(Blocks.FLOWER_POT, DyeColor.BROWN);
    addColor(Blocks.POTTED_OAK_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_SPRUCE_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_BIRCH_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_JUNGLE_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_ACACIA_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_DARK_OAK_SAPLING, DyeColor.GREEN);
    addColor(Blocks.POTTED_FERN, DyeColor.GREEN);
    addColor(Blocks.POTTED_DANDELION, DyeColor.YELLOW);
    addColor(Blocks.POTTED_POPPY, DyeColor.RED);
    addColor(Blocks.POTTED_BLUE_ORCHID, DyeColor.BLUE);
    addColor(Blocks.POTTED_ALLIUM, DyeColor.PINK);
    addColor(Blocks.POTTED_AZURE_BLUET, DyeColor.WHITE);
    addColor(Blocks.POTTED_RED_TULIP, DyeColor.RED);
    addColor(Blocks.POTTED_ORANGE_TULIP, DyeColor.ORANGE);
    addColor(Blocks.POTTED_WHITE_TULIP, DyeColor.WHITE);
    addColor(Blocks.POTTED_PINK_TULIP, DyeColor.PINK);
    addColor(Blocks.POTTED_OXEYE_DAISY, DyeColor.LIGHT_GRAY);
    addColor(Blocks.POTTED_CORNFLOWER, DyeColor.LIGHT_BLUE);
    addColor(Blocks.POTTED_LILY_OF_THE_VALLEY, DyeColor.WHITE);
    addColor(Blocks.POTTED_WITHER_ROSE, DyeColor.BLACK);
    addColor(Blocks.POTTED_RED_MUSHROOM, DyeColor.RED);
    addColor(Blocks.POTTED_BROWN_MUSHROOM, DyeColor.BROWN);
    addColor(Blocks.POTTED_DEAD_BUSH, DyeColor.BROWN);
    addColor(Blocks.POTTED_CACTUS, DyeColor.GREEN);
    
    addColor(Blocks.GLASS, 0xffDEDEDE);
    addColor(Blocks.GLASS_PANE, 0xffDEDEDE);
    
    addStained(Blocks.WHITE_STAINED_GLASS_PANE);
    addStained(Blocks.ORANGE_STAINED_GLASS_PANE);
    addStained(Blocks.MAGENTA_STAINED_GLASS_PANE);
    addStained(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
    addStained(Blocks.YELLOW_STAINED_GLASS_PANE);
    addStained(Blocks.LIME_STAINED_GLASS_PANE);
    addStained(Blocks.PINK_STAINED_GLASS_PANE);
    addStained(Blocks.GRAY_STAINED_GLASS_PANE);
    addStained(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
    addStained(Blocks.CYAN_STAINED_GLASS_PANE);
    addStained(Blocks.PURPLE_STAINED_GLASS_PANE);
    addStained(Blocks.BLUE_STAINED_GLASS_PANE);
    addStained(Blocks.BROWN_STAINED_GLASS_PANE);
    addStained(Blocks.GREEN_STAINED_GLASS_PANE);
    addStained(Blocks.RED_STAINED_GLASS_PANE);
    addStained(Blocks.BLACK_STAINED_GLASS_PANE);
  }
  
  public static void addStained(Block block)
  {
    if (block instanceof StainedGlassPaneBlock)
    {
      addColor(block, ((StainedGlassPaneBlock) block).getColor());
    }
  }
  
  public static void addColor(Block block, DyeColor color)
  {
    addColor(block, color.getColorValue());
  }
  
  public static void addColor(Block block, int color)
  {
    colorMap.put(block, color);
  }
  
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
    
    return y + 1;// < 0 ? getHeight(world, worldPos, skipLiquid) : y;

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

    int maxDiff = MappyConfig.maxDifference;
    
    if (y_diff > maxDiff)
    {
      y_diff = maxDiff;
    }

    int alpha = (int)(255.0 * y_diff / (double) maxDiff);

    return (alpha << 24) | base_color;
  }

  public static int processTopView(ChunkData chunkData, int x, int z)
  {
    Chunk worldChunk = chunkData.chunk;
    World world = worldChunk.getWorld();
 
    int y = chunkData.heightmap[x + z * 16];
//
//    if (y < 0)
//    {
//      return 0xffff00ff;
//    }
//    int c = y * 25;
//
//    BlockPos worldPos = new BlockPos(x + worldChunk.getPos().x * 16, y, z + worldChunk.getPos().z * 16);
//
//    BlockState state = world.getBlockState(worldPos);
//
//    return color(world, state, worldPos);
    
    if (y < 0)
    {
      y = effectiveHeight(chunkData.chunk, x, 255, z, false);
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

    return VOID_COLOR;
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
  
  public static int color(World world, BlockState state, BlockPos pos)
  {
    int brightness = 1;
//    int skyLight = 15 - world.getSkylightSubtracted();
//
//    int l = 0;
//    if (skyLight == 15)
//    {
//      l = 15;
//    }
//    else
//    {
//      l = Math.max(world.getLightFor(LightType.BLOCK, pos.up()), skyLight);
//    }
//
//    if (l > 12)
//    {
//      brightness = 2;
//    }
//    else if (l > 8)
//    {
//      brightness = 1;
//    }
//    else
//    {
//      brightness = 3;
//    }
    
    Block block = state.getBlock();
    if (colorMap.containsKey(block))
    {
      return getMapColor(colorMap.get(block), brightness);
    }
    
    return state.getMaterialColor(world, pos).getMapColor(brightness);
//    return state.getMaterial().getColor().getMapColor(2);
  }
  
  public static int getMapColor(int colorValue, int index)
  {
    int i = 220;
    if (index == 3)
    {
      i = 135;
    }
  
    if (index == 2)
    {
      i = 255;
    }
  
    if (index == 1)
    {
      i = 220;
    }
  
    if (index == 0)
    {
      i = 180;
    }
  
    int j = (colorValue >> 16 & 255) * i / 255;
    int k = (colorValue >> 8 & 255) * i / 255;
    int l = (colorValue & 255) * i / 255;
    return -16777216 | l << 16 | k << 8 | j;
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
