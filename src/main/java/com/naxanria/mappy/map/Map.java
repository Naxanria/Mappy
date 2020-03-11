package com.naxanria.mappy.map;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.gui.Alignment;
import com.naxanria.mappy.config.ConfigBase;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.event.EventListener;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.waypoint.IconType;
import com.naxanria.mappy.map.waypoint.WayPoint;
import com.naxanria.mappy.map.waypoint.WayPointEditor;
import com.naxanria.mappy.map.waypoint.WayPointManager;
import com.naxanria.mappy.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Map
{
  private static final BlockState AIR_STATE = Blocks.AIR.getDefaultState();
  private static final BlockState CAVE_AIR_STATE = Blocks.CAVE_AIR.getDefaultState();
  private static final BlockState VOID_AIR_STATE = Blocks.VOID_AIR.getDefaultState();
  
  private static final Minecraft client = Minecraft.getInstance();
  
  private MapInfoLineManager manager;
  private MapInfoLine playerPositionInfo = new MapInfoLine(Alignment.Center, "0 0 0");
  private MapInfoLine biomeInfo = new MapInfoLine(Alignment.Center, "plains");
  private MapInfoLine inGameTimeInfo = new MapInfoLine(Alignment.Center, "00:00");
  private MapInfoLine fpsInfo = new MapInfoLine(Alignment.Center, "60 fps");
  private MapInfoLine directionInfo = new MapInfoLine(Alignment.Center, "north");
  
  private int size = 64;
  private int width = size, height = size;
  private int sizeX = size, sizeZ = size;
  private int scale = 1;
  
  private TriValue<BlockPos, BlockState, Integer> debugData;
  
  private Biome biome;
  
  private NativeImage image;
  
  private List<MapIcon.Player> players = new ArrayList<>();
  private MapIcon.Player playerIcon;
  private List<MapIcon.Waypoint> waypoints = new ArrayList<>();
  private List<MapIcon.Entity> entities = new ArrayList<>();
  
  
  private PlayerEntity locPlayer = null;
  
  private EffectState effects = EffectState.NONE;
  
  
  private boolean showMap;
  private boolean showPosition;
  private boolean showTime;
  private boolean showBiome;
  
  public Map()
  {
    // todo: check what that boolean value actually does.
    image = new NativeImage(NativeImage.PixelFormat.RGBA, width, height, false);
    
    manager = new MapInfoLineManager(this);
  }
  
  public void update()
  {
    if (!Mappy.showMap)
    {
      return;
    }
    
    if (Minecraft.getInstance().currentScreen instanceof WorldMapGUI)
    {
      // dont update while in the worldmap screen
      return;
    }
  
    Integer newSize = MappyConfig.config.mapSize.get();
    if (newSize != size || size != image.getWidth())
    {
      resize(newSize);
    }
  
    PlayerEntity player = client.player;
    if (player != null)
    {
      if (playerIcon == null)
      {
        playerIcon = new MapIcon.Player(this, player, true);
      }
      
      if (locPlayer == null || locPlayer != player)
      {
//        WayPointManager.INSTANCE.load();
        locPlayer = player;
      }
      
      itemCheck();
      
      playerIcon.setPosition(size / 2, size / 2);
      
      generate(player);
  
      updateInfo(player);
      
      updateStatusEffects();
      
//      image.fillAreaRGBA(10, 10, 10, 10, 0xff0000ff);

      MapGUI.instance.markDirty();
    }
    else
    {
      locPlayer = null;
    }
  }
  
  private void itemCheck()
  {
    boolean inHotBar = MappyConfig.inHotBar;
    showMap = MappyConfig.mapItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, MappyConfig.mapItem);
    showPosition = MappyConfig.showPosition && (MappyConfig.positionItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, MappyConfig.positionItem));
    showTime = MappyConfig.showTime && (MappyConfig.timeItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, MappyConfig.timeItem));
    showBiome = MappyConfig.showBiome && (MappyConfig.biomeItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, MappyConfig.biomeItem));
  }
  
  private void updateStatusEffects()
  {
    /*
    * Based on code by ThexXTURBOXx in pull request #5
    * */
    Collection<EffectInstance> statusEffects = locPlayer.getActivePotionEffects();
    if (statusEffects.size() > 0)
    {
      effects = EffectState.BENEFICIAL;
      boolean showing = false;
      for (EffectInstance e :
        statusEffects)
      {
        showing |= e.isShowIcon();
        if (!e.getPotion().isBeneficial())
        {
          effects = EffectState.HARMFUL;
          if (showing)
          {
            break;
          }
        }
      }
      
      if (!showing)
      {
        effects = EffectState.NONE;
      }
    }
    else
    {
      effects = EffectState.NONE;
    }
  }
  
  private void resize(int newSize)
  {
    image = new NativeImage(NativeImage.PixelFormat.RGBA, newSize, newSize, false);
    System.out.println("Map resized to " + newSize + "x" + newSize);
    size = newSize;
  }
  
  public void onConfigChanged()
  {
    int configScale = MappyConfig.scale;
    int configSize = MappyConfig.mapSize;
    
    boolean resize = false;
    
    if (configScale != scale)
    {
      scale = configScale;
      resize = true;
    }
    

    if (configSize != size)
    {
      size = configSize;
      resize = true;
    }
  
    if (resize)
    {
      size = configSize * scale;
      resize(size);
    }
  }
  
  private void updateInfo(PlayerEntity player)
  {
    manager.clear();
    
    if (showPosition)
    {
      BlockPos playerPos = client.player.getPosition();
      playerPositionInfo.setText(playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ());
      manager.add(playerPositionInfo);
    }
    
    if (showBiome)
    {
      biomeInfo.setText(I18n.format(biome.getTranslationKey()));
      manager.add(biomeInfo);
    }
    
    if (MappyConfig.showFPS)
    {
      String debug = client.debug;
      String fpsString = debug.substring(0, debug.indexOf(' ', debug.indexOf(' ') + 1));
      fpsInfo.setText(fpsString);
      manager.add(fpsInfo);
    }
    
    if (showTime)
    {
      inGameTimeInfo.setText(getTimeFormatted(client.world.getDayTime()));
      manager.add(inGameTimeInfo);
    }
    
    if (MappyConfig.showDirection)
    {
      //todo: check if correct
      Direction direction = player.getHorizontalFacing();
      
      directionInfo.setText(direction.toString());
      manager.add(directionInfo);
    }
//
//    if (Mappy.debugMode)
//    {
//      manager.add(new MapInfoLine(Alignment.Center, (locPlayer.headYaw * -1 % 360) + ""));
//    }
    
//    World world = player.world;
//    BlockPos pos = player.getPosition();
//    BiInteger cpos = MathUtil.getXZInChunk(pos);
//
//    int h = MapLayerProcessor.getHeight(world, pos, false);
//    int h2 = MapLayerProcessor.effectiveHeight((Chunk) world.getChunk(pos), cpos.A, 255, cpos.B, false);
//
//    BlockPos pos2 = new BlockPos(pos.getX(), h, pos.getZ());
//    BlockState state = world.getBlockState(pos2);
//
//
//
//    int col = ColorUtil.ABGRtoARGB(MapLayerProcessor.color(world, state, pos2));
//
//    float[] f = ColorUtil.toFloats(col);//ColorUtil.BGRAtoARGB(ColorUtil.rgb(1, 2, 3)));
//    int r = (int)(f[0] * 255);
//    int g = (int)(f[1] * 255);
//    int b = (int)(f[2] * 255);
//    String info = "Current height: " + h + ":"+ h2 + " state: " + state + " col: " + r + "," + g + "," + b;
//
////    col = ColorUtil.rgb(b, g, r);
//
//    MapInfoLine infoLine = new MapInfoLine(Alignment.Center, info);
//    infoLine.color = col;
//    manager.add(infoLine);
  }
  
  public EffectState getEffects()
  {
    return effects;
  }
  
  private String getTimeFormatted(long time)
  {
//    return time + "";
    if (time > 24000)
    {
      time %= 24000;
    }

    int m = (int) (((time % 1000) / 1000f) * 60);
    int h = (int) time / 1000 + 6;
    if (h >= 24)
    {
      h -= 24;
    }

    return ((h < 10) ? "0" + h : h) + ":" + ((m < 10) ? "0" + m : m);
  }
  
  public TriValue<BlockPos, BlockState, Integer> getDebugData()
  {
    return debugData;
  }
  
  public void generate(PlayerEntity player)
  {
    World world = player.world;
    BlockPos pos = player.getPosition();
  
    biome = world.getBiome(pos);
//    biome = world.getBiome(pos);
    DimensionType type = world.dimension.getType();
    
//    boolean nether = type == DimensionType.THE_NETHER;
//
    
    int scaled = scale * size;
//    int size = scaled;
    int startX = pos.getX() - scaled / 2;
    int startZ = pos.getZ() - scaled / 2;
    int endX = startX + scaled;
    int endZ = startZ + scaled;
    
    ChunkCache.getPreLoader(world).update(this, startX, startZ);
    
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
      
      if (p.isCrouching() || p.isSpectator())
      {
        continue;
      }
      
      BlockPos ppos = p.getPosition();
     
      int x = ppos.getX();
      int z = ppos.getZ();
      
      if (x >= startX && x <= endX && z >= startZ && z <= endZ)
      {
        MapIcon.Player playerIcon1 = new MapIcon.Player(this, p, false);
        playerIcon1.setPosition(MapIcon.getScaled(x, startX, endX, size), MapIcon.getScaled(z, startZ, endZ, size));
        this.players.add(playerIcon1);
      }
    }
    
    if (MappyConfig.showEntities)
    {
      entities.clear();
      
      int checkHeight = 24;
      int y = player.getPosition().getY();
      BlockPos start = new BlockPos(startX, y - checkHeight / 2, startZ);
      BlockPos end = new BlockPos(endX, y + checkHeight / 2, endZ);
      List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(start, end));
      
      int t = 0;
      
      for (Entity entity :
        entities)
      {
        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity))
        {
          t++;
          LivingEntity livingEntity = (LivingEntity) entity;
          MapIcon.Entity mie = new MapIcon.Entity(this, entity, livingEntity instanceof MonsterEntity);
  
          Vec3d vec = entity.getPositionVec();
          mie.setPosition(MapIcon.getScaled((int) vec.x, startX, endX, size), MapIcon.getScaled((int) vec.z, startZ, endZ, size));
          
          this.entities.add(mie);
        }
        if (t >= 250)
        {
          break;
        }
      }
      
