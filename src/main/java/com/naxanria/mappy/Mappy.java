package com.naxanria.mappy;

import com.naxanria.mappy.client.ClientHandler;
import com.naxanria.mappy.client.DrawPosition;
import com.naxanria.mappy.client.KeyHandler;
import com.naxanria.mappy.client.KeyParser;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapGUI;
import com.naxanria.mappy.map.waypoint.WayPointListEditor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;

public class Mappy implements ClientModInitializer
{
  public static final String MODID = "mappy";
  
  public static Map map = new Map();
  private File output;
  
  public static boolean debugMode = false;
  public static boolean showMap = true;
  
  @Override
  public void onInitializeClient()
  {
    output = new File(FabricLoader.getInstance().getGameDirectory(), "/map/image.png");
    output.getParentFile().mkdirs();
  
    KeyBindingRegistry.INSTANCE.addCategory(MODID);
  
    File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), MODID + "/" + MODID + ".cfg");
    configFile.getParentFile().mkdirs();
    if (!configFile.exists())
    {
      try
      {
        configFile.createNewFile();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  
    Config.registerListener(map::onConfigChanged);
    
    Config config = new Config(configFile);
    
    showMap = config.getShowMap();
  
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoint_create", GLFW.GLFW_KEY_B))
    {
      @Override
      public void onKeyUp()
      {
        map.createWayPoint();
      }

      @Override
      public boolean isListening()
      {
        return mc.player != null && mc.currentScreen == null;
      }
    });


    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoint_delete", GLFW.GLFW_KEY_BACKSPACE))
    {
      @Override
      public void onKeyUp()
      {
        map.removeWayPoint();
      }
  
      @Override
      public boolean isListening()
      {
        return mc.player != null && mc.currentScreen == null;
      }
    });
    
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("hide_map", GLFW.GLFW_KEY_H))
    {
      @Override
      public void onKeyUp()
      {
        boolean show = !showMap;
        showMap = show;
        config.setShowMap(show);
      }
    });
    
    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoints_list", GLFW.GLFW_KEY_U))
    {
      @Override
      public void onKeyUp()
      {
        MinecraftClient.getInstance().openScreen(new WayPointListEditor(null));
      }
  
      @Override
      public boolean isListening()
      {
        return mc.player != null && mc.currentScreen == null;
      }
    });
  
    ClientTickCallback.EVENT.register(ClientHandler::tick);
  
    MapGUI mapGUI = new MapGUI(map, 4, DrawPosition.TOP_RIGHT);
  }
  
  private FabricKeyBinding createKeyBinding(String name, int key)
  {
    return FabricKeyBinding.Builder.create(new Identifier(MODID, name), InputUtil.Type.KEYSYM, key, MODID).build();
  }
}
