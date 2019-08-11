package com.naxanria.mappy.util;

import java.util.List;

public class StringUtil
{
  public static String combine(List<String> path, String joiner)
  {
    StringBuilder combined = new StringBuilder();
    for (int i = 0; i < path.size(); i++)
    {
      combined.append(path.get(i));
      if (i < path.size() - 1)
      {
        combined.append(joiner);
      }
    }
    
    return combined.toString();
  }
}
