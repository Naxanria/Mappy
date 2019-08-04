package com.naxanria.mappy.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public abstract class KeyParser
{
  public final KeyBinding keyBinding;
  public static final Minecraft mc = Minecraft.getInstance();
  
  private boolean state = false;
  private boolean lastState = false;

  protected KeyParser(KeyBinding keyBinding)
  {
    this.keyBinding = keyBinding;
  }

  final void update()
  {
    lastState = state;
    state = keyBinding.isKeyDown();
    
    if (isListening())
    {
      if (state && !lastState)
      {
        onKeyDown();
      }
      else if (!state && lastState)
      {
        onKeyUp();
      }
    }
  }

  public void onKeyDown()
  {}

  public void onKeyUp()
  {}

  public boolean isListening()
  {
    return true;
  }
}
