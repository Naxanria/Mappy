package com.naxanria.mappy;

import com.naxanria.mappy.config.MappyConfig;
import com.naxanria.mappy.config.gui.ConfigGui;
import com.naxanria.mappy.event.EventListener;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.map.Map;
import com.naxanria.mappy.map.MapGUI;
import com.naxanria.mappy.map.waypoint.WayPointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/*
  @author: Naxanria
*/
public class ClientSetup
{
  public static void setup()
  {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(ClientSetup::setupClient);
  
    MappyConfig.register(ModLoadingContext.get());
  
    MapGUI mapGUI = new MapGUI(Mappy.map = new Map(), 4, DrawPosition.TOP_RIGHT);
  
    ModLoadingContext ctx = ModLoadingContext.get();
  
    ctx.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ClientSetup::openConfigScreen);
  
    IEventBus eventBus = MinecraftForge.EVENT_BUS;
    eventBus.addListener(WayPointManager::onWorldEnterEvent);
    eventBus.addListener(EventListener::clientTick);
    eventBus.addListener(EventListener::hudUpdate);
  }
  
  private static Screen openConfigScreen(Minecraft minecraft, Screen parent)
  {
    return new ConfigGui(parent);
  }
  
  private static void setupClient(final FMLClientSetupEvent event)
  {
    EventListener.setupKeyBinds();
    Mappy.showMap = MappyConfig.showMap;
  }
}
