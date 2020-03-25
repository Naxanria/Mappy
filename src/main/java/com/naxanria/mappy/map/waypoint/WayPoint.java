package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.Serializable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class WayPoint implements Serializable<WayPoint>
{
  private final String fallbackIcon = "diamond";
  
  private static final Map<Integer, String> convertMap = Util.make(new HashMap<>(), map ->
  {
    map.put(0, "diamond");
    map.put(1, "square");
    map.put(2, "triangle");
    map.put(3, "skull");
    map.put(4, "house");
  });
  
  public static final Integer[] WAYPOINT_COLORS = new Integer[]
    {
      0xFFFFFFFF, 0xFFFF0000, 0xFFFFFF00, 0xFF00FFFF, 0xFF00FF00,
      0xFF00AA00, 0xFF0000FF, 0xFFFF8800, 0xFFFF00FF, 0xFF000000,
      0xFFAA00AA, 0xFF888888, 0xFFFFAAAA, 0xFFAAFFAA, 0xFFAAAAFF
    };
  
  public String name = "";
  public BlockPos pos = new BlockPos(0, 0, 0);
  public int dimension;
  public int color;
  public boolean showAlways;
  public boolean hidden;
  public int showRange = 5000;
  public IconType iconType = IconType.DIAMOND;
  public boolean deathPoint = false;
  public boolean converted;
  
  public boolean show()
  {
    return !hidden && (showAlways || MathUtil.getDistance(pos, Minecraft.getInstance().player.getPosition(), true) <= showRange);
  }
  
  @Override
  public WayPoint writeToNBT(CompoundNBT tag)
  {
    if (tag != null)
    {
      tag.putString("name", name);
      tag.put("pos", NBTUtil.writeBlockPos(pos));
      tag.putInt("dimension", dimension);
      tag.putInt("color", color);
      tag.putBoolean("showAlways", showAlways);
      tag.putBoolean("hidden", hidden);
      tag.putInt("showRange", showRange);
      tag.putString("icon", iconType.name);
      tag.putBoolean("death", deathPoint);
    }
    
    return this;
  }
  
  @Override
  public WayPoint readFromNBT(CompoundNBT tag)
  {
    if (tag != null)
    {
      name = tag.getString("name");
      pos = NBTUtil.readBlockPos(tag.getCompound("pos"));
      dimension = tag.getInt("dimension");
      color = tag.getInt("color");
      showAlways = tag.getBoolean("showAlways");
      hidden = tag.getBoolean("hidden");
      showRange = tag.getInt("showRange");
      
      
      String icon = fallbackIcon;
      if (tag.contains("icon", Constants.NBT.TAG_INT))
      {
        icon = convertMap.getOrDefault(tag.getInt("icon"), fallbackIcon);
        converted = true;
      }
      else if (tag.contains("icon", Constants.NBT.TAG_STRING))
      {
        icon = tag.getString("icon");
      }
      iconType = IconType.getIcon(icon);
      
      deathPoint = tag.contains("death") && tag.getBoolean("death");
    }
    
    return this;
  }

}