//      System.out.println("Found " + t + " entities");
    }
    

    waypoints.clear();
    List<WayPoint> wps = WayPointManager.INSTANCE.getWaypoints(world.dimension.getType().getId());
    List<WayPoint> toRemove = new ArrayList<>();
    if (wps != null)
    {
      for (WayPoint wp :
        wps)
      {
        boolean show = false;
        if (!wp.hidden || wp.showAlways || wp.deathPoint)
        {
          if (wp.showAlways && !wp.deathPoint)
          {
            show = true;
          }
          else
          {
            double distS = MathUtil.getDistanceSqrd(pos, wp.pos);

            if (wp.deathPoint)
            {
              if (MappyConfig.autoRemoveDeathWaypoints && distS <= MappyConfig.autoRemoveRange * MappyConfig.autoRemoveRange)
              {
                // don't want to remove the waypoint we just added
                if (EventListener.alive && !(Minecraft.getInstance().currentScreen instanceof DeathScreen))
                {
                  toRemove.add(wp);
                }
              }
              else
              {
                show = true;
              }
            }
            else
            {
              if (distS <= wp.showRange * wp.showRange)
              {
                show = true;
              }
            }
          }
        }

        if (show)
        {
          MapIcon.Waypoint waypoint = new MapIcon.Waypoint(this, wp);
          waypoint.setPosition
          (
            MathUtil.clamp(MapIcon.getScaled(wp.pos.getX(), startX, endX, size), 0, size),
            MathUtil.clamp(MapIcon.getScaled(wp.pos.getZ(), startZ, endZ, size), 0, size)
          );
          waypoints.add(waypoint);
        }
      }

      if (toRemove.size() > 0)
      {
        toRemove.forEach(WayPointManager.INSTANCE::remove);
        WayPointManager.INSTANCE.save();
      }
      
//      wps.stream()
//        .filter
//          (
//            wp -> !wp.hidden && (wp.showAlways || MathUtil.getDistance(pos, wp.pos, true) <= wp.showRange)
//          )
//        .forEach(wp ->
//        {
//          MapIcon.Waypoint waypoint = new MapIcon.Waypoint(this, wp);
//          waypoint.setPosition(
//            MathUtil.clamp(MapIcon.getScaled(wp.pos.getX(), startX, endX, size), 0, size),
//            MathUtil.clamp(MapIcon.getScaled(wp.pos.getZ(), startZ, endZ, size), 0, size));
//          waypoints.add(waypoint);
//        });
    }

  }
  
