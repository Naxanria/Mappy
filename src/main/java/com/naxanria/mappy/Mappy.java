package com.naxanria.mappy;

import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.config.gui.ConfigGui;
import com.naxanria.mappy.event.EventListener;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

import java.io.File;

@Mod(Mappy.MODID)
public class Mappy
{
  public static final String MODID = "mappy";
//  public static final Logger LOGGER = new Logger("[" +MODID + "]");
  public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MODID);
  
  public static Map map = new Map();
  private File output;
  
  public static boolean debugMode = false;
  public static boolean showMap = true;
  
//  public static Path configFolder;
  
  public Mappy()
  {
    DistExecutor.runWhenOn(Dist.CLIENT,
    () -> () ->
      {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setupClient);
        
        
        MapGUI mapGUI = new MapGUI(map, 4, DrawPosition.TOP_RIGHT);
  
        MappyConfig.register(ModLoadingContext.get());
        
        ModLoadingContext ctx = ModLoadingContext.get();

        ctx.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> this::openConfigScreen);
      }
    );
  }
  
  private Screen openConfigScreen(Minecraft minecraft, Screen parent)
  {
    return new ConfigGui(parent);
  }
  
  private void setupClient(final FMLClientSetupEvent event)
  {
    EventListener.setupKeyBinds();
  }
}
