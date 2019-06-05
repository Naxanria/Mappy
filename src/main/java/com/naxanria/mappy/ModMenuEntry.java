package com.naxanria.mappy;

import com.naxanria.mappy.client.DrawPosition;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.config.ConfigBase;
import com.naxanria.mappy.config.Settings;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.entries.*;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;

import java.util.Set;
import java.util.function.Function;

public class ModMenuEntry implements ModMenuApi
{
  private static final String RESET = "text.cloth.reset_value";
  
  @Override
  public String getModId()
  {
    return Mappy.MODID;
  }
  
  private Screen getScreen(Screen parent)
  {
    ConfigScreenBuilder builder = ConfigScreenBuilder.create(parent, "Mappy Config", this::saveConfig);
    
    ConfigScreenBuilder.CategoryBuilder general = builder.addCategory(lang("category.general"));
    general.addOption(new IntegerListEntry(lang("offset"), Settings.offset, RESET, () -> Settings.offset, (i) -> Settings.offset = i));
    general.addOption(new EnumListEntry<>(lang("draw_position"), DrawPosition.class, Settings.drawPosition, RESET, () -> Settings.drawPosition, (p) -> Settings.drawPosition = p));
    general.addOption(new IntegerListEntry(lang("map_size"), Settings.mapSize, RESET, () -> Settings.mapSize, (i) -> Settings.mapSize = i).setMinimum(32).setMaximum(1024));
//    general.addOption(new IntegerSliderEntry(lang("map_scale"), 1, 8, Settings.scale, RESET, () -> Settings.scale, (i) -> Settings.scale = i));
    general.addOption(new BooleanListEntry(lang("map_move"), Settings.moveMapForEffects, RESET, () -> Settings.moveMapForEffects, (b) -> Settings.moveMapForEffects = b));
    general.addOption(new BooleanListEntry(lang("show_in_chat"), Settings.showInChat, RESET, () -> Settings.showInChat, (b) -> Settings.showInChat = b));
    
    general.addOption(new BooleanListEntry(lang("shaded"), Settings.shaded, RESET, () -> Settings.shaded, (b) -> Settings.shaded = b));
    general.addOption(new IntegerSliderEntry(lang("shade_strength"),2, 16, 18 - Settings.maxDifference, RESET, () -> 18 - Settings.maxDifference, (i) -> Settings.maxDifference = 18 - i));
    
    
    ConfigScreenBuilder.CategoryBuilder mapInfo = builder.addCategory(lang("category.info"));
    mapInfo.addOption(new BooleanListEntry(lang("show_grid"), Settings.drawChunkGrid, RESET, () -> Settings.drawChunkGrid, (b) -> Settings.drawChunkGrid = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_position"), Settings.showPosition, RESET, () -> Settings.showPosition, (b) -> Settings.showPosition = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_biome"), Settings.showBiome, RESET, () -> Settings.showBiome, (b) -> Settings.showBiome= b));
    mapInfo.addOption(new BooleanListEntry(lang("show_fps"), Settings.showFPS, RESET, () -> Settings.showFPS, (b) -> Settings.showFPS = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_game_time"), Settings.showTime, RESET, () -> Settings.showTime, (b) -> Settings.showTime = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_direction"), Settings.showDirection, RESET, () -> Settings.showDirection, (b) -> Settings.showDirection = b));
    
    mapInfo.addOption(new BooleanListEntry(lang("show_player_names"), Settings.showPlayerNames, RESET, () -> Settings.showPlayerNames, (b) -> Settings.showPlayerNames = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_player_heads"), Settings.showPlayerHeads, RESET, () -> Settings.showPlayerHeads, (b) -> Settings.showPlayerHeads = b));
    mapInfo.addOption(new BooleanListEntry(lang("show_entities"), Settings.showEntities, RESET, () -> Settings.showEntities, (b) -> Settings.showEntities = b));
  
    ConfigScreenBuilder.CategoryBuilder optimization = builder.addCategory(lang("category.optimization"));
    optimization.addOption(new IntegerListEntry(lang("update_cycle"), Settings.updatePerCycle, RESET, () -> Settings.updatePerCycle, (i) -> Settings.updatePerCycle = i).setMinimum(1).setMaximum(1000));
    optimization.addOption(new IntegerListEntry(lang("prune_delay"), Settings.pruneDelay, RESET, () -> Settings.pruneDelay, (i) -> Settings.pruneDelay = i).setMinimum(10).setMaximum(600));
    optimization.addOption(new IntegerListEntry(lang("prune_amount"), Settings.pruneAmount, RESET, () -> Settings.pruneAmount, (i) -> Settings.pruneAmount = i).setMinimum(100).setMaximum(5000));
    
    if (Settings.showItemConfigInGame)
    {
      ConfigScreenBuilder.CategoryBuilder items = builder.addCategory(lang("category.items"));
      items.addOption(new TextListEntry(lang("item_description_name"), lang("item_description")));
      items.addOption(new BooleanListEntry(lang("item_in_hotbar"), Settings.inHotBar, RESET, () -> Settings.inHotBar, (b) -> Settings.inHotBar = b));
      items.addOption(new StringListEntry(lang("item_show_map"), Settings.mapItem, RESET, () -> Settings.mapItem, (s) -> Settings.mapItem = s));
      items.addOption(new StringListEntry(lang("item_show_position"), Settings.positionItem, RESET, () -> Settings.positionItem, (s) -> Settings.positionItem = s));
      items.addOption(new StringListEntry(lang("item_show_biome"), Settings.biomeItem, RESET, () -> Settings.biomeItem, (s) -> Settings.biomeItem = s));
      items.addOption(new StringListEntry(lang("item_show_time"), Settings.timeItem, RESET, () -> Settings.timeItem, (s) -> Settings.timeItem = s));
    }
    builder.setDoesConfirmSave(false);
    
    return builder.build();
  }
  
  private String lang(String key)
  {
    return I18n.translate("mappy.config." + key);
  }
  
  
  private void saveConfig(ConfigScreenBuilder.SavedConfig config)
  {
    Config.instance.save();
    System.out.println("Saved config");
    Mappy.map.onConfigChanged();
  }
  
  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory()
  {
    return (this::getScreen);
  }
}
