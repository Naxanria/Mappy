package com.naxanria.mappy.util;

public class BiInteger extends BiValue<Integer, Integer>
{
  public BiInteger(Integer a, Integer b)
  {
    super(a, b);
  }
  
  @Override
  public boolean equals(Object o)
  {
    return super.equals(o);
  }
  
  @Override
  public int hashCode()
  {
    return A.hashCode() ^ B.hashCode();
  }
}
