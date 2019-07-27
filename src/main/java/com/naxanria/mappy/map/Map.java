package com.naxanria.mappy.map;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.client.Alignment;
import com.naxanria.mappy.config.ConfigBase;
import com.naxanria.mappy.config.Settings;
import com.naxanria.mappy.map.chunk.ChunkCache;
import com.naxanria.mappy.map.waypoint.WayPoint;
import com.naxanria.mappy.map.waypoint.WayPointEditor;
import com.naxanria.mappy.map.waypoint.WayPointManager;
import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.RandomUtil;
import com.naxanria.mappy.util.StackUtil;
import com.naxanria.mappy.util.TriValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
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
  
  private static final MinecraftClient client = MinecraftClient.getInstance();
  
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
    image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
    
    manager = new MapInfoLineManager(this);
  }
  
  public void update()
  {
    if (!Mappy.showMap)
    {
      return;
    }
    
    PlayerEntity player = client.player;
    if (player != null)
    {
      if (playerIcon == null)
      {
        playerIcon = new MapIcon.Player(this, player, true);
      }
      
      if (locPlayer == null)
      {
        WayPointManager.INSTANCE.load();
        locPlayer = player;
      }
      
      itemCheck();
      
      playerIcon.setPosition(size / 2, size / 2);
      
      generate(player);
  
      updateInfo(player);
      
      updateStatusEffects();

      MapGUI.instance.markDirty();
    }
    else
    {
      locPlayer = null;
    }
  }
  
  private void itemCheck()
  {
    boolean inHotBar = Settings.inHotBar;
    showMap = Settings.mapItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, Settings.mapItem);
    showPosition = Settings.showPosition && (Settings.positionItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, Settings.positionItem));
    showTime = Settings.showTime && (Settings.timeItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, Settings.timeItem));
    showBiome = Settings.showBiome && (Settings.biomeItem.equals("") || StackUtil.contains(locPlayer.inventory, inHotBar, Settings.biomeItem));
  }
  
  private void updateStatusEffects()
  {
    /*
    * Based on code by ThexXTURBOXx in pull request #5
    * */
    Collection<StatusEffectInstance> statusEffects = locPlayer.getStatusEffects();
    if (statusEffects.size() > 0)
    {
      effects = EffectState.BENEFICIAL;
      boolean showing = false;
      for (StatusEffectInstance e :
        statusEffects)
      {
        showing |= e.shouldShowIcon();
        if (!e.getEffectType().method_5573())
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
    image = new NativeImage(NativeImage.Format.RGBA, newSize, newSize, false);
    System.out.println("Map resized to " + newSize + "x" + newSize);
  }
  
  public void onConfigChanged()
  {
    int configScale = Settings.scale;
    int configSize = Settings.mapSize;
    
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
      BlockPos playerPos = client.player.getBlockPos();
      playerPositionInfo.setText(playerPos.getX() + " " + playerPos.getY() + " " + playerPos.getZ());
      manager.add(playerPositionInfo);
    }
    
    if (showBiome)
    {
      biomeInfo.setText(I18n.translate(biome.getTranslationKey()));
      manager.add(biomeInfo);
    }
    
    if (Settings.showFPS)
    {
      fpsInfo.setText(MinecraftClient.getCurrentFps() + " fps");
      manager.add(fpsInfo);
    }
    
    if (showTime)
    {
      inGameTimeInfo.setText(getTimeFormatted(client.world.getTimeOfDay()));
      manager.add(inGameTimeInfo);
    }
    
    if (Settings.showDirection)
    {
      Direction direction = player.getMovementDirection();
      
      directionInfo.setText(direction.asString());
      manager.add(directionInfo);
    }
    
    if (Mappy.debugMode)
    {
      manager.add(new MapInfoLine(Alignment.Center, (locPlayer.headYaw * -1 % 360) + ""));
    }
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
    BlockPos pos = player.getBlockPos();
  
    biome = world.getBiome(pos);
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
      }
    }
    
    if (Settings.showEntities)
    {
      entities.clear();
      
      int checkHeight = 24;
      BlockPos start = new BlockPos(startX, player.y - checkHeight / 2, startZ);
      BlockPos end = new BlockPos(endX, player.y + checkHeight / 2, endZ);
      List<Entity> entities = world.getEntities((Entity) null, new BoundingBox(start, end));
  
      int t = 0;
      
      for (Entity entity :
        entities)
      {
        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity))
        {
          t++;
          LivingEntity livingEntity = (LivingEntity) entity;
          MapIcon.Entity mie = new MapIcon.Entity(this, entity, livingEntity instanceof HostileEntity);
          
          mie.setPosition(MapIcon.getScaled((int) entity.x, startX, endX, size), MapIcon.getScaled((int) entity.z, startZ, endZ, size));
          
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
    List<WayPoint> wps = WayPointManager.INSTANCE.getWaypoints(world.dimension.getType().getRawId());
    if (wps != null)
    {
      wps.stream()
        .filter
          (
            wp -> !wp.hidden && (wp.showAlways || MathUtil.getDistance(pos, wp.pos, true) <= wp.showRange)
          )
        .forEach(wp ->
        {
          MapIcon.Waypoint waypoint = new MapIcon.Waypoint(this, wp);
          waypoint.setPosition(
            MathUtil.clamp(MapIcon.getScaled(wp.pos.getX(), startX, endX, size), 0, size),
            MathUtil.clamp(MapIcon.getScaled(wp.pos.getZ(), startZ, endZ, size), 0, size));
          waypoints.add(waypoint);
        });
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
  
  public List<MapIcon.Waypoint> getWaypoints()
  {
    return waypoints;
  }
  
  public void createWayPoint()
  {
    PlayerEntity player = client.player;
    
    WayPoint wayPoint = new WayPoint();
    wayPoint.dimension = player.world.dimension.getType().getRawId();
//    Random random = player.world.random;
    wayPoint.name = "Waypoint";
    wayPoint.color = RandomUtil.getElement(WayPoint.WAYPOINT_COLORS); //ColorUtil.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    wayPoint.pos = player.getBlockPos();
    
    client.openScreen(new WayPointEditor(wayPoint, client.currentScreen, WayPointManager.INSTANCE::add));
    
//    WayPointManager.INSTANCE.add(wayPoint);
//    WayPointManager.INSTANCE.save();
//
//    player.sendMessage(new StringTextComponent("Created waypoint " + wayPoint.pos.getX() + " " + wayPoint.pos.getY() + " " + wayPoint.pos.getZ()));
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
