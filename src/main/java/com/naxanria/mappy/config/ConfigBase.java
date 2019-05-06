package com.naxanria.mappy.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigBase<T extends ConfigBase>
{
  public interface ConfigChangedListener
  {
    void onConfigChanged(ConfigBase<?> config);
  }
  
  private static List<ConfigChangedListener> listeners = new ArrayList<>();
  
  public static void registerListener(ConfigChangedListener listener)
  {
    listeners.add(listener);
  }
  
  public final DataMap dataMap;
  
  private Map<String, ConfigEntry<?>> entryMap = new HashMap<>();
  
  public ConfigBase(File file)
  {
    this.dataMap = new DataMap(file, true);
    
    init();

    load();
  
    for (String key : entryMap.keySet())
    {
      ConfigEntry<?> entry = entryMap.get(key);
      
      if (!dataMap.contains(key))
      {
        System.out.println("Entry for " + entry.name + " not found, going to default value " + entry.defaultValue);
        entry.saveDefault();
      }
    }
    
    save();
    
    onConfigChanged();
  }
  
  protected abstract void init();
  
  public T addEntry(ConfigEntry entry)
  {
    entryMap.put(entry.name, entry);
    
    return (T) this;
  }
  
  public void load()
  {
    try
    {
      dataMap.load();
      onConfigChanged();
    }
    catch (IOException e)
    {
      System.err.println("Could not load the config!");
      e.printStackTrace();
    }
  }
  
  public void save()
  {
    try
    {
      dataMap.save();
    }
    catch (IOException e)
    {
      System.err.println("Could not save the config!");
      e.printStackTrace();
    }
  }
  
  public void onConfigChanged()
  {
    for (ConfigChangedListener listener :
      listeners)
    {
      listener.onConfigChanged(this);
    }
  }
  
  public ConfigEntry<?> getEntry(String key)
  {
    return entryMap.getOrDefault(key, null);
  }
  
  public ConfigEntry.IntegerEntry getIntEntry(String key)
  {
    ConfigEntry<?> entry = getEntry(key);
    
    if (entry != null && entry instanceof ConfigEntry.IntegerEntry)
    {
      return (ConfigEntry.IntegerEntry) entry;
    }
    
    return null;
  }
  
  public ConfigEntry.BooleanEntry getBooleanEntry(String key)
  {
    ConfigEntry<?> entry = getEntry(key);
    
    if (entry != null && entry instanceof ConfigEntry.BooleanEntry)
    {
      return (ConfigEntry.BooleanEntry) entry;
    }
    
    return null;
  }
  
  public ConfigEntry.StringEntry getStringEntry(String key)
  {
    ConfigEntry<?> entry = getEntry(key);
    
    if (entry != null && entry instanceof ConfigEntry.StringEntry)
    {
      return (ConfigEntry.StringEntry) entry;
    }
    
    return null;
  }
  
  public int getInt(String key)
  {
    ConfigEntry.IntegerEntry entry = getIntEntry(key);
    if (entry != null)
    {
      return entry.getValue();
    }
    
    return 1;
  }
  
  public T setInt(String key, int value)
  {
    ConfigEntry.IntegerEntry entry = getIntEntry(key);
    if (entry != null)
    {
      entry.setValue(value);
    }
    
    return (T) this;
  }
  
  public boolean getBoolean(String key)
  {
    ConfigEntry.BooleanEntry entry = getBooleanEntry(key);
    if (entry != null)
    {
      return entry.getValue();
    }
    
    return false;
  }
  
  public T setBoolean(String key, boolean value)
  {
    ConfigEntry.BooleanEntry entry = getBooleanEntry(key);
    if (entry != null)
    {
      entry.setValue(value);
    }
    
    return (T) this;
  }
  
  public String getString(String key)
  {
    ConfigEntry.StringEntry entry = getStringEntry(key);
    if (entry != null)
    {
      return entry.getValue();
    }
    
    return "";
  }
  
  public T setString(String key, String value)
  {
    ConfigEntry.StringEntry entry = getStringEntry(key);
    if (entry != null)
    {
      entry.setValue(value);
    }
    
    return (T) this;
  }
}
