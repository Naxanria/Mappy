package com.naxanria.mappy.util;

public class EnumUtil
{
  public static <T extends Enum<T>> T[] getValues(T t)
  {
    return t.getDeclaringClass().getEnumConstants();
  }
  
  public static <T extends Enum<T>> String[] getValueNames(T t)
  {
    T[] ts = getValues(t);
    String[] values = new String[ts.length];
    return ArrayUtil.convert(ts, values, Enum::toString);
  }
}
