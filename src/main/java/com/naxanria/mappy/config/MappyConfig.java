package com.naxanria.mappy.config;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.client.DrawPosition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class MappyConfig
{
  public static int offset = 4;
  public static DrawPosition drawPosition = DrawPosition.TOP_RIGHT;
  public static int mapSize = 64;
  
  public static boolean createDeathWaypoints = true;
  public static boolean autoRemoveDeathWaypoints = true;
  public static int autoRemoveRange = 5;
  public static boolean printDeathPointInChat = false;
  
  public static boolean showPosition = true;
  public static boolean showFPS = false;
  public static boolean showBiome = true;
  public static boolean showTime = true;
  public static boolean showDirection = false;
  
  public static boolean showPlayerNames = true;
  public static boolean showPlayerHeads = true;
  public static boolean showEntities = true;
  
  public static int updatePerCycle = 4;
  public static int pruneDelay = 60;
  public static int pruneAmount = 1500;
  public static boolean forceHeightmap = true;
  
  public static boolean showMap = true;
  
  public static boolean moveMapForEffects = true;
  
  public static boolean shaded = true;
  public static int maxDifference = 10;
  public static boolean drawChunkGrid = false;
  public static int scale = 1;
  public static boolean showInChat = true;
  
  public static boolean inHotBar = false;
  public static String mapItem = "";
  public static String positionItem = "";
  public static String timeItem = "";
  public static boolean showItemConfigInGame = false;
  public static String biomeItem = "";
  
  public static Client config;
  private static ForgeConfigSpec spec;
  
  static
  {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    spec = specPair.getRight();
    config = specPair.getLeft();
  }
  
  private static String key(String key)
  {
    return "mappy.config." + key;
  }
  
  public static void register(final ModLoadingContext context)
  {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(MappyConfig::reload);
    
    context.registerConfig(ModConfig.Type.CLIENT, spec);
  }
  
  public static void reload(ModConfig.ModConfigEvent event)
  {
    ModConfig config = event.getConfig();
    
    if (config.getSpec() == spec)
    {
      reloadClient();
    }
  }
  
  private static void reloadClient()
  {
    offset = config.offset.get();
    drawPosition = config.drawPosition.get();
    mapSize = config.mapSize.get();
  
    createDeathWaypoints = config.createDeathWayPoints.get();
    autoRemoveDeathWaypoints = config.autoRemoveDeathWaypoint.get();
    autoRemoveRange = config.autoRemoveRange.get();
    printDeathPointInChat = config.printDeathPointInChat.get();
  
    showPosition = config.showPosition.get();
    showFPS = config.showFPS.get();
    showBiome = config.showBiome.get();
    showTime = config.showTime.get();
    showDirection = config.showDirection.get();
  
    showPlayerNames = config.showPlayerNames.get();
    showPlayerHeads = config.showPlayerHeads.get();
    showEntities = config.showEntities.get();
  
    updatePerCycle = config.updatePerCycle.get();
    pruneDelay = config.pruneDelay.get();
    pruneAmount = config.pruneAmount.get();
    forceHeightmap = config.forceHeightmapUse.get();
    
    showMap = config.showMap.get();
    moveMapForEffects = config.moveMapForEffects.get();
  
    shaded = config.shaded.get();
    // todo: see if this needs inverting to make more sense
    maxDifference = config.shadeStrength.get();
    drawChunkGrid = config.drawChunkGrid.get();
    // todo: implement scale
    scale = config.scale.get();
    showInChat = config.showInChat.get();
  
    showItemConfigInGame = config.showItemConfigInGame.get();
    inHotBar = config.inHotBar.get();
    mapItem = config.mapItem.get();
    positionItem = config.positionItem.get();
    timeItem = config.timeItem.get();
    biomeItem = config.biomeItem.get();
  
    Mappy.map.onConfigChanged();
  }
  
  private static class Client
  {
    private final ForgeConfigSpec.IntValue offset;
    private final ForgeConfigSpec.EnumValue<DrawPosition> drawPosition;
    private final ForgeConfigSpec.IntValue mapSize;
  
    private final ForgeConfigSpec.BooleanValue createDeathWayPoints;
    private final ForgeConfigSpec.BooleanValue printDeathPointInChat;
    private final ForgeConfigSpec.BooleanValue autoRemoveDeathWaypoint;
    private final ForgeConfigSpec.IntValue autoRemoveRange;
  
    private final ForgeConfigSpec.BooleanValue showPosition;
    private final ForgeConfigSpec.BooleanValue showFPS;
    private final ForgeConfigSpec.BooleanValue showBiome;
    private final ForgeConfigSpec.BooleanValue showTime;
    private final ForgeConfigSpec.BooleanValue showDirection;
  
    private final ForgeConfigSpec.BooleanValue showPlayerNames;
    private final ForgeConfigSpec.BooleanValue showPlayerHeads;
    private final ForgeConfigSpec.BooleanValue showEntities;
  
    private final ForgeConfigSpec.IntValue updatePerCycle;
    private final ForgeConfigSpec.IntValue pruneDelay;
    private final ForgeConfigSpec.IntValue pruneAmount;
    private final ForgeConfigSpec.BooleanValue forceHeightmapUse;
  
    public final ForgeConfigSpec.BooleanValue showMap;
  
    private final ForgeConfigSpec.BooleanValue moveMapForEffects;
  
    private final ForgeConfigSpec.BooleanValue shaded;
    private final ForgeConfigSpec.IntValue shadeStrength;
    private final ForgeConfigSpec.BooleanValue drawChunkGrid;
    private final ForgeConfigSpec.IntValue scale;
    private final ForgeConfigSpec.BooleanValue showInChat;
  
    private final ForgeConfigSpec.BooleanValue inHotBar;
    private final ForgeConfigSpec.ConfigValue<String> mapItem;
    private final ForgeConfigSpec.ConfigValue<String> positionItem;
    private final ForgeConfigSpec.ConfigValue<String> timeItem;
    private final ForgeConfigSpec.BooleanValue showItemConfigInGame;
    private final ForgeConfigSpec.ConfigValue<String> biomeItem;
  
    public Client(final ForgeConfigSpec.Builder builder)
    {
      builder.comment("Mappy settings");
      builder.push("general");
      
      offset = builder
        .comment("Offset from screen edge")
        .translation(key("offset"))
        .defineInRange("offset", 4, 0, 50);
        
      drawPosition = builder
        .comment("Draw position of the map")
        .translation(key("draw_postion"))
        .defineEnum("draw_position", DrawPosition.TOP_RIGHT);
      
      mapSize = builder
        .comment("The map size")
        .translation(key("map_size"))
        .defineInRange("map_size", 64, 16, 256);
      
      builder.comment("Death waypoints for when you die.").push("death");
  
      createDeathWayPoints = builder
        .comment("Create a waypoint when you die")
        .translation(key("death_waypoint"))
        .define("death_waypoint", true);
      
      autoRemoveDeathWaypoint = builder
        .comment("Auto remove the death point when you get close")
        .translation(key("death_auto_remove"))
        .define("death_auto_remove", true);
      
      autoRemoveRange = builder
        .comment("Range for when to remove the death waypoint")
        .translation(key("death_remove_range"))
        .defineInRange("death_remove_range", 5, 1, 40);
      
      printDeathPointInChat = builder
        .comment("Print the death position into your chat.")
        .translation(key("death_print"))
        .define("death_print", false);
      
      builder.pop();
      
      shaded = builder
        .comment("Show the map shaded")
        .translation(key("shaded"))
        .define("shaded", true);
        
      shadeStrength = builder
//        .comment("The strength for the shading")
        .comment("Lower numbers are stronger")
        .translation(key("shade_strength"))
        .defineInRange("shade_strength", 10, 2, 16);
      
      drawChunkGrid = builder
        .comment("Show the chunk grid")
        .translation(key("draw_grid"))
        .define("draw_grid", false);
      
      scale = builder
        .comment("The scale of the map - WIP")
//        .comment("WIP")
        .translation("scale")
        .defineInRange("scale", 1, 1, 8);
      
      showInChat = builder
        .comment("Show map while chat is open")
        .translation(key("show_in_chat"))
        .define("show_in_chat", true);
      
      builder.pop().comment("The info to show").push("info");
      
      showPosition = builder
        .comment("Show the position in the info")
        .translation(key("show_position"))
        .define("show_position", true);
      
      showFPS = builder
        .comment("Show fps in the info")
        .translation(key("show_fps"))
        .define("show_fps", false);
  
      showBiome = builder
        .comment("Show biome name in info")
        .translation(key("show_biome"))
        .define("show_biome", true);
      
      showTime = builder
        .comment("Show in game time in info")
        .translation(key("show_time"))
        .define("show_time", true);
      
      showDirection = builder
        .comment("Show direction currently facing in info")
        .translation(key("show_direction"))
        .define("show_direction", false);
  
      showPlayerNames = builder
        .comment("Show player names on the map")
        .translation(key("show_player_names"))
        .define("show_player_names", true);
      
      showPlayerHeads = builder
        .comment("Show player heads on the map")
        .translation(key("show_player_heads"))
        .define("show_player_heads", true);
      
      showEntities = builder
        .comment("Show entities on the map")
        .translation(key("show_entities"))
        .define("show_entities", true);
      
      moveMapForEffects = builder
        .comment("Moves map to show effects")
        .translation(key("move_map_for_effects"))
        .define("move_map_for_effects", true);
  
      builder.pop().comment("These options are for performance").push("performance");
      
      updatePerCycle = builder
        .comment("Updated chunks per cycle")
        .translation(key("update_per_cycle"))
        .defineInRange("update_per_cycle", 10, 1, 100);
      
      pruneDelay = builder
        .comment("Delay before pruning old not visited chunks")
        .translation(key("prune_delay"))
        .defineInRange("prune_delay", 60, 20, 600);
      
      pruneAmount = builder
        .comment("Max amount to purge per prune cycle")
        .translation(key("prune_amount"))
        .defineInRange("prune_amount", 1500, 100, 6000);
      
      forceHeightmapUse = builder
        .comment("Forces use of heightmap for height checking, this is more performing but can be less accurate")
        .translation(key("force_heightmap"))
        .define("force_heightmap", true);
  
      builder.pop().comment("Items required for showing").push("items");
  
      showItemConfigInGame = builder
        .comment("Showing this part of the config in game - wip")
        .define("show_item_config_in_game", false);
      
      inHotBar = builder
        .comment("Does the item need to be in the hotbar?")
        .translation(key("in_hot_bar"))
        .define("in_hot_bar", false);
      
      builder.comment("An empty value means an item is not required");
      
      mapItem = builder
        .comment("The item required to show the map.")
        .translation(key("map_item"))
        .define("map_item", "");
      
      positionItem = builder
        .comment("The item required to show position.")
        .translation(key("position_item"))
        .define("position_item", "");
      
      timeItem = builder
        .comment("The item required to see the time")
        .translation(key("time_item"))
        .define("time_item", "");
      
      biomeItem = builder
        .comment("The item required to see current biome")
        .translation(key("biome_item"))
        .define("biome_item", "");
      
      builder.pop().comment("Some internally used config options").push("hidden");
      
      showMap = builder
        .comment("Showing the map or not")
        .define("show_map", true);
    
      builder.pop();
    }
  }
}
