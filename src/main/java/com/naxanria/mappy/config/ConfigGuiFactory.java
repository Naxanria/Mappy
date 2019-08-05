package com.naxanria.mappy.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ConfigGuiFactory implements IModGuiFactory
{
  @Override
  public void initialize(Minecraft minecraftInstance)
  {
  
  }
  
  @Override
  public boolean hasConfigGui()
  {
    return false;
  }
  
  @Override
  public Screen createConfigGui(Screen parentScreen)
  {
    
    return null;
  }
  
  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
  {
    return null;
  }
}
