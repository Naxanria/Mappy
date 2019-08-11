package com.naxanria.mappy.map;

import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.config.MappyConfig;
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
//      boolean alpha = Config.instance.alphaFeatures();
  
      int size = (self) ? 4 : PlayerHeadIcon.HEAD_SIZE;
  
      int drawX = mapX + x - size / 2;
      int drawY = mapY + y - size / 2;
  
      if (self)
      {
        if (client.player != player)
        {
          player = client.player;
        }
        
        fill(drawX, drawY, drawX + size, drawY + size, PLAYER_SELF);
        
//        if (alpha)
//        {
          int l = 4;
          // player.cameraYaw
          double angle = Math.toRadians((player.rotationYaw + 90) % 360);
          line(drawX + size / 2, drawY + size / 2, (int) (drawX + size / 2 + Math.cos(angle) * l), (int) (drawY + size / 2 + Math.sin(angle) * l), RED);
//        }
      }
      else
      {
        if (MappyConfig.showPlayerHeads)
        {
          PlayerHeadIcon.drawHead(player, drawX, drawY);
        }
        else
        {
          fill(drawX, drawY, drawX + size, drawY + size, PLAYER_OTHER);
        }
      }
      
      if (!self && MappyConfig.showPlayerNames)
      {
        drawStringCenteredBound(client.fontRenderer, player.getName().getString(), drawX + size / 2, drawY - size / 2 - 10, 0, client.mainWindow.getScaledWidth(), WHITE);
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
      
      wayPoint.iconType.draw(drawX, drawY, col);
  
//      GlStateManager.pushMatrix();
//      diamond(drawX, drawY, size, size, col);
//      GlStateManager.popMatrix();
    }
  }
  
  public static class Entity extends MapIcon<Entity>
  {
  
    private final net.minecraft.entity.Entity entity;
    boolean hostile;
    
    public Entity(Map map, net.minecraft.entity.Entity entity, boolean hostile)
    {
      super(map);
      
      this.entity = entity;
      this.hostile = hostile;
    }
  
    @Override
    public void draw(int mapX, int mapY)
    {
      int size = 1;
      int col = (hostile) ? 0xFFFF8800 : 0xFFFFFF00;
  
//      int x1 = mapX + x - size / 2;
//      int y1 = mapY + y + size / 2;
//      int x2 = mapX + x + size / 2;
//      int y2 = mapY + y + size / 2;
//      int x3 = mapX + x;
//      int y3 = mapY + y;
//
      fill(mapX + x - size, mapY + y - size, mapX + x + size, mapY + y + size, col);
      //triangle(x1, y1, x2, y2, x3, y3, col);
    }
  }
}
