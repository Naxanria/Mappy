package com.naxanria.mappy.config.gui;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.common.base.Strings;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.config.MappyConfig;
import net.minecraft.client.gui.IGuiEventListener;
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
  
  private GuiTooltip tooltip = null;
  private ConfigGuiEntry<?, ?> lastEntry;
  
  @Override
  public void init()
  {
//    keys.clear();
    
    
    spec = MappyConfig.getSpec();
    
    if (entries.size() == 0)
    {
      addEntry(MappyConfig.config.mapSize);
      lastEntry.tooltip.addInfo("The size of the map.").line().range(16, 256).def("64");
      addEntry(MappyConfig.config.offset);
      lastEntry.tooltip.addInfo("Offset of the map").range(0, 8).def("4");
      addEntry(MappyConfig.config.drawPosition);
      lastEntry.tooltip.addInfo("The position of the map").def("TOP_RIGHT");
      addEntry(MappyConfig.config.showMap);
      lastEntry.tooltip.addInfo("If to show the map").def("True");
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
    lastEntry = entry;
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
    tooltip = null;
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
  
  @Override
  protected void processChild(int mouseX, int mouseY, float partialTicks, IGuiEventListener child)
  {
    super.processChild(mouseX, mouseY, partialTicks, child);
    
    if (child instanceof ConfigGuiEntry)
    {
      ConfigGuiEntry entry = (ConfigGuiEntry) child;
      
      if (tooltip == null)
      {
        tooltip = entry.getTooltip();
        if (tooltip != null)
        {
          tooltip.x = entry.x;
          tooltip.y = entry.y - tooltip.height - 2;
        }
      }
    }
  }
  
  @Override
  public void renderForeground()
  {
    if (tooltip != null)
    {
      tooltip.render(tooltip.x, tooltip.y);
    }
  }
}
