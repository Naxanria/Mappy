package com.naxanria.mappy.client;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.MinecraftClient;

public abstract class KeyParser
{
  public final FabricKeyBinding keyBinding;
  public static final MinecraftClient mc = MinecraftClient.getInstance();
  
  protected KeyParser(FabricKeyBinding keyBinding)
  {
    this.keyBinding = keyBinding;
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
