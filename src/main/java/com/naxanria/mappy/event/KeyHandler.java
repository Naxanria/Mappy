package com.naxanria.mappy.event;



import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum KeyHandler
{
  INSTANCE;

  private List<KeyParser> parsers = new ArrayList<>();
  private Map<Integer, Boolean> keyStates = new HashMap<>();
  private Map<Integer, Boolean> previousStates = new HashMap<>();
  private List<Integer> toWatch = new ArrayList<>();

  KeyHandler()
  {}

  public void register(KeyParser parser)
  {
    ClientRegistry.registerKeyBinding(parser.keyBinding);
    parsers.add(parser);
  }

  void update()
  {
    previousStates = keyStates;
    keyStates = new HashMap<>();
  
    for (int code : toWatch)
    {
      keyStates.put(code, InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), code));
    }
    
    for (KeyParser kp :
      parsers)
    {
      kp.update();
//      if (kp.isListening())
//      {
//        if (kp.keyBinding.())
//        {
//          kp.onKeyUp();
//        }
//        else if (kp.keyBinding.isPressed())
//        {
//          kp.onKeyDown();
//        }
//      }
    }
  }
  
  public boolean isKeyPressed(int keyCode)
  {
    if (!isWatching(keyCode))
    {
      return false;
    }
    
    return !previousStates.getOrDefault(keyCode, false) && keyStates.getOrDefault(keyCode, false);
  }
  
  public boolean isKeyDown(int keyCode)
  {
    if (isWatching(keyCode))
    {
      return keyStates.getOrDefault(keyCode, false);
    }
    
    toWatch.add(keyCode);
    
    return InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), keyCode);
  }
  
  public boolean isKeyUp(int keyCode)
  {
    if (!isWatching(keyCode))
    {
      return false;
    }
    
    return previousStates.getOrDefault(keyCode, false) && !keyStates.getOrDefault(keyCode, false);
  }
  
  public void watch(int keyCode)
  {
    if (!toWatch.contains(keyCode))
    {
      toWatch.add(keyCode);
    }
  }
  
  public void watch(int... keyCodes)
  {
    for (int code : keyCodes)
    {
      watch(code);
    }
  }
  
  public boolean isWatching(int keyCode)
  {
    return toWatch.contains(keyCode);
  }
}