//  protected boolean isAir(BlockState state)
//  {
//    return state.isAir() || state == AIR_STATE || state == CAVE_AIR_STATE || state == VOID_AIR_STATE;
//  }
  
  public List<MapIcon.Player> getPlayerIcons()
  {
    return players;
  }
  
  public List<MapIcon.Waypoint> getWaypoints()
  {
    return waypoints;
  }
  
  public void createWayPoint()
  {
    createWayPoint(false);
  }
  
  public void createWayPoint(boolean death)
  {
    PlayerEntity player = client.player;
    
    WayPoint wayPoint = new WayPoint();
    wayPoint.pos = player.getPosition();
    wayPoint.dimension = player.world.dimension.getType().getId();
    
    if (death)
    {
      wayPoint.deathPoint = true;
      wayPoint.iconType = IconType.SKULL;
      wayPoint.color = 0xff666666;
      wayPoint.name = "Death";
      
      WayPointManager.INSTANCE.add(wayPoint);
      
      
      Mappy.LOGGER.info("Created death waypoint [" + wayPoint.dimension + "] " + Util.prettyFy(wayPoint.pos));
  
      WayPointManager.INSTANCE.save();
    }
    else
    {
      wayPoint.name = "Waypoint";
      wayPoint.color = RandomUtil.getElement(WayPoint.WAYPOINT_COLORS); //ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
  
  
      client.displayGuiScreen(new WayPointEditor(wayPoint, client.currentScreen, WayPointManager.INSTANCE::add));
    }
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
  
  public List<MapIcon.Entity> getEntities()
  {
    return entities;
  }
  
  public MapInfoLineManager getManager()
  {
    return manager;
  }
  
  public void onConfigChanged(ConfigBase<?> configBase)
  {
    onConfigChanged();
  }
  
  public boolean canShowMap()
  {
    return showMap;
  }
}
