package com.naxanria.mappy.event;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.config.gui.ConfigGui;
import com.naxanria.mappy.map.MapGUI;
import com.naxanria.mappy.map.WorldMapGUI;
import com.naxanria.mappy.map.waypoint.WayPointListEditor;
import com.naxanria.mappy.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class EventListener
{
  public static final String KEY_BIND_CATEGORY = "mappy";
  public static boolean alive;
  
  
  public static void clientTick(final TickEvent.ClientTickEvent event)
  {
    if (event.phase == TickEvent.Phase.END)
    {
      if (Mappy.showMap)
      {
        Mappy.map.update();
      }
      Minecraft minecraft = Minecraft.getInstance();
      
      boolean lastAlive = alive;
      
      alive = minecraft.player != null && !(minecraft.currentScreen instanceof DeathScreen);
      
      if (lastAlive != alive)
      {
        if (minecraft.player != null)
        {
          if (lastAlive)
          {
//            Mappy.LOGGER.info("Found manual death event");
            handleDeath(minecraft.player);
          }
          else
          {
//            Mappy.LOGGER.info("Found manual respawn event");
          }
        }
      }
    }
    
    KeyHandler.INSTANCE.update();
  }
  
  
  public static void hudUpdate(final RenderGameOverlayEvent.Post event)
  {
    if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
    {
      return;
    }
    
    MapGUI gui = MapGUI.instance;
    if (gui != null)
    {
      gui.draw();
    }
  }
  
  public static void setupKeyBinds()
  {
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("hide_map", GLFW.GLFW_KEY_H, KeyConflictContext.IN_GAME))
    {
      @Override
      public void onKeyUp()
      {
        Mappy.showMap = !Mappy.showMap;
        
        MappyConfig.getConfig().showMap.set(Mappy.showMap);
        MappyConfig.getConfig().showMap.save();
      }
  
      @Override
      public boolean isListening()
      {
        return mc.currentScreen == null;
      }
    });
    
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("create_waypoint", GLFW.GLFW_KEY_B, KeyConflictContext.IN_GAME))
    {
      @Override
      public void onKeyUp()
      {
        Mappy.map.createWayPoint();
      }
  
      @Override
      public boolean isListening()
      {
        return mc.player != null && mc.currentScreen == null;
      }
    });
    
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoints_list", GLFW.GLFW_KEY_U, KeyConflictContext.IN_GAME))
    {
      @Override
      public void onKeyUp()
      {
        mc.displayGuiScreen(new WayPointListEditor(null));
      }
  
      @Override
      public boolean isListening()
      {
        return mc.player != null && mc.currentScreen == null;
      }
    });

    
    // Now handled with config button in the mod config
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("config", GLFW.GLFW_KEY_UNKNOWN, KeyConflictContext.IN_GAME))
    {
      @Override
      public void onKeyUp()
      {
        mc.displayGuiScreen(new ConfigGui(null));
      }

      @Override
      public boolean isListening()
      {
        return mc.currentScreen == null;
      }
    });
    
    
    if (MappyConfig.getConfig().enableWorldMapKey.get())
    {
      KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("map", GLFW.GLFW_KEY_M, KeyConflictContext.IN_GAME))
      {
        @Override
        public void onKeyUp()
        {
          mc.displayGuiScreen(new WorldMapGUI(null));
        }
    
        @Override
        public boolean isListening()
        {
          return mc.currentScreen == null;
        }
      });
    }
    
  }
  
  private static KeyBinding createKeyBinding(String name, int key, KeyConflictContext conflictContext)
  {
    return new KeyBinding(name, conflictContext, InputMappings.Type.KEYSYM, key, KEY_BIND_CATEGORY);
  }
  
  private static void handleDeath(PlayerEntity player)
  {
    
    if (player.getUniqueID() != Minecraft.getInstance().player.getUniqueID())
    {
      return;
    }
  
    MappyConfig.Client config = MappyConfig.getConfig();
  
    if (config.printDeathPointInChat.get())
    {
      player.sendMessage(new StringTextComponent("You died at " + Util.prettyFy(player.getPosition())));
    }
  
    if (config.createDeathWayPoints.get())
    {
      Mappy.map.createWayPoint(true);
    }
  
//    playerAlive = false;
  }
  
//  @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
//  public static void entityDeath(final LivingDeathEvent event)
//  {
//    Mappy.LOGGER.info("Detected death - " + event.getEntity().getClass().getCanonicalName());
//    if (event.getEntity() instanceof PlayerEntity)
//    {
//      Mappy.LOGGER.info("Detected Player death");
//      PlayerEntity player = (PlayerEntity) event.getEntity();
////      if (player.getUniqueID() != Minecraft.getInstance().player.getUniqueID())
////      {
////        return;
////      }
////
////      if (MappyConfig.printDeathPointInChat)
////      {
////        player.sendMessage(new StringTextComponent("You died at " + Util.prettyFy(player.getPosition())));
////      }
////
////      if (MappyConfig.createDeathWaypoints)
////      {
////        Mappy.map.createWayPoint(true);
////      }
////
////      playerAlive = false;
//    }
//  }
//
//  @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
//  public static void playerRespawn(final PlayerEvent.PlayerRespawnEvent event)
//  {
//    PlayerEntity player = event.getPlayer();
//
//    if (player.getUniqueID() != Minecraft.getInstance().player.getUniqueID())
//    {
//      return;
//    }
//
//    Mappy.LOGGER.info("Respawned");
//
//    playerAlive = true;
//  }
}
