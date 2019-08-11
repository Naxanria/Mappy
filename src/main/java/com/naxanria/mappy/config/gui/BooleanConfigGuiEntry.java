package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.gui.widget.CycleButton;
import net.minecraftforge.common.ForgeConfigSpec;

public class BooleanConfigGuiEntry extends ConfigGuiEntry<Boolean, ForgeConfigSpec.BooleanValue>
{
  private CycleButton.Boolean booleanCycle;
  
  public BooleanConfigGuiEntry(ForgeConfigSpec configSpec, ForgeConfigSpec.BooleanValue configValue)
  {
    super(configSpec, configValue);
  }
  
  @Override
  public void init()
  {
    super.init();
  
    if (booleanCycle == null)
    {
      booleanCycle = new CycleButton.Boolean(0, 0, value).setOnChangeCallback((b) -> {displayValue = ((CycleButton.Boolean) b).get();});
    }
    
    children.add(booleanCycle);
    rightAlign(booleanCycle, resetStartValueButton, 2);
    booleanCycle.y = y;
    displayValue = booleanCycle.get();
  }
}
