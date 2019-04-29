package com.naxanria.mappy.config;

import com.naxanria.mappy.client.DrawPosition;

import java.io.File;

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
  }
  
  public int getOffset()
  {
    return getInt("offset");
  }
  
  public DrawPosition getPosition()
  {
    int p = getInt("drawPosition");
    return DrawPosition.values()[p];
  }
  
  public int getMapSize()
  {
    return getInt("mapSize");
  }
}
