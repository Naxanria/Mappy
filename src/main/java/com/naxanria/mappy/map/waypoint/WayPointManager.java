package com.naxanria.mappy.map.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum WayPointManager
{
  INSTANCE();
  
  private Map<Integer, List<WayPoint>> wayPoints = new HashMap<>();
  
  WayPointManager()
  { }
  
  private CompoundTag writeToNBT(CompoundTag tag)
  {
    if (tag == null)
    {
      tag = new CompoundTag();
    }
  
    List<Integer> dimList = new ArrayList<>();
    
    for (Integer dimension :
      wayPoints.keySet())
    {
      List<WayPoint> wps = wayPoints.get(dimension);
      if (wps.size() == 0)
      {
        continue;
      }
      
      ListTag wpsTags = new ListTag();
      for (WayPoint wp :
        wps)
      {
        CompoundTag wpTag = new CompoundTag();
        wp.writeToNBT(wpTag);
        wpsTags.add(wpTag);
      }
      
      tag.put("wps" + dimension, wpsTags);
      dimList.add(dimension);
    }
    
    tag.putIntArray("dimensions", dimList);
    
    return tag;
  }
  
  private void readFromNBT(CompoundTag tag)
  {
    if (tag == null)
    {
      return;
    }
    
    wayPoints.clear();
    
    if (!tag.containsKey("dimensions"))
    {
      return;
    }
    int[] dimList = tag.getIntArray("dimensions");
  
    for (int dim :
      dimList)
    {
      String tagName = "wps" + dim;
      if (!tag.containsKey(tagName))
      {
        continue;
      }
      ListTag wps = tag.getList(tagName, tag.getType());
      for (int i = 0; i < wps.size(); i++)
      {
        CompoundTag wpsTag = wps.getCompoundTag(i);
        WayPoint wayPoint = new WayPoint().readFromNBT(wpsTag);
        add(wayPoint);
      }
    }
  }
  
  public void load()
  {
    try
    {
      CompoundTag tag = NbtIo.read(getSaveFile());
      if (tag != null && !tag.isEmpty())
      {
        readFromNBT(tag);
        System.out.println("Loaded waypoints");
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void save()
  {
    try
    {
      NbtIo.safeWrite(writeToNBT(new CompoundTag()), getSaveFile());
      System.out.println("Saved waypoints");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private File getSaveFile()
  {
    File mappyDir;
    File saveDir;
  
    MinecraftClient client = MinecraftClient.getInstance();
  
    mappyDir = new File(client.runDirectory.getAbsolutePath() + "/mappy");
    mappyDir.mkdirs();
  
    if (client.isInSingleplayer())
    {
      MinecraftServer server = MinecraftClient.getInstance().getServer();
      if (server != null)
      {
        saveDir = new File(mappyDir, "/local/" + server.getLevelName() + "/");
      }
      else
      {
        saveDir = new File(mappyDir, "/local/UNKNOWN/");
      }
    }
    else
    {
      ServerEntry serverEntry = client.getCurrentServerEntry();
      if (serverEntry != null)
      {
        saveDir = new File(mappyDir, "/servers/" + Integer.toHexString(serverEntry.address.hashCode()) + "/");
      }
      else
      {
        saveDir = new File(mappyDir, "/servers/UNKNOWN/");
      }
    }

//    String debug = MinecraftClient.getInstance().world.getWorld();
    File saveFile = new File(saveDir, "waypoints.dat");
    saveDir.mkdirs();
    if (!saveFile.exists())
    {
      try
      {
        saveFile.createNewFile();
        CompoundTag emptyTag = new CompoundTag();
        NbtIo.safeWrite(emptyTag, saveFile);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    
    return saveFile;
  }
  
  public void add(WayPoint wayPoint)
  {
    Integer dimension = wayPoint.dimension;
    List<WayPoint> wps = getWaypoints(dimension);
    wps.add(wayPoint);
  }
  
  public void remove(WayPoint wayPoint)
  {
    Integer dimension = wayPoint.dimension;
    getWaypoints(dimension).remove(wayPoint);
  }
  
  public List<WayPoint> getWaypoints(int dimension)
  {
    if (!wayPoints.containsKey(dimension))
    {
      List<WayPoint> list = new ArrayList<>();
      wayPoints.put(dimension, list);
      return list;
    }
    
    return wayPoints.get(dimension);
  }
  
  public List<WayPoint> getWaypointsToRender(int dimension)
  {
    List<WayPoint> list = getWaypoints(dimension).stream()
      .filter(WayPoint::show)
      .collect(Collectors.toList());
    
    return list;
  }
  
  public List<Integer> getWaypointDimensions()
  {
    return new ArrayList<>(wayPoints.keySet());
  }
}
