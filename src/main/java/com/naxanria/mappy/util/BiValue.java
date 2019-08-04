package com.naxanria.mappy.util;

import java.util.Objects;

public class BiValue<TA, TB>
{
  public final TA A;
  public final TB B;
  
  public BiValue(TA a, TB b)
  {
    A = a;
    B = b;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    BiValue<?, ?> biValue = (BiValue<?, ?>) o;
    return Objects.equals(A, biValue.A) &&
      Objects.equals(B, biValue.B);
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(A, B);
  }
}
