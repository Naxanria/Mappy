package com.naxanria.mappy.util;

import com.google.common.base.Preconditions;

import java.util.function.Function;

public class ArrayUtil
{
  public static <C, T> C[] convert(T[] in, C[] out, Function<T, C> converter)
  {
    Preconditions.checkArgument(in.length == out.length, "Input and output array are not the same size!");
    Preconditions.checkNotNull(converter, "Converter cannot be null!");
  
    for (int i = 0; i < in.length; i++)
    {
      out[i] = converter.apply(in[i]);
    }
    
    return out;
  }
}
