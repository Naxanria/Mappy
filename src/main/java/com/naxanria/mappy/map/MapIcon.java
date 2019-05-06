package com.naxanria.mappy.map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.config.Config;
import com.naxanria.mappy.map.waypoint.WayPoint;
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
      boolean alpha = Config.instance.alphaFeatures();
  
      int size = (self) ? 4 : PlayerHeadIcon.HEAD_SIZE;
  
      int drawX = mapX + x - size / 2;
      int drawY = mapY + y - size / 2;
  
      if (self)
      {
        fill(drawX, drawY, drawX + size, drawY + size, PLAYER_SELF);
        
        if (alpha)
        {
          int l = 4;
          double angle = Math.toRadians((player.headYaw + 90) % 360);
          line(drawX + size / 2, drawY + size / 2, (int) (drawX + size / 2 + Math.cos(angle) * l), (int) (drawY + size / 2 + Math.sin(angle) * l), RED);
        }
      }
      else
      {
        if (Config.instance.showPlayerHeads())
        {
          PlayerHeadIcon.drawHead(player, drawX, drawY);
        }
        else
        {
          fill(drawX, drawY, drawX + size, drawY + size, PLAYER_OTHER);
        }
      }
      
      if (!self && Config.instance.showPlayerNames())
      {
        drawStringCenteredBound(client.textRenderer, player.getName().getString(), drawX + size / 2, drawY - size / 2 - 10, 0, client.window.getScaledWidth(), WHITE);
      }
    }
  }
  
  public static class Waypoint extends MapIcon<Waypoint>
  {
    private WayPoint wayPoint;
    public Waypoint(Map map, WayPoint wayPoint)
    {
      super(map);
      this.wayPoint = wayPoint;
    }
  
    @Override
    public void draw(int mapX, int mapY)
    {
      int size = 8;
      int col = wayPoint.color;
      
      int drawX = mapX + x - size / 2;
      int drawY = mapY + y - size / 2;
  
      GlStateManager.pushMatrix();
      diamond(drawX, drawY, size, size, col);
      GlStateManager.popMatrix();
    }
  }
}
