package com.naxanria.mappy.config;

import com.naxanria.mappy.client.DrawPosition;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Config extends ConfigBase<Config>
{
  public static Config instance;
  
  public Config(File file)
  {
    super(file);
    
    instance = this;
  }
  
  @Override
  protected void init()
  {
    addEntry(new ConfigEntry.IntegerEntry("offset", 4, (i) -> Settings.offset = i, () -> Settings.offset));
//    addEntry(new ConfigEntry.IntegerRangeEntry("drawPosition", DrawPosition.TOP_RIGHT.ordinal(), DrawPosition.values().length - 1,
//      (i) -> Settings.drawPosition = DrawPosition.get(i), () -> Settings.drawPosition.ordinal()));
    addEntry(new ConfigEntry.EnumEntry<>("drawPosition", DrawPosition.class, DrawPosition.TOP_RIGHT, (e) -> Settings.drawPosition = e, () -> Settings.drawPosition));
    addEntry(new ConfigEntry.IntegerRangeEntry("mapSize", 64, 16, 256, (i) -> Settings.mapSize = i, () -> Settings.mapSize));
    addEntry(new ConfigEntry.BooleanEntry("showPosition", true, (b) -> Settings.showPosition = b, () -> Settings.showPosition));
    addEntry(new ConfigEntry.BooleanEntry("showFPS", true, (b) -> Settings.showFPS = b, () -> Settings.showFPS));
    addEntry(new ConfigEntry.BooleanEntry("showBiome", true, (b) -> Settings.showBiome = b, () -> Settings.showBiome));
    addEntry(new ConfigEntry.BooleanEntry("showTime", true, (b) -> Settings.showTime = b, () -> Settings.showTime));
    addEntry(new ConfigEntry.BooleanEntry("showPlayerNames", true, (b) -> Settings.showPlayerNames = b, () -> Settings.showPlayerNames));
    addEntry(new ConfigEntry.BooleanEntry("showPlayerHeads", true, (b) -> Settings.showPlayerHeads = b, () -> Settings.showPlayerHeads));
    addEntry(new ConfigEntry.BooleanEntry("showDirection", true, (b) -> Settings.showDirection = b, () -> Settings.showDirection));
    addEntry(new ConfigEntry.BooleanEntry("showEntities", false, (b) -> Settings.showEntities = b, () -> Settings.showEntities));

    addEntry(new ConfigEntry.IntegerRangeEntry("updatePerCycle",  10, 1, 500, (i) -> Settings.updatePerCycle = i, () -> Settings.updatePerCycle));
    addEntry(new ConfigEntry.IntegerRangeEntry("pruneDelay",  60, 1, 600, (i) -> Settings.pruneDelay = i, () -> Settings.pruneDelay));
    addEntry(new ConfigEntry.IntegerRangeEntry("pruneAmount",  1500, 100, 50000, (i) -> Settings.pruneAmount = i, () -> Settings.pruneAmount));
    addEntry(new ConfigEntry.BooleanEntry("showMap", true, (b) -> Settings.showMap = b, () -> Settings.showMap));
    addEntry(new ConfigEntry.BooleanEntry("moveMapForEffects", true, (b) -> Settings.moveMapForEffects = b, () -> Settings.moveMapForEffects));
    addEntry(new ConfigEntry.BooleanEntry("shaded", true, (b) -> Settings.shaded = b, () -> Settings.shaded));
    addEntry(new ConfigEntry.IntegerRangeEntry("maxDifference",  10, 2, 16, (i) -> Settings.maxDifference = i, () -> Settings.maxDifference));
    addEntry(new ConfigEntry.BooleanEntry("drawChunkGrid", false, (b) -> Settings.drawChunkGrid = b, () -> Settings.drawChunkGrid));
    addEntry(new ConfigEntry.IntegerRangeEntry("scale",  1, 1, 8, (i) -> Settings.scale = i, () -> Settings.scale));
  }
  
  public boolean getShowMap()
  {
    return getBoolean("showMap");
  }

  public void setShowMap(boolean show)
  {
    setBoolean("showMap", show);

    save();
  }
}
