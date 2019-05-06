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
  public String name = "";
  public BlockPos pos = new BlockPos(0, 0, 0);
  public int dimension;
  public int color;
  public boolean showAlways;
  public boolean hidden;
  public int showRange = 500;
  
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
