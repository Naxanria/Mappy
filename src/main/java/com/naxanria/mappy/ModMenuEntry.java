package com.naxanria.mappy;

import com.naxanria.mappy.client.DrawPosition;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.config.ConfigBase;
import com.naxanria.mappy.config.Settings;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.entries.BooleanListEntry;
import me.shedaniel.cloth.gui.entries.EnumListEntry;
import me.shedaniel.cloth.gui.entries.IntegerListEntry;
import me.shedaniel.cloth.gui.entries.IntegerSliderEntry;
import net.minecraft.client.gui.Screen;

import java.util.function.Function;

public class ModMenuEntry implements ModMenuApi
{
  private static final String RESET = "text.cloth.reset_value";
  
  @Override
  public String getModId()
  {
    return Mappy.MODID;
  }
  
  private Screen getScreen(Screen parent)
  {
    ConfigScreenBuilder builder = ConfigScreenBuilder.create(parent, "Mappy Config", this::saveConfig);
    
    ConfigScreenBuilder.CategoryBuilder general = builder.addCategory("General");
    general.addOption(new IntegerListEntry("Offset", Settings.offset, RESET, () -> Settings.offset, (i) -> Settings.offset = i));
    general.addOption(new EnumListEntry<>("Draw Position", DrawPosition.class, Settings.drawPosition, RESET, () -> Settings.drawPosition, (p) -> Settings.drawPosition = p));
    general.addOption(new IntegerListEntry("Map Size", Settings.mapSize, RESET, () -> Settings.mapSize, (i) -> Settings.mapSize = i).setMinimum(32).setMaximum(1024));
//    general.addOption(new IntegerSliderEntry("Map Scale", 1, 8, Settings.scale, RESET, () -> Settings.scale, (i) -> Settings.scale = i));
    general.addOption(new BooleanListEntry("Move Map For Effects", Settings.moveMapForEffects, RESET, () -> Settings.moveMapForEffects, (b) -> Settings.moveMapForEffects = b));
    general.addOption(new BooleanListEntry("Show Map While In Chat", Settings.showInChat, RESET, () -> Settings.showInChat, (b) -> Settings.showInChat = b));
    
    general.addOption(new BooleanListEntry("Shaded", Settings.shaded, RESET, () -> Settings.shaded, (b) -> Settings.shaded = b));
    general.addOption(new IntegerSliderEntry("Shading strength",2, 16, 18 - Settings.maxDifference, RESET, () -> 18 - Settings.maxDifference, (i) -> Settings.maxDifference = 18 - i));
    
    
    ConfigScreenBuilder.CategoryBuilder mapInfo = builder.addCategory("Map Info");
    mapInfo.addOption(new BooleanListEntry("Draw Chunk Grid", Settings.drawChunkGrid, RESET, () -> Settings.drawChunkGrid, (b) -> Settings.drawChunkGrid = b));
    mapInfo.addOption(new BooleanListEntry("Show Position", Settings.showPosition, RESET, () -> Settings.showPosition, (b) -> Settings.showPosition = b));
    mapInfo.addOption(new BooleanListEntry("Show Biome", Settings.showBiome, RESET, () -> Settings.showBiome, (b) -> Settings.showBiome= b));
    mapInfo.addOption(new BooleanListEntry("Show FPS", Settings.showFPS, RESET, () -> Settings.showFPS, (b) -> Settings.showFPS = b));
    mapInfo.addOption(new BooleanListEntry("Show Time", Settings.showTime, RESET, () -> Settings.showTime, (b) -> Settings.showTime = b));
    mapInfo.addOption(new BooleanListEntry("Show Direction", Settings.showDirection, RESET, () -> Settings.showDirection, (b) -> Settings.showDirection = b));
    
    mapInfo.addOption(new BooleanListEntry("Show Player Names", Settings.showPlayerNames, RESET, () -> Settings.showPlayerNames, (b) -> Settings.showPlayerNames = b));
    mapInfo.addOption(new BooleanListEntry("Show Players As Heads", Settings.showPlayerHeads, RESET, () -> Settings.showPlayerHeads, (b) -> Settings.showPlayerHeads = b));
    mapInfo.addOption(new BooleanListEntry("Show Entities", Settings.showEntities, RESET, () -> Settings.showEntities, (b) -> Settings.showEntities = b));
  
    ConfigScreenBuilder.CategoryBuilder optimization = builder.addCategory("Optimization");
    optimization.addOption(new IntegerListEntry("Update Per Cycle", Settings.updatePerCycle, RESET, () -> Settings.updatePerCycle, (i) -> Settings.updatePerCycle = i).setMinimum(1).setMaximum(1000));
    optimization.addOption(new IntegerListEntry("Prune Delay", Settings.pruneDelay, RESET, () -> Settings.pruneDelay, (i) -> Settings.pruneDelay = i).setMinimum(10).setMaximum(600));
    optimization.addOption(new IntegerListEntry("Prune Amount", Settings.pruneAmount, RESET, () -> Settings.pruneAmount, (i) -> Settings.pruneAmount = i).setMinimum(100).setMaximum(5000));
    
    builder.setDoesConfirmSave(false);
    
    return builder.build();
  }
  
  private void saveConfig(ConfigScreenBuilder.SavedConfig config)
  {
    Config.instance.save();
    System.out.println("Saved config");
    Mappy.map.onConfigChanged();
  }
  
  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory()
  {
    return (this::getScreen);
  }
}
