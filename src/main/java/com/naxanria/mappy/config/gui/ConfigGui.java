package com.naxanria.mappy.config.gui;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.common.base.Strings;
import com.naxanria.mappy.config.ConfigCategoryNode;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.config.MappyConfig;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigGui extends ScreenBase
{
  private List<ConfigGuiEntry<?, ?>> entries = new ArrayList<>();
  private List<String> keys = new ArrayList<>();
  private ForgeConfigSpec spec;
  
  private GuiTooltip tooltip = null;
  private ConfigGuiEntry<?, ?> lastEntry;
  
  private Map<ConfigCategoryNode, CategoryWidget> widgetMap = new HashMap<>();
  private ConfigCategoryNode categories = ConfigCategoryNode.create();
  private ConfigCategoryNode currentCategory;
  
  private List<String> subCategories;
  
  private CategoryWidget categoryWidget;
  private List<GuiButtonExt> subCategoryButtons = new ArrayList<>();
  private GuiButtonExt saveButton;
  private GuiButtonExt cancelButton;
  
  
  public ConfigGui(Screen parent)
  {
    super(new StringTextComponent("Config"), parent);
  
    spec = MappyConfig.getSpec();
    
    currentCategory = categories.push("General");
    
    addEntry(MappyConfig.config.mapSize);
    lastEntry.tooltip.addInfo("The size of the map.").range(16, 256).def(64);
    addEntry(MappyConfig.config.offset);
    lastEntry.tooltip.addInfo("Offset of the map").range(0, 8).def(4);
    addEntry(MappyConfig.config.drawPosition);
    lastEntry.tooltip.addInfo("The position of the map").def("TOP_RIGHT");
    
    currentCategory = currentCategory.push("Look and Feel");
    addEntry(MappyConfig.config.moveMapForEffects);
    addEntry(MappyConfig.config.shaded);
    addEntry(MappyConfig.config.shadeStrength);
    addEntry(MappyConfig.config.drawChunkGrid);
    // still WIP so not showing here for now.
//    addEntry(MappyConfig.config.scale);
    addEntry(MappyConfig.config.showInChat);
    
    currentCategory = currentCategory.pop().push("Death");
    addEntry(MappyConfig.config.createDeathWayPoints);
    lastEntry.tooltip.addInfo("Create a way point on death").def(true);
    addEntry(MappyConfig.config.printDeathPointInChat);
    lastEntry.tooltip.addInfo("Print the death position into your chat.").def(false);
    addEntry(MappyConfig.config.autoRemoveDeathWaypoint);
    lastEntry.tooltip.addInfo("Remove the death-waypoint when you are close").def(true);
    addEntry(MappyConfig.config.autoRemoveRange);
    lastEntry.tooltip.addInfo("Distance for when the death-waypoint will be automatically removed.").def(5);
    
    currentCategory = currentCategory.getTop().push("Info");
    addEntry(MappyConfig.config.showPosition);
    addEntry(MappyConfig.config.showFPS);
    addEntry(MappyConfig.config.showBiome);
    addEntry(MappyConfig.config.showTime);
    addEntry(MappyConfig.config.showDirection);
    
    currentCategory = currentCategory.push("Map");
    addEntry(MappyConfig.config.showPlayerNames);
    addEntry(MappyConfig.config.showPlayerHeads);
    addEntry(MappyConfig.config.showEntities);
    
    currentCategory = currentCategory.getTop().push("Optimization");
    addEntry(MappyConfig.config.updatePerCycle);
    addEntry(MappyConfig.config.pruneDelay);
    addEntry(MappyConfig.config.pruneAmount);
    addEntry(MappyConfig.config.forceHeightmapUse);
    
    if (MappyConfig.showItemConfigInGame)
    {
      currentCategory = currentCategory.getTop().push("Items");
      addEntry(MappyConfig.config.inHotBar);
      addEntry(MappyConfig.config.mapItem);
      addEntry(MappyConfig.config.positionItem);
      addEntry(MappyConfig.config.biomeItem);
      addEntry(MappyConfig.config.timeItem);
    }
    
    currentCategory = currentCategory.getTop();
    
    String s = I18n.format("mappy.gui.save");
    int w = font.getStringWidth(s);
    saveButton = new GuiButtonExt(8, 0, w + 8, 20, s, this::save);
    
    s = I18n.format("mappy.gui.cancel");
    w = font.getStringWidth(s);
    cancelButton = new GuiButtonExt(8 + saveButton.getWidth(), 0, w + 8, 20, s, this::cancel);
    
    setupCategory();
  }
  
  @Override
  public void init()
  {
    windowWidth = minecraft.mainWindow.getScaledWidth();
    windowHeight = minecraft.mainWindow.getScaledHeight();

    children.clear();
  
    int x = 8;
    for (String subCat : subCategories)
    {
      if (subCat.equals("Hidden"))
      {
        continue;
      }
      
      int w = font.getStringWidth(subCat) + 8;
      GuiButtonExt subButton = new GuiButtonExt(x, 22, w, 20, subCat, this::subCat);
      x += w + 1;
      subCategoryButtons.add(subButton);
    }
    
    children.addAll(subCategoryButtons);
    children.addAll(entries);
    
    if (categoryWidget != null)
    {
      children.add(categoryWidget);
    }
    
    saveButton.y = windowHeight - 22 + 2;
    cancelButton.y = saveButton.y;
    
    children.add(saveButton);
    children.add(cancelButton);
  }
  
  private void subCat(Button b)
  {
    setNode(currentCategory.getChild(b.getMessage()));
  }
  
  public void setupCategory()
  {
    entries.clear();
    subCategoryButtons.clear();
    
    subCategories = currentCategory.getChildren();
    entries.addAll(currentCategory.getEntries());
    
    if (!widgetMap.containsKey(currentCategory))
    {
      categoryWidget = new CategoryWidget(8, 2, currentCategory, this);
      widgetMap.put(currentCategory, categoryWidget);
    }
    else
    {
      categoryWidget = widgetMap.get(currentCategory);
    }
    
    init();
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
    lastEntry = entry;
    
    currentCategory.add(entry);
    
    return this;
  }
  
  @Override
  public void renderBackground()
  {
    renderDirtBackground(0);
  }
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    
    renderBackground();
    
    renderEntries();
    renderTop();
    renderBottom();
    
    renderForeground();
  }
  
  private void renderEntries()
  {
    int x = 10;
    int y = 48;
    int width = windowWidth - x - 20;
    int scroll = 0;
    int spacing = 2;
  
    int totHeight = 0;
    
    fill(0, 45, windowWidth, windowHeight - 22, 0xaa000000);
  
    tooltip = null;
    
    for (ConfigGuiEntry<?, ?> entry :
      entries)
    {
      entry.width = width;
      entry.setPosition(x, y);
      int h = entry.height;
      totHeight += h + spacing;
    
      y += h + spacing;

      entry.render(mouseX, mouseY, 0);
  
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
  
  private void renderBottom()
  {
    int h = 22;
//    fill(0, windowHeight - h, windowWidth, windowHeight, 0x66373737);
    
    saveButton.renderButton(mouseX, mouseY, 0);
    cancelButton.renderButton(mouseX, mouseY, 0);
  }
  
  private void renderTop()
  {
//    fill(0, 0, windowWidth, 45, 0x66373737);
    
    if (categoryWidget != null)
    {
      categoryWidget.render(mouseX, mouseY, 0);
    }
    
    for (GuiButtonExt button : subCategoryButtons)
    {
      button.renderButton(mouseX, mouseY, 0);
    }
  }
  
  @Override
  public void renderPreChildren()
  {
    tooltip = null;
  }
  
  @Override
  public void renderForeground()
  {
    if (tooltip != null)
    {
      tooltip.render(tooltip.x, tooltip.y);
    }
  }
  
  public void setNode(ConfigCategoryNode node)
  {
    this.currentCategory = node;
    setupCategory();
    
  }
  
  private void save(Button button)
  {
    entries.forEach(ConfigGuiEntry::save);
    
    onClose();
  }
  
  private void cancel(Button button)
  {
    onClose();
  }
  
  public static class Builder
  {
    private final Screen parentScreen;
    private final ConfigCategoryNode categories = ConfigCategoryNode.create();
  
    public Builder(Screen parentScreen)
    {
      this.parentScreen = parentScreen;
    }
  
    public static Builder create(Screen parenTScreen)
    {
      return new Builder(parenTScreen);
    }
    
    public ConfigGui build()
    {
      return new ConfigGui(parentScreen);
    }
  }
}
