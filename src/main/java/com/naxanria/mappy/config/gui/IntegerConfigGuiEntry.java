package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.util.Predicates;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Predicate;

public class IntegerConfigGuiEntry extends ConfigGuiEntry<Integer,ForgeConfigSpec.IntValue>
{
  private TextFieldWidget integerField;
  private static Predicate<String> validNumber = (s) -> Predicates.or(s, Predicates.isInteger, Predicates.isEmpty, (s1 -> s.equals("-")));
  
  public IntegerConfigGuiEntry(ForgeConfigSpec configSpec, ForgeConfigSpec.IntValue configValue)
  {
    super(configSpec, configValue);
  }
  
  @Override
  public void init()
  {
    super.init();
    
    if (integerField == null)
    {
      integerField = new TextFieldWidget(font, 0, 0, font.getStringWidth("-999999999"), height, value.toString());
      integerField.setValidator(validNumber);
      integerField.setMaxStringLength(11);
      integerField.setText(displayValue.toString());
      integerField.func_212954_a((s) -> displayValue = getValue()); // callback for when text changed
    }
    
    rightAlign(integerField, resetStartValueButton, 1);
    integerField.y = y;
    
    integerField.setTextColor(inRange() ? 0xffffffff : 0xffff0000);
//    displayValue = getValue();
    
    children.add(integerField);
  }
  
  public int getValue()
  {
    String val = integerField.getText();
    
    try
    {
      return val.equals("") || val.equals("-") ? 0 : Integer.parseInt(val);
    }
    catch (Exception e)
    {
      return 0;
    }
  }
  
  public boolean inRange()
  {
    Integer val = getValue();
    Object correct = valueSpec.correct(val);
    return correct == val;
  }
}
