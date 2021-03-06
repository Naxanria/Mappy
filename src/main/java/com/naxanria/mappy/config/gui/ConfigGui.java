package com.naxanria.mappy.config.gui;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.google.common.base.Strings;
import com.naxanria.mappy.config.ConfigCategoryNode;
import com.naxanria.mappy.gui.ScreenBase;
import com.naxanria.mappy.config.MappyConfig;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;


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
  private List<ExtendedButton> subCategoryButtons = new ArrayList<>();
  private ExtendedButton saveButton;
  private ExtendedButton cancelButton;
  
  
  public ConfigGui(Screen parent)
  {
    super(new StringTextComponent("Config"), parent);
  
    spec = MappyConfig.getSpec();
    MappyConfig.Client config = MappyConfig.getConfig();
  
    currentCategory = categories.push("General");
    
    addEntry(config.mapSize)
      .tooltip.addInfo("The size of the map.").range(16, 256).def(128);
    addEntry(config.offset)
      .tooltip.addInfo("Offset of the map").range(0, 8).def(4);
    addEntry(config.drawPosition)
      .tooltip.addInfo("The position of the map").def("TOP_RIGHT");
    
    currentCategory = currentCategory.push("Look and Feel");
    addEntry(config.moveMapForEffects)
      .tooltip.addInfo("Should the map move if there are potion effects.").def(true);
    addEntry(config.shaded)
      .tooltip.addInfo("Shade the map.").def(true);
    addEntry(config.shadeStrength)
      .tooltip.addInfo("Strength of the shading, the lower, the stronger.").range(2, 16).def(10);
    addEntry(config.drawChunkGrid)
      .tooltip.addInfo("The chunk grid").def(false);
    addEntry(config.biomeBlending)
      .tooltip.addInfo("Biome blending").def(true);
    
    // still WIP so not showing here for now.
//    addEntry(MappyConfig.config.scale);
    addEntry(config.showInChat)
      .tooltip.addInfo("Show the map while chat is open").def(true);
    
    currentCategory = currentCategory.pop().push("Waypoints");
    addEntry(config.chatButton);
    lastEntry.tooltip.addInfo("Show button to print the waypoint to chat");
    addEntry(config.teleportButton);
    lastEntry.tooltip.addInfo("Show button to teleport to waypoint");
    
    currentCategory = currentCategory.pop().push("Death");
    addEntry(config.createDeathWayPoints);
    lastEntry.tooltip.addInfo("Create a way point on death").def(true);
    addEntry(config.printDeathPointInChat);
    lastEntry.tooltip.addInfo("Print the death position into your chat.").def(false);
    addEntry(config.autoRemoveDeathWaypoint);
    lastEntry.tooltip.addInfo("Remove the death-waypoint when you are close").def(true);
    addEntry(config.autoRemoveRange);
    lastEntry.tooltip.addInfo("Distance for when the death-waypoint will be automatically removed.").def(5);
    
    currentCategory = currentCategory.getTop().push("Info");
    addEntry(config.showPosition)
      .tooltip.addInfo("Show the current position").def(true);
    addEntry(config.showFPS)
      .tooltip.addInfo("Show the current FPS").def(false);
    addEntry(config.showBiome)
      .tooltip.addInfo("Show biome name").def(true);
    addEntry(config.showTime)
      .tooltip.addInfo("Show in game time").def(true);
    addEntry(config.showDirection)
      .tooltip.addInfo("Show current direction").def(false);
    
    currentCategory = currentCategory.push("Map");
    addEntry(config.showPlayerNames)
      .tooltip.addInfo("Show player names of other players").def(true);
    addEntry(config.showPlayerHeads)
      .tooltip.addInfo("Show the player heads instead of dots").def(true);
    addEntry(config.showEntities)
      .tooltip.addInfo("Show entities nearby").def(true);
    
    currentCategory = currentCategory.getTop().push("Optimization");
    addEntry(config.updatePerCycle)
      .tooltip.addInfo("How many chunks on the map to update per tick").range(1, 100).def(10);
//    addEntry(MappyConfig.config.pruneDelay)
//      .tooltip.addInfo("Delay before pruning cached chunks (in seconds)").range(20, 600).def(60);
//    addEntry(MappyConfig.config.pruneAmount)
//      .tooltip.addInfo("The max amount of chunks to prune from the cache").range(100, 6000).def(1500);
    addEntry(config.forceHeightmapUse)
      .tooltip.addInfo("Forces use of heightmap for height checking,").addInfo("this is more performing but can be less accurate").def(true);
    
    if (config.showItemConfigInGame.get())
    {
      currentCategory = currentCategory.getTop().push("Items");
      addEntry(config.inHotBar)
        .tooltip.addInfo("Require the items to be in the hotbar").def(false);
      addEntry(config.mapItem)
        .tooltip.addInfo("The item required for showing the map");
      addEntry(config.positionItem)
        .tooltip.addInfo("The item required for showing current position");
      addEntry(config.biomeItem)
        .tooltip.addInfo("The item required for showing current biome");
      addEntry(config.timeItem)
        .tooltip.addInfo("The item required for showing current in game time");
    }
    
    currentCategory = currentCategory.getTop();
    
    String s = I18n.format("mappy.gui.save");
    int w = font.getStringWidth(s);
    saveButton = new ExtendedButton(8, 0, w + 8, 20, s, this::save);
    
    s = I18n.format("mappy.gui.cancel");
    w = font.getStringWidth(s);
    cancelButton = new ExtendedButton(8 + saveButton.getWidth(), 0, w + 8, 20, s, this::cancel);
    
    setupCategory();
  }
  
  @Override
  public void init()
  {
    MainWindow mainWindow = minecraft.getMainWindow();
    windowWidth = mainWindow.getScaledWidth();
    windowHeight = mainWindow.getScaledHeight();

    children.clear();
  
    int x = 8;
    for (String subCat : subCategories)
    {
      if (subCat.equals("Hidden"))
      {
        continue;
      }
      
      int w = font.getStringWidth(subCat) + 8;
      ExtendedButton subButton = new ExtendedButton(x, 22, w, 20, subCat, this::subCat);
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
    tooltip = entry.tooltip;
    
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
    
    for (ExtendedButton button : subCategoryButtons)
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
    MappyConfig.getSpec().save();
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
