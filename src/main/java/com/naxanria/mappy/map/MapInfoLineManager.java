package com.naxanria.mappy.map;

import java.util.ArrayList;
import java.util.List;

public class MapInfoLineManager
{
  public enum Direction
  {
    UP,
    DOWN
  }
  
  private List<MapInfoLine> lines;
  private int x, y;
  private int spacing = 16;
  private Direction direction = Direction.DOWN;
  private Map map;
  
  public MapInfoLineManager(Map map)
  {
    lines = new ArrayList<>();
    this.map = map;
  }
  
  public void clear()
  {
    lines.clear();
  }
  
  public void draw()
  {
    int yp = y;
    if (direction == Direction.UP)
    {
      yp = y - spacing * lines.size();
    }
    
    for (MapInfoLine line :
      lines)
    {
      switch (line.alignment)
      {
        case Left:
          line.x = x;
          break;
        case Center:
          line.x = x + map.getSize() / 2;
          break;
        case Right:
          line.x = x + map.getSize();
          break;
      }
      
      line.y = yp;
      line.draw();
      
      yp += spacing;
    }
  }
  
  public void add(MapInfoLine line)
  {
    lines.add(line);
  }
  
  public MapInfoLineManager setSpacing(int spacing)
  {
    this.spacing = spacing;
    return this;
  }
  
  public MapInfoLineManager setPosition(int x, int y)
  {
    this.x = x;
    this.y = y;
    return this;
  }
  
  public MapInfoLineManager setDirection(Direction direction)
  {
    this.direction = direction;
    return this;
  }
}
