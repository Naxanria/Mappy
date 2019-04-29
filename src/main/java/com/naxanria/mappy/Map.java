package com.naxanria.mappy;

import com.naxanria.mappy.client.MapGUI;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Map
{
  private static final MinecraftClient client = MinecraftClient.getInstance();
  
  private int size = 64;
  private int width = size, height = size;
  private int sizeX = size, sizeZ = size;
  private int lastX = Integer.MAX_VALUE, lastZ = Integer.MAX_VALUE;
  private int ticks = 0;
  
  private NativeImage image;
  
  public Map()
  {
    // todo: check what that boolean value actually does.
    image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
  }
  
  public void update()
  {
    PlayerEntity player = client.player;
    
    if (player == null)
    {
      lastX = Integer.MAX_VALUE;
      lastZ = Integer.MAX_VALUE;
      
      return;
    }
    
    if (ticks++ % 5 != 0)
    {
      return;
    }
  
    BlockPos pos = player.getBlockPos();
//    int d = Math.abs(pos.getX() - lastX)  + Math.abs(pos.getZ() - lastZ);
    if (lastX != pos.getX() || lastZ != pos.getZ() || ticks % 60 == 0)
    {
      generate(player);
      
      lastX = pos.getX();
      lastZ = pos.getZ();
  
      MapGUI.instance.markDirty();
    }
  }
  
  public void generate(PlayerEntity player)
  {
    World world = player.world;
    BlockPos pos = player.getBlockPos();
    
    int startX = pos.getX() - sizeX / 2;
    int startZ = pos.getZ() - sizeZ / 2;
    int endX = startX + sizeX;
    int endZ = startZ + sizeZ;
  
//    player.sendMessage(new StringTextComponent("[" + startX + "-" + endX + "(" + (endX - startX) + "), " + startZ + "-" + endZ + "(" + (endZ - startZ) + ")]"));
    
    for (int x = startX, px = 0; x < endX; x++, px++)
    {
      for (int z = startZ, pz = 0; z < endZ; z++, pz++)
      {
        int col = 0xff007700;
        
        BlockPos blockPos = new BlockPos(x, 64, z);
        Chunk chunk = world.getWorldChunk(blockPos);
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE);
        
        int y = heightmap.get(x & 15, z & 15) - 1;
        
        BlockPos bpos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(bpos.up()).isAir() ? world.getBlockState(bpos) : world.getBlockState(bpos.up());
//        state = Blocks.LAVA.getDefaultState();
        col = state.getMaterial().getColor().getRenderColor(2);
//        col = state.getMaterial().getColor().color | 0xff000000;
//        col = state.getBlock().getMapColor(state, world, bpos).color | 0xff000000;
        
//        FluidState fluidState = world.getFluidState(bpos);
//        if (!fluidState.isEmpty())
//        {
//          System.out.println(fluidState);
//        }
//

//        FluidTags.WATER
//        if (fluidState.matches(FluidTags.WATER) || fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER)
//        {
//          col = 0xff2222ff;
//        }
//        else if (fluidState.matches(FluidTags.LAVA) || fluidState.getFluid() == Fluids.LAVA || fluidState.getFluid() == Fluids.FLOWING_LAVA)
//        {
//          col = 0xffaa3030;
//        }
//        else
//        {
//          col = state.getMaterial().getColor().color | 0xff000000;
//        }
  
        image.setPixelRGBA(px, pz, col);
      }
    }
    
    int s = 4;
    image.fillRGBA(width / 2 - s, height / 2 - s, s, s, 0xff00ff00);
    
  }
  
  public NativeImage getImage()
  {
    return image;
  }
}
