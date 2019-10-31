package com.naxanria.mappy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class MappyFileUtil
{
  public static File getSaveDirectory()
  {
    Minecraft client = Minecraft.getInstance();
    File saveDir;
    File mappyDir = new File(client.gameDir.getAbsolutePath() + "/mappy");
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
    
    return saveDir;
  }
  
  public static File createSubDir(File parent, String name)
  {
    File newDir = new File(parent, name);
    newDir.mkdir();
    
    return newDir;
  }
  
}
