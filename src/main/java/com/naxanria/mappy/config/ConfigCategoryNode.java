package com.naxanria.mappy.config;

import com.google.common.base.Preconditions;
import com.naxanria.mappy.config.gui.ConfigGuiEntry;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigCategoryNode
{
  private String name;
  private String translationKey = null;
  private ConfigCategoryNode parent;
  private Map<String, ConfigCategoryNode> children = new HashMap<>();
  private List<ConfigGuiEntry<?, ?>> entries = new ArrayList<>();
  
  private ConfigCategoryNode(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getFullName()
  {
    return getFullName(true);
  }
  
  public String getFullName(boolean skipTOP)
  {
    if (isTop())
    {
      return skipTOP ? "" : name;
    }
    
    String full = parent != null ? parent.getFullName(skipTOP) : "";
    
    return full.equals("") ? name : full + "." + name;
  }
  
  public String getTranslation()
  {
    return translationKey == null ? I18n.format(name) : I18n.format(translationKey);
  }
  
  public ConfigCategoryNode setTranslationKey(String translationKey)
  {
    this.translationKey = translationKey;
    return this;
  }
  
  public ConfigCategoryNode getParent()
  {
    return parent;
  }
  
  public List<String> getChildren()
  {
    return new ArrayList<>(children.keySet());
  }
  
  public ConfigCategoryNode getChild(String name)
  {
    if (children.containsKey(name))
    {
      return children.get(name);
    }
    
    throw new IllegalArgumentException("No such child found: " + name);
  }
  
  public ConfigCategoryNode push(String name)
  {
    Preconditions.checkArgument(!children.containsKey(name), "Category " + getFullName(true) + " already exists");
    
    ConfigCategoryNode node = new ConfigCategoryNode(name);
    children.put(name, node);
    node.parent = this;
    
    return node;
  }
  
  public ConfigCategoryNode pop()
  {
    Preconditions.checkArgument(!isTop(), "Can't pop the top category node!");
    
    return parent;
  }
  
  public ConfigCategoryNode add(ConfigGuiEntry<?, ?> entry)
  {
    entries.add(entry);
    
    return this;
  }
  
  public boolean isTop()
  {
    return parent == null;
  }
  
  public ConfigCategoryNode getTop()
  {
    return isTop() ? this : parent.getTop();
  }
  
  public List<ConfigGuiEntry<?, ?>> getEntries()
  {
    return entries;
  }
  
  public static ConfigCategoryNode create()
  {
    return new ConfigCategoryNode("TOP");
  }
}
