package com.naxanria.mappy;

import com.naxanria.mappy.client.*;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapGUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
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
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    
    Config config = new Config(configFile);
    
    FabricKeyBinding reload = FabricKeyBinding.Builder.create
    (
      new Identifier(MODID, "reload"),
      InputUtil.Type.KEYSYM,
      GLFW.GLFW_KEY_G,
      MODID
    ).build();
  
    KeyHandler.INSTANCE.register(new KeyParser(reload)
    {
      @Override
      public void onKeyUp()
      {
        config.load();
        mc.player.sendMessage(new StringTextComponent("Reloaded mappy config").setStyle(new Style().setColor(TextFormat.AQUA)));
      }
  
      @Override
      public boolean isListening()
      {
        return mc.currentScreen == null && mc.player != null;
      }
    });
    
    
    FabricKeyBinding debug = FabricKeyBinding.Builder.create
    (
      new Identifier(MODID, "debug"),
      InputUtil.Type.KEYSYM,
      GLFW.GLFW_KEY_F12,
      MODID
    ).build();
    
    KeyHandler.INSTANCE.register(new KeyParser(debug)
    {
      @Override
      public void onKeyUp()
      {
        debugMode = !debugMode;
        mc.player.sendMessage(new StringTextComponent("Mappy debug mode " + (debugMode ? "enabled" : "disabled")));
      }
  
      @Override
      public boolean isListening()
      {
        return mc.player != null;
      }
    });
    
//    FabricKeyBinding typeCycle = FabricKeyBinding.Builder.create
//    (
//      new Identifier(MODID, "cycle_type"),
//      InputUtil.Type.KEYSYM,
//      GLFW.GLFW_KEY_BACKSPACE,
//      MODID
//    ).build();
//
//    KeyHandler.INSTANCE.register(new KeyParser(typeCycle)
//    {
//      @Override
//      public void onKeyUp()
//      {
//        Heightmap.Type type = map.nextType();
//        mc.player.sendMessage(new StringTextComponent("Heightmap type changed to " + type.getName()));
//      }
//
//      @Override
//      public boolean isListening()
//      {
//        return mc.currentScreen == null && mc.player != null;
//      }
//    });
//    KeyHandler.INSTANCE.register(new KeyParser(generate)
//    {
//      @Override
//      public void onKeyUp()
//      {
//        mc.player.sendMessage(new StringTextComponent("Generating image!"));
//        map.generate(mc.player);
//
//        try
//        {
//          if (!output.exists())
//          {
//            output.createNewFile();
//          }
//
//          ImageIO.write(map.getImage(), "png", output);
//          mc.player.sendMessage(new StringTextComponent("Generated and saved to " + output.getAbsolutePath()));
//        }
//        catch (IOException e)
//        {
//          e.printStackTrace();
//        }
//      }
//
//      @Override
//      public void onKeyDown()
//      {
//
//      }
//
//      @Override
//      public boolean isListening()
//      {
//        return  (mc.currentScreen == null && mc.player != null);
//      }
//    });
  
    ClientTickCallback.EVENT.register(ClientHandler::tick);
  
    MapGUI mapGUI = new MapGUI(map, 4, DrawPosition.TOP_RIGHT);
  }
}
