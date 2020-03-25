package com.naxanria.mappy.config;

import com.naxanria.mappy.gui.DrawPosition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class MappyConfig
{
  
  private static Client config;
  private static ForgeConfigSpec spec;
  
  static
  {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    spec = specPair.getRight();
    config = specPair.getLeft();
  }
  
  public static Client getConfig()
  {
    return config;
  }
  
  private static String key(String key)
  {
    return "mappy.config." + key;
  }
  
  public static void register(final ModLoadingContext context)
  {
    context.registerConfig(ModConfig.Type.CLIENT, spec);
  }
  
  public static ForgeConfigSpec getSpec()
  {
    return spec;
  }
  
  public static class Client
  {
    public final ForgeConfigSpec.IntValue offset;
    public final ForgeConfigSpec.EnumValue<DrawPosition> drawPosition;
    public final ForgeConfigSpec.IntValue mapSize;
    
    public final ForgeConfigSpec.BooleanValue teleportButton;
    public final ForgeConfigSpec.BooleanValue chatButton;
  
    public final ForgeConfigSpec.BooleanValue createDeathWayPoints;
    public final ForgeConfigSpec.BooleanValue printDeathPointInChat;
    public final ForgeConfigSpec.BooleanValue autoRemoveDeathWaypoint;
    public final ForgeConfigSpec.IntValue autoRemoveRange;
  
    public final ForgeConfigSpec.BooleanValue showPosition;
    public final ForgeConfigSpec.BooleanValue showFPS;
    public final ForgeConfigSpec.BooleanValue showBiome;
    public final ForgeConfigSpec.BooleanValue showTime;
    public final ForgeConfigSpec.BooleanValue showDirection;
    public final ForgeConfigSpec.BooleanValue biomeBlending;
  
    public final ForgeConfigSpec.BooleanValue showPlayerNames;
    public final ForgeConfigSpec.BooleanValue showPlayerHeads;
    public final ForgeConfigSpec.BooleanValue showEntities;
  
    public final ForgeConfigSpec.IntValue updatePerCycle;
//    public final ForgeConfigSpec.IntValue pruneDelay;
//    public final ForgeConfigSpec.IntValue pruneAmount;
    public final ForgeConfigSpec.BooleanValue forceHeightmapUse;
  
    public final ForgeConfigSpec.BooleanValue showMap;
  
    public final ForgeConfigSpec.BooleanValue moveMapForEffects;
  
    public final ForgeConfigSpec.BooleanValue shaded;
    public final ForgeConfigSpec.IntValue shadeStrength;
    public final ForgeConfigSpec.BooleanValue drawChunkGrid;
    public final ForgeConfigSpec.IntValue scale;
    public final ForgeConfigSpec.BooleanValue showInChat;
  
    public final ForgeConfigSpec.BooleanValue inHotBar;
    public final ForgeConfigSpec.ConfigValue<String> mapItem;
    public final ForgeConfigSpec.ConfigValue<String> positionItem;
    public final ForgeConfigSpec.ConfigValue<String> timeItem;
    public final ForgeConfigSpec.BooleanValue showItemConfigInGame;
    public final ForgeConfigSpec.ConfigValue<String> biomeItem;
    
    public final ForgeConfigSpec.BooleanValue enableWorldMapKey;
  
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
        .translation(key("draw_position"))
        .defineEnum("draw_position", DrawPosition.TOP_RIGHT);
      
      mapSize = builder
        .comment("The map size")
        .translation(key("map_size"))
        .defineInRange("map_size", 128, 16, 256);
      
      builder.comment("Extra waypoint buttons").push("waypoints");
      
      teleportButton = builder
        .comment("Show the teleport button")
        .translation(key("teleport_button"))
        .define("teleport_button", false);
      
      chatButton = builder
        .comment("Show chat button")
        .translation(key("chat_button"))
        .define("chat_button", true);
      
      builder.pop();
      
      builder.comment("Death waypoints for when you die.").push("death");
  
      createDeathWayPoints = builder
        .comment("Create a waypoint when you die")
        .translation(key("death_waypoint"))
        .define("waypoint", true);
      
      autoRemoveDeathWaypoint = builder
        .comment("Auto remove the death point when you get close")
        .translation(key("death_auto_remove"))
        .define("auto_remove", true);
      
      autoRemoveRange = builder
        .comment("Range for when to remove the death waypoint")
        .translation(key("death_remove_range"))
        .defineInRange("remove_range", 5, 1, 40);
      
      printDeathPointInChat = builder
        .comment("Print the death position into your chat.")
        .translation(key("death_print"))
        .define("print", false);
      
      builder.pop();
      
      shaded = builder
        .comment("Show the map shaded")
        .translation(key("shaded"))
        .define("shaded", true);
      
      biomeBlending = builder
        .comment("Blending colors based on biome (e.g: Grass)")
        .translation(key("biome_blending"))
        .define("biomeBlend", true);
        
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
      
//      pruneDelay = builder
//        .comment("Delay before pruning old not visited chunks")
//        .translation(key("prune_delay"))
//        .defineInRange("prune_delay", 60, 20, 600);
//
//      pruneAmount = builder
//        .comment("Max amount to purge per prune cycle")
//        .translation(key("prune_amount"))
//        .defineInRange("prune_amount", 1500, 100, 6000);
      
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
      
      enableWorldMapKey = builder.push("Extra settings")
        .comment("Enable the show world map key, needs restart")
        .translation(key("enable_world_map_key"))
        .define("enable_world_map_key", false);
    }
  }
}
