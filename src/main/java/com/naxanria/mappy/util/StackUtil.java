package com.naxanria.mappy.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class StackUtil
{
  public static boolean contains(PlayerInventory inventory, boolean inHotBar, String search)
  {
    
    if (inHotBar)
    {
      return contains(inventory.offHandInventory, search) || contains(inventory.mainInventory, search, 9);
    }
    
    return contains(inventory.offHandInventory, search) || contains(inventory.mainInventory, search);
  }
  
  public static boolean contains(List<ItemStack> stacks, String search, int end)
  {
    return contains(stacks, search, 0, end);
  }
  
  public static boolean contains(List<ItemStack> stacks, String search, int start, int end)
  {
    for (int i = start; i < end; i++)
    {
      ItemStack stack = stacks.get(i);
      if (search.equalsIgnoreCase(getIdString(stack)))
      {
        return true;
      }
    }
    
    return false;
  }
  
  public static boolean contains(List<ItemStack> stacks, String search)
  {
    for (ItemStack stack :
      stacks)
    {
      String id = getIdString(stack);
      if (search.equalsIgnoreCase(id))
      {
        return true;
      }
    }
    
    return false;
  }
  
  public static String getIdString(ItemStack stack)
  {
    if (stack.isEmpty())
    {
      return null;
    }
  
    ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
    String fullID = id.toString();
    
    if (stack.isDamaged())
    {
      fullID = fullID + "@" + stack.getDamage();
    }
    
    
    return fullID;
  }
}
