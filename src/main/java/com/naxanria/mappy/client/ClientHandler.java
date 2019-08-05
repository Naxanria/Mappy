package com.naxanria.mappy.client;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.map.MapGUI;
import com.naxanria.mappy.map.waypoint.WayPointListEditor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Mappy.MODID)
public class ClientHandler
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
  }
  
  private static KeyBinding createKeyBinding(String name, int key, KeyConflictContext conflictContext)
  {
    return new KeyBinding(name, conflictContext, InputMappings.Type.KEYSYM, key, KEY_BIND_CATEGORY);
  }
}
