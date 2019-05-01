package com.naxanria.mappy.map;

import com.naxanria.mappy.client.DrawableHelperBase;
import net.minecraft.entity.player.PlayerEntity;

public abstract class MapIcon<T extends MapIcon<T>> extends DrawableHelperBase
{
  protected Map map;
  public int x, y;
  
  public MapIcon(Map map)
  {
    this.map = map;
  }
  
  public T setPosition(int x, int y)
  {
    this.x = x;
    this.y = y;
    
    return (T) this;
  }
  
  public static int getScaled(int val, int startVal, int endVal, int range)
  {
    return (int) (((val - startVal) / ((float) (endVal - startVal))) * range);
  }
  
  public abstract void draw(int mapX, int mapY);
  
  public static class Player extends MapIcon<Player>
  {
    public static final int PLAYER_SELF = GREEN;
    public static final int PLAYER_OTHER = 0xff009933;
    
    protected boolean self;
    protected PlayerEntity player;
    
    public Player(Map map, PlayerEntity player, boolean self)
    {
      super(map);
      this.self = self;
      this.player = player;
    }
  
    @Override
    public void draw(int mapX, int mapY)
    {
      int size = 2;
      int drawX = mapX + x - size / 2;
      int drawY = mapY + y - size / 2;
      
      fill(drawX, drawY, drawX + size, drawY + size, self ? PLAYER_SELF : PLAYER_OTHER);
      if (!self)
      {
        drawStringCenteredBound(client.textRenderer, player.getName().getString(), drawX + size / 2, drawY - size / 2 - 10, 0, client.window.getScaledWidth(), WHITE);
      }
    }
  }
}
