package com.naxanria.mappy.map.waypoint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.world.WorldEvent;

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
  
  private CompoundNBT writeToNBT(CompoundNBT tag)
  {
    if (tag == null)
    {
      tag = new CompoundNBT();
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
  
      ListNBT wpsTags = new ListNBT();
      for (WayPoint wp :
        wps)
      {
        CompoundNBT wpTag = new CompoundNBT();
        wp.writeToNBT(wpTag);
        wpsTags.add(wpTag);
      }
      
      tag.put("wps" + dimension, wpsTags);
      dimList.add(dimension);
    }
    
    tag.putIntArray("dimensions", dimList);
    
    return tag;
  }
  
  private void readFromNBT(CompoundNBT tag)
  {
    if (tag == null)
    {
      return;
    }
    
    wayPoints.clear();
    
    if (!tag.contains("dimensions"))
    {
      return;
    }
    
    int[] dimList = tag.getIntArray("dimensions");
    boolean converted = false;
  
    for (int dim :
      dimList)
    {
      String tagName = "wps" + dim;
      if (!tag.contains(tagName))
      {
        continue;
      }
      ListNBT wps = tag.getList(tagName, tag.getId());
      for (int i = 0; i < wps.size(); i++)
      {
        CompoundNBT wpsTag = wps.getCompound(i);
        WayPoint wayPoint = new WayPoint().readFromNBT(wpsTag);
        converted |= wayPoint.converted;
        add(wayPoint);
      }
    }
    
    if (converted)
    {
      save();
    }
  }
  
  public static void onWorldEnterEvent(final WorldEvent.Load event)
  {
    if (event.getWorld().getDimension().getType() == DimensionType.OVERWORLD)
    {
      INSTANCE.load();
    }
  }
  
  public void load()
  {
    // clear our old waypoints
    wayPoints.clear();
    
    try
    {
      CompoundNBT tag = CompressedStreamTools.read(getSaveFile());
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
      CompressedStreamTools.safeWrite(writeToNBT(new CompoundNBT()), getSaveFile());
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
  
    Minecraft client = Minecraft.getInstance();
  
    mappyDir = new File(client.gameDir.getAbsolutePath() + "/mappy");
    mappyDir.mkdirs();
  
    if (client.isSingleplayer())
    {
      MinecraftServer server = Minecraft.getInstance().getIntegratedServer();
      if (server != null)
      {
        saveDir = new File(mappyDir, "/local/" + server.getFolderName() + "/");
      }
      else
      {
        saveDir = new File(mappyDir, "/local/UNKNOWN/");
      }
    }
    else
    {
  
      ServerData serverData = client.getCurrentServerData();
      if (serverData != null)
      {
        saveDir = new File(mappyDir, "/servers/" + Integer.toHexString(serverData.serverIP.hashCode()) + "/");
      }
      else
      {
        saveDir = new File(mappyDir, "/servers/UNKNOWN/");
      }
    }

//    String debug = Minecraft.getInstance().world.getWorld();
    File saveFile = new File(saveDir, "waypoints.dat");
    saveDir.mkdirs();
    if (!saveFile.exists())
    {
      try
      {
        saveFile.createNewFile();
        CompoundNBT emptyTag = new CompoundNBT();
        CompressedStreamTools.safeWrite(emptyTag, saveFile);
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
