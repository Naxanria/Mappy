package com.naxanria.mappy.client;

import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;

import java.util.ArrayList;
import java.util.List;

public enum KeyHandler
{
  INSTANCE;
  
  private List<KeyParser> parsers = new ArrayList<>();
  
  KeyHandler()
  {}
  
  public void register(KeyParser parser)
  {
    KeyBindingRegistry.INSTANCE.register(parser.keyBinding);
    parsers.add(parser);
  }
  
  void update()
  {
    for (KeyParser kp :
      parsers)
    {
      if (kp.isListening())
      {
        if (kp.keyBinding.wasPressed())
        {
          kp.onKeyUp();
        }
        else if (kp.keyBinding.isPressed())
        {
          kp.onKeyDown();
        }
      }
    }
  }
}
