package com.naxanria.mappy.config;

import com.naxanria.mappy.client.DrawPosition;

import java.io.File;
import java.io.IOException;

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
    addEntry(new ConfigEntry.IntegerEntry("offset", dataMap, 4));
    addEntry(new ConfigEntry.IntegerRangeEntry("drawPosition", dataMap, DrawPosition.TOP_RIGHT.ordinal(), DrawPosition.values().length - 1));
    addEntry(new ConfigEntry.IntegerRangeEntry("mapSize", dataMap, 64, 16, 256));
    addEntry(new ConfigEntry.BooleanEntry("showPosition", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showFPS", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showBiome", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showTime", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showPlayerNames", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showPlayerHeads", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showDirection", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("showEntities", dataMap, false));
//    addEntry(new ConfigEntry.IntegerRangeEntry("mapTriesLimit", dataMap, 50, 1, 255));
    addEntry(new ConfigEntry.IntegerRangeEntry("updatePerCycle", dataMap, 10, 1, 500));
    addEntry(new ConfigEntry.IntegerRangeEntry("pruneDelay", dataMap, 60, 1, 600));
    addEntry(new ConfigEntry.IntegerRangeEntry("pruneAmount", dataMap, 1500, 100, 50000));
    addEntry(new ConfigEntry.BooleanEntry("showMap", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("moveMapForEffects", dataMap, true));
    addEntry(new ConfigEntry.BooleanEntry("shaded", dataMap, true));
    addEntry(new ConfigEntry.IntegerRangeEntry("maxDifference", dataMap, 10, 2, 16));
//    addEntry(new ConfigEntry.BooleanEntry("alphaFeatures", dataMap, false));
  }
  
  @Override
  public void save()
  {
    setInt("offset", Settings.offset);
    setInt("drawPosition", Settings.drawPosition.ordinal());
    setInt("mapSize", Settings.mapSize);
    
    setBoolean("showPosition", Settings.showPosition);
    setBoolean("showFPS", Settings.showFPS);
    setBoolean("showBiome", Settings.showBiome);
    setBoolean("showTime", Settings.showTime);
    setBoolean("showPlayerNames", Settings.showPlayerNames);
    setBoolean("showPlayerHeads", Settings.showPlayerHeads);
    setBoolean("showEntities", Settings.showEntities);
    setBoolean("showDirection", Settings.showDirection);
    setInt("updatePerCycle", Settings.updatePerCycle);
    setInt("pruneDelay", Settings.pruneDelay);
    setInt("pruneAmount", Settings.pruneAmount);
    setBoolean("showMap", Settings.showMap);
    setBoolean("moveMapForEffects", Settings.moveMapForEffects);
    setBoolean("shaded", Settings.shaded);
    setInt("maxDifference", Settings.maxDifference);
    
    super.save();
  }
  
  @Override
  public void load()
  {
    try
    {
      dataMap.load();
    }
    catch (IOException e)
    {
      System.err.println("Could not load the config!");
      e.printStackTrace();
    }

    Settings.offset = getInt("offset");
    Settings.drawPosition = DrawPosition.values()[getInt("drawPosition")];
    Settings.mapSize = getInt("mapSize");
    Settings.showPosition = getBoolean("showPosition");
    Settings.showFPS = getBoolean("showFPS");
    Settings.showBiome = getBoolean("showBiome");
    Settings.showTime = getBoolean("showTime");
    Settings.showPlayerNames = getBoolean("showPlayerNames");
    Settings.showPlayerHeads = getBoolean("showPlayerHeads");
    Settings.showDirection = getBoolean("showDirection");
    Settings.showEntities = getBoolean("showEntities");
    Settings.updatePerCycle = getInt("updatePerCycle");
    Settings.pruneDelay = getInt("pruneDelay");
    Settings.pruneAmount = getInt("pruneAmount");
    Settings.showMap = getBoolean("showMap");
    Settings.moveMapForEffects = getBoolean("moveMapForEffects");
    Settings.shaded = getBoolean("shaded");
    Settings.maxDifference = getInt("maxDifference");
    
    onConfigChanged();
  }
//
//  public int getOffset()
//  {
//    return getInt("offset");
//  }
//
//  public DrawPosition getPosition()
//  {
//    int p = getInt("drawPosition");
//    return DrawPosition.values()[p];
//  }
//
//  public int getMapSize()
//  {
//    return getInt("mapSize");
//  }
//
//  public boolean showFPS()
//  {
//    return getBoolean("showFPS");
//  }
//
//  public boolean showPosition()
//  {
//    return getBoolean("showPosition");
//  }
//
//  public boolean showBiome()
//  {
//    return getBoolean("showBiome");
//  }
//
//  public boolean showTime()
//  {
//    return getBoolean("showTime");
//  }
//
//  public boolean showPlayerNames()
//  {
//    return getBoolean("showPlayerNames");
//  }
//
//  public boolean alphaFeatures()
//  {
//    return getBoolean("alphaFeatures");
//  }
//
//  public boolean showPlayerHeads()
//  {
//    return getBoolean("showPlayerHeads");
//  }
//
//  public int getMapTriesLimit()
//  {
//    return getInt("mapTriesLimit");
//  }
//
//  public boolean showDirection()
//  {
//    return getBoolean("showDirection");
//  }
//
//  public boolean showEntities()
//  {
//    return getBoolean("showEntities");
//  }
//
//  public int getUpdatePerCycle()
//  {
//    return getInt("updatePerCycle");
//  }
//
//  public int getPruneDelay()
//  {
//    return getInt("pruneDelay");
//  }
//
//  public int getPruneAmount()
//  {
//    return getInt("pruneAmount");
//  }
//
  public boolean getShowMap()
  {
    return getBoolean("showMap");
  }

  public void setShowMap(boolean show)
  {
    setBoolean("showMap", show);

    save();
  }
//
//  public boolean moveMapForEffects()
//  {
//    return getBoolean("moveMapForEffects");
//  }
}
