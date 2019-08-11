package com.naxanria.mappy.config.gui;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.common.base.Strings;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.config.MappyConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigGui extends ScreenBase
{
  private List<ConfigGuiEntry<?, ?>> entries = new ArrayList<>();
  private List<String> keys = new ArrayList<>();
  private ForgeConfigSpec spec;
  
  public ConfigGui(Screen parent)
  {
    super(new StringTextComponent("Config"), parent);
  }
  
  @Override
  public void init()
  {
//    keys.clear();
    
    
    spec = MappyConfig.getSpec();
    
    if (entries.size() == 0)
    {
      addEntry(MappyConfig.config.mapSize);
      addEntry(MappyConfig.config.offset);
      addEntry(MappyConfig.config.drawPosition);
      addEntry(MappyConfig.config.showMap);
    }
    children.clear();
    children.addAll(entries);
    
 //    walkThrough(configSpec.valueMap());
  }
  
  protected ConfigGui addEntry(ForgeConfigSpec.ConfigValue<String> var)
  {
    return addEntry(new ConfigGuiEntry<>(spec, var));
  }
  
  protected ConfigGui addEntry(ForgeConfigSpec.IntValue var)
  {
    return addEntry(new IntegerConfigGuiEntry(spec, var));
  }
  
  protected ConfigGui addEntry(ForgeConfigSpec.BooleanValue var)
  {
    return addEntry(new BooleanConfigGuiEntry(spec, var));
  }
  
  protected <T extends Enum<T>> ConfigGui addEntry(ForgeConfigSpec.EnumValue<T> var)
  {
    return addEntry(new EnumConfigGuiEntry<>(spec, var));
  }
  
  protected ConfigGui addEntry(ConfigGuiEntry<?, ?> entry)
  {
    entries.add(entry);
    
    return this;
  }
  
  
  private void walkThrough(Map<String, Object> valueMap)
  {
    walkThrough(valueMap, 0, "");
  }
  
  private void walkThrough(Map<String, Object> valueMap, int depth, String path)
  {
    String spacer = Strings.repeat(" ", depth * 4);
    valueMap.forEach
    (
      (name, obj) ->
      {
        if (obj instanceof AbstractConfig)
        {
          AbstractConfig config = (AbstractConfig) obj;
          keys.add(spacer + name);
          walkThrough(config.valueMap(), depth + 1, path.equals("") ? name : path + "." + name);
        }
        else if (obj instanceof ForgeConfigSpec.ValueSpec)
        {
          ForgeConfigSpec.ValueSpec valueSpec = (ForgeConfigSpec.ValueSpec) obj;
          
//          Object val = configSpec.getRaw(path);
          Object def = valueSpec.getDefault();
          
          
          
  
          keys.add(spacer + name + ": " + valueSpec.getClazz().getSimpleName() + " [" + def + "]");
        }
        else
        {
          keys.add(spacer + name + ":" + obj);
        }
      }
    );
  }
  
  @Override
  public void renderBackground()
  {
    int windowWidth = minecraft.mainWindow.getScaledWidth();
    int windowHeight = minecraft.mainWindow.getScaledHeight();
    
    fill(0, 0, windowWidth, windowHeight, 0xffaaaaaa);
  }
  
  @Override
  public void renderPreChildren()
  {
    setupEntries();
  }
  
  private void setupEntries()
  {
    int x = 10;
    int y = 40;
    int width = windowWidth - x - 20;
    int scroll = 0;
    int spacing = 2;
    
    int totHeight = 0;
    
    for (ConfigGuiEntry<?, ?> entry :
      entries)
    {
      entry.width = width;
      entry.setPosition(x, y);
      int h = entry.height;
      totHeight += h + spacing;
      
      y += h + spacing;
    }
  }
}
