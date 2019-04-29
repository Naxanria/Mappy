package com.naxanria.mappy.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class DataMap
{
  private final Map<String, String> dataMap = new HashMap<>();
  
  private final File dataLocation;
  
  private boolean createOnMissing = false;
  
  public DataMap(File dataLocation)
  {
    this.dataLocation = dataLocation;
  }
  
  public DataMap(File dataLocation, boolean createOnMissing)
  {
    this.dataLocation = dataLocation;
    this.createOnMissing = createOnMissing;
  }
  
  public DataMap load() throws IOException
  {
    return load(false);
  }
  
  public DataMap load(boolean createFile) throws IOException
  {
    if (!dataLocation.exists())
    {
      if (createFile)
      {
        boolean success = dataLocation.createNewFile();
        
        if (!success)
        {
          throw new IOException();
        }
      }
      else
      {
        throw new FileNotFoundException(dataLocation.getAbsolutePath());
      }
    }
    
    dataMap.clear();
    
    Stream<String> stream = Files.lines(dataLocation.toPath());
    stream.forEach(this::deSerialize);
    
    return this;
  }
  
  public DataMap save() throws IOException
  {
    return save(true);
  }
  
  public DataMap save(boolean createFile) throws IOException
  {
    if (!dataLocation.exists())
    {
      if (createFile)
      {
        boolean success = dataLocation.createNewFile();
      
        if (!success)
        {
          throw new IOException();
        }
      }
      else
      {
        throw new FileNotFoundException(dataLocation.getAbsolutePath());
      }
    }
  
    List<String> lines = new ArrayList<>();
    for (String key :
      dataMap.keySet())
    {
      lines.add(serialize(key));
    }
    
    Files.write(dataLocation.toPath(), lines);
    
    return this;
  }
  
  private String serialize(String key)
  {
    String val = dataMap.get(key);
    return key + "=" + val;
  }
  
  private void deSerialize(String line)
  {
    if (line.isEmpty() || !line.contains("="))
    {
      return;
    }
    
    String key, val;
    int p = line.indexOf("=");
    
    key = line.substring(0, p);
    val = line.substring(p + 1);
    
    if (key.contains(" "))
    {
      key = key.replaceAll(" ", "");
    }
    
    if (val.startsWith(" "))
    {
      if (val.length() == 1)
      {
        val = "";
      }
      else
      {
        val = val.substring(1);
      }
    }
    
    dataMap.put(key, val);
  }

  
  public String get(String key, String defaultValue)
  {
    if (!createOnMissing)
    {
      return dataMap.getOrDefault(key, defaultValue);
    }
    
    if (!dataMap.containsKey(key))
    {
      dataMap.put(key, defaultValue);
      return defaultValue;
    }
    
    return dataMap.get(key);
  }
  
  public int getInt(String key, int defaultValue)
  {
    String val = get(key, defaultValue + "");
//    ItemGot.logger.info("!!{}->{}!!", key, val);
    Integer i;
    try
    {
      i = Integer.valueOf(val);
    }
    catch (NumberFormatException e)
    {
      return defaultValue;
    }
    
    return i ;
  }
  
  public boolean getBoolean(String key, boolean defaultValue)
  {
    String val = get(key, defaultValue ? "true" : "false");
    return val.equalsIgnoreCase("true");
  }
  
  public DataMap set(String key, String value)
  {
    dataMap.put(key, value);
    
    return this;
  }
  
  public DataMap set(String key, int value)
  {
    return set(key, value + "");
  }
  
  public DataMap set(String key, boolean value)
  {
    return set(key, value ? "true" : "false");
  }
  
  public boolean isCreateOnMissing()
  {
    return createOnMissing;
  }
  
  public DataMap setCreateOnMissing(boolean createOnMissing)
  {
    this.createOnMissing = createOnMissing;
    return this;
  }
  
  public int size()
  {
    return dataMap.size();
  }
  
  public Set<String> keys()
  {
    return dataMap.keySet();
  }
  
  public File getDataLocation()
  {
    return dataLocation;
  }
  
  public boolean contains(String key)
  {
    return dataMap.containsKey(key);
  }
}
