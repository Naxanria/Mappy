package com.naxanria.mappy;

import com.naxanria.mappy.event.EventListener;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapGUI;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;
import java.nio.file.Path;

@Mod(Mappy.MODID)
public class Mappy
{
  public static final String MODID = "mappy";
  public static final Logger LOGGER = new Logger("[" + MODID + "]");
  
  public static Map map = new Map();
  private File output;
  
  public static boolean debugMode = false;
  public static boolean showMap = true;
  
  public static Path configFolder;
  
  public Mappy()
  {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::setupClient);
  
//    configFolder = FMLPaths.CONFIGDIR.get().resolve(MODID + "/");
//    File configFolderFile = configFolder.toFile();
//    if (!configFolderFile.exists())
//    {
//      if (configFolderFile.mkdir())
//      {
//        LOGGER.info("Created config folder");
//      }
//      else
//      {
//        LOGGER.warn("failed to create config folder");
//      }
//    }
  
    MapGUI mapGUI = new MapGUI(map, 4, DrawPosition.TOP_RIGHT);
  
    MappyConfig.register(ModLoadingContext.get());
    
  }
  
  private void setupClient(final FMLClientSetupEvent event)
  {
    EventListener.setupKeyBinds();
  }
  
  //  @Override
//  public void onInitializeClient()
//  {
//    output = new File(FabricLoader.getInstance().getGameDirectory(), "/map/image.png");
//    output.getParentFile().mkdirs();
//
//    KeyBindingRegistry.INSTANCE.addCategory(MODID);
//
//    File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), MODID + "/" + MODID + ".cfg");
//    configFile.getParentFile().mkdirs();
//    if (!configFile.exists())
//    {
//      try
//      {
//        configFile.createNewFile();
//      } catch (IOException e)
//      {
//        e.printStackTrace();
//      }
//    }
//
//    Config.registerListener(map::onConfigChanged);
//
//    Config config = new Config(configFile);
//
//    showMap = config.getShowMap();
//
//    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoint_create", GLFW.GLFW_KEY_B))
//    {
//      @Override
//      public void onKeyUp()
//      {
//        map.createWayPoint();
//      }
//
//      @Override
//      public boolean isListening()
//      {
//        return mc.player != null && mc.currentScreen == null;
//      }
//    });
//
//    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("hide_map", GLFW.GLFW_KEY_H))
//    {
//      @Override
//      public void onKeyUp()
//      {
//        boolean show = !showMap;
//        showMap = show;
//        config.setShowMap(show);
//      }
//    });
//
//    KeyHandler.INSTANCE.register(new KeyParser(createKeyBinding("waypoints_list", GLFW.GLFW_KEY_U))
//    {
//      @Override
//      public void onKeyUp()
//      {
//        MinecraftClient.getInstance().openScreen(new WayPointListEditor(null));
//      }
//
//      @Override
//      public boolean isListening()
//      {
//        return mc.player != null && mc.currentScreen == null;
//      }
//    });
//
//    ClientTickCallback.EVENT.register(EventListener::tick);
//
//    MapGUI mapGUI = new MapGUI(map, 4, DrawPosition.TOP_RIGHT);
//  }
//
//  private FabricKeyBinding createKeyBinding(String name, int key)
//  {
//    return FabricKeyBinding.Builder.create(new Identifier(MODID, name), InputUtil.Type.KEYSYM, key, MODID).build();
//  }
}
