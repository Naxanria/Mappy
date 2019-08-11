package com.naxanria.mappy.event;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.gui.ConfigGui;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.MapGUI;
import com.naxanria.mappy.map.waypoint.WayPointListEditor;
import com.naxanria.mappy.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Mappy.MODID)
public class EventListener
{
  public static final String KEY_BIND_CATEGORY = "mappy";
  
  @SubscribeEvent
  public static void clientTick(final TickEvent.ClientTickEvent event)
  {
    if (event.phase == TickEvent.Phase.END)
    {
      if (Mappy.showMap)
      {
        Mappy.map.update();
      }
    }
    KeyHandler.INSTANCE.update();
  }
  
  @SubscribeEvent
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
        // todo: save in config
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
    
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("config", GLFW.GLFW_KEY_KP_6, KeyConflictContext.IN_GAME))
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
  }
  
  private static KeyBinding createKeyBinding(String name, int key, KeyConflictContext conflictContext)
  {
    return new KeyBinding(name, conflictContext, InputMappings.Type.KEYSYM, key, KEY_BIND_CATEGORY);
  }
  
  public static boolean playerAlive = true;
  
  @SubscribeEvent
  public static void entityDeath(final LivingDeathEvent event)
  {
    if (event.getEntity() instanceof PlayerEntity)
    {
      Mappy.LOGGER.info("Detected death");
      PlayerEntity player = (PlayerEntity) event.getEntity();
      if (player.getUniqueID() != Minecraft.getInstance().player.getUniqueID())
      {
        return;
      }
      
      if (MappyConfig.printDeathPointInChat)
      {
        player.sendMessage(new StringTextComponent("You died at " + Util.prettyFy(player.getPosition())));
      }
      
      if (MappyConfig.createDeathWaypoints)
      {
        Mappy.map.createWayPoint(true);
      }
      
      playerAlive = false;
    }
  }
  
  @SubscribeEvent
  public static void playerRespawn(final PlayerEvent.PlayerRespawnEvent event)
  {
    PlayerEntity player = event.getPlayer();
  
    if (player.getUniqueID() != Minecraft.getInstance().player.getUniqueID())
    {
      return;
    }
    
    Mappy.LOGGER.info("Respawned");
    
    playerAlive = true;
  }
}
