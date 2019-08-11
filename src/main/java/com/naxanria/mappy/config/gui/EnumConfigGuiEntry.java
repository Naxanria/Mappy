package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.gui.widget.CycleButton;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnumConfigGuiEntry<T extends Enum<T>> extends ConfigGuiEntry<T, ForgeConfigSpec.EnumValue<T>>
{
  private CycleButton.EnumButton<T> enumButton;
  
  public EnumConfigGuiEntry(ForgeConfigSpec configSpec, ForgeConfigSpec.EnumValue<T> configValue)
  {
    super(configSpec, configValue);
  }
  
  @Override
  public void init()
  {
    super.init();
    
    if (enumButton == null)
    {
      enumButton = new CycleButton.EnumButton<>(0, 0, value).setOnChangeCallback((b)-> displayValue = ((CycleButton.EnumButton<T>) b).get());
    }
    
    enumButton.y = y;
    children.add(rightAlign(enumButton, resetStartValueButton, 1));
  }
}
