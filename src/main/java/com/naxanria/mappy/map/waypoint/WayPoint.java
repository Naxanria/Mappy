package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.util.MathUtil;
import com.naxanria.mappy.util.Serializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class WayPoint implements Serializable<WayPoint>
{
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
  
  public boolean show()
  {
    return !hidden && (showAlways || MathUtil.getDistance(pos, MinecraftClient.getInstance().player.getBlockPos(), true) <= showRange);
  }
  
  @Override
  public WayPoint writeToNBT(CompoundTag tag)
  {
    if (tag != null)
    {
      tag.putString("name", name);
      tag.put("pos", TagHelper.serializeBlockPos(pos));
      tag.putInt("dimension", dimension);
      tag.putInt("color", color);
      tag.putBoolean("showAlways", showAlways);
      tag.putBoolean("hidden", hidden);
      tag.putInt("showRange", showRange);
    }
    
    return this;
  }
  
  @Override
  public WayPoint readFromNBT(CompoundTag tag)
  {
    if (tag != null)
    {
      name = tag.getString("name");
      pos = TagHelper.deserializeBlockPos((CompoundTag) tag.getCompound("pos"));
      dimension = tag.getInt("dimension");
      color = tag.getInt("color");
      showAlways = tag.getBoolean("showAlways");
      hidden = tag.getBoolean("hidden");
      showRange = tag.getInt("showRange");
    }
    
    return this;
  }

}
