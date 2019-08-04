package com.naxanria.mappy.client;



import net.minecraftforge.fml.client.registry.ClientRegistry;

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
    ClientRegistry.registerKeyBinding(parser.keyBinding);
    parsers.add(parser);
  }

  void update()
  {
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
}
