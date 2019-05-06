package com.naxanria.mappy.util;

import net.minecraft.nbt.CompoundTag;

public interface Serializable<T>
{
  T writeToNBT(CompoundTag tag);
  
  T readFromNBT(CompoundTag tag);
}
