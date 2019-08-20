package com.naxanria.mappy.config.gui;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.common.ForgeConfigSpec;

public class StringConfigGuiEntry extends ConfigGuiEntry<String, ForgeConfigSpec.ConfigValue<String>>
{
  private TextFieldWidget textField;
  
  public StringConfigGuiEntry(ForgeConfigSpec configSpec, ForgeConfigSpec.ConfigValue<String> configValue)
  {
    super(configSpec, configValue);
  }
  
  @Override
  public void init()
  {
    super.init();
    
    if (textField == null)
    {
      textField = new TextFieldWidget(font, 0, 0, font.getStringWidth("W") * 12, height, value);
      textField.setText(displayValue);
      textField.func_212954_a((s) -> displayValue = textField.getText());
    }
    
    textField.y = y;
    rightAlign(textField, resetStartValueButton, 1);
    children.add(textField);
  }
  
  @Override
  protected void setDisplayValue(String value)
  {
    super.setDisplayValue(value);
    
    textField.setText(value);
  }
}
