package com.naxanria.mappy.config;

import com.naxanria.mappy.client.DrawPosition;

public class Settings
{
  public static int offset = 4;
  public static DrawPosition drawPosition = DrawPosition.TOP_RIGHT;
  public static int mapSize = 64;
  
  public static boolean showPosition = true;
  public static boolean showFPS = false;
  public static boolean showBiome = true;
  public static boolean showTime = true;
  public static boolean showDirection = false;
  
  public static boolean showPlayerNames = true;
  public static boolean showPlayerHeads = true;
  public static boolean showEntities = true;
  
  public static int updatePerCycle = 10;
  public static int pruneDelay = 60;
  public static int pruneAmount = 1500;
  
  public static boolean showMap = true;
  
  public static boolean moveMapForEffects = true;
  
  public static boolean shaded = true;
  public static int maxDifference = 10;
  public static boolean drawChunkGrid = false;
  public static int scale = 1;
}
