package com.naxanria.mappy;

import com.naxanria.mappy.client.MapGUI;
import com.naxanria.mappy.util.ColorUtil;
import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.TriValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class Map
{
  private static final BlockState AIR_STATE = Blocks.AIR.getDefaultState();
  private static final BlockState CAVE_AIR_STATE = Blocks.CAVE_AIR.getDefaultState();
  private static final BlockState VOID_AIR_STATE = Blocks.VOID_AIR.getDefaultState();
  private static final MinecraftClient client = MinecraftClient.getInstance();
  
  
  private int size = 64;
  private int width = size, height = size;
  private int sizeX = size, sizeZ = size;
  
  private TriValue<BlockPos, BlockState, Integer> debugData;
  
  private Biome biome;
  
  private NativeImage image;
  
  private List<MapIcon.Player> players = new ArrayList<>();
  private MapIcon.Player playerIcon;
  
  public Map()
  {
    // todo: check what that boolean value actually does.
    image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
  }
  
  public void update()
  {
    PlayerEntity player = client.player;
    if (player != null)
    {
      if (playerIcon == null)
      {
        playerIcon = new MapIcon.Player(this, player, true);
      }
      
      playerIcon.setPosition(size / 2, size / 2);
      
      generate(player);
  
      MapGUI.instance.markDirty();
    }
  }
  
  public TriValue<BlockPos, BlockState, Integer> getDebugData()
  {
    return debugData;
  }
  
  public Biome getBiome()
  {
    return biome;
  }
  
  public void generate(PlayerEntity player)
  {
    World world = player.world;
    BlockPos pos = player.getBlockPos();
  
    biome = world.getBiome(pos);
    DimensionType type = world.dimension.getType();
    
    boolean nether = type == DimensionType.THE_NETHER;
    
    int startX = pos.getX() - sizeX / 2;
    int startZ = pos.getZ() - sizeZ / 2;
    int endX = startX + sizeX;
    int endZ = startZ + sizeZ;
    
    for (int x = startX, px = 0; x < endX; x++, px++)
    {
      for (int z = startZ, pz = 0; z < endZ; z++, pz++)
      {
        int col;
        int y;
        
        if (!nether)
        {
          BlockPos blockPos = new BlockPos(x, 64, z);
          WorldChunk chunk = world.getWorldChunk(blockPos);
          Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
  
          y = heightmap.get(x & 15, z & 15) - 1;
        }
        else
        {
          y = pos.getY();
        }

        BlockPos bpos = new BlockPos(x, y, z);
        BlockState state =  world.getBlockState(bpos);
        
        boolean up = !isAir(state);
        
//        col = up ? 0xffffff00 : 0xff00ffff;
        
        int tries = 100;
        do
        {
          if (!nether)
          {
            bpos = new BlockPos(x, y, z);
            state = world.getBlockState(bpos);
            
            if (!isAir(state))
            {
              break;
            }
            y--;
          }
          else
          {
            y += (up) ? 1 : -1;
            bpos = new BlockPos(x, y, z);
            state = world.getBlockState(bpos);
            
            if (up && isAir(state) || !isAir(state))
            {
              if (up)
              {
                bpos = bpos.down();
                state = world.getBlockState(bpos);
              }
              break;
            }
          }
        }
        while (y >= 0 && y <= world.getHeight() && tries-- > 0);
        

//        col = state.getMaterial().getColor().getRenderColor(2);
        col = state.getBlock().getMapColor(state, world, bpos).getRenderColor(2);
        if (nether)
        {
          col = ColorUtil.multiply(col, (up) ? 0.5f : 1);
        }
        
        if (Mappy.debugMode)
        {
          if (x == pos.getX() && z == pos.getZ())
          {
            debugData = new TriValue<>(bpos, state, col);
          }
        }
  
        image.setPixelRGBA(px, pz, col);
      }
    }
    
    // todo: make option to show players or not.
    players.clear();
    players.add(playerIcon);
    
    List<? extends PlayerEntity> players = world.getPlayers();
    for (PlayerEntity p :
      players)
    {
      if (p == player)
      {
        continue;
      }
      
      if (p.isSneaking() || p.isSpectator())
      {
        continue;
      }
      
      BlockPos ppos = p.getBlockPos();
     
      int x = ppos.getX();
      int z = ppos.getZ();
      
      if (x >= startX && x <= endX && z >= startZ && z <= endZ)
      {
        MapIcon.Player playerIcon1 = new MapIcon.Player(this, p, false);
        playerIcon1.setPosition(MapIcon.getScaled(x, startX, endX, size), MapIcon.getScaled(z, startZ, endZ, size));
        this.players.add(playerIcon1);
        
//        int drawX = MathUtil.clamp((int) (((x - startX) / ((float)sizeX)) * width) - s, 0, width - s);
//        int drawZ = MathUtil.clamp((int) (((z - startZ) / ((float)sizeZ)) * height) - s, 0, height - s);
//
//        image.fillRGBA(drawX, drawZ, s, s, 0xff009900);
      }
    }
  }
  
  protected boolean isAir(BlockState state)
  {
    return state.isAir() || state == AIR_STATE || state == CAVE_AIR_STATE || state == VOID_AIR_STATE;
  }
  
  public List<MapIcon.Player> getPlayerIcons()
  {
    return players;
  }
  
  public NativeImage getImage()
  {
    return image;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getSizeX()
  {
    return sizeX;
  }
  
  public int getSizeZ()
  {
    return sizeZ;
  }
}
