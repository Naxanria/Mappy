package com.naxanria.mappy.util;

import java.util.Objects;

public class TriValue<TA, TB, TC>
{
  public final TA A;
  public final TB B;
  public final TC C;
  
  public TriValue(TA a, TB b, TC c)
  {
    A = a;
    B = b;
    C = c;
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
    TriValue<?, ?, ?> triValue = (TriValue<?, ?, ?>) o;
    return Objects.equals(A, triValue.A) &&
      Objects.equals(B, triValue.B) &&
      Objects.equals(C, triValue.C);
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(A, B, C);
  }
}
