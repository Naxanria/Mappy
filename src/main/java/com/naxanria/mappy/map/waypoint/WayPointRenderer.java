package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.util.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class WayPointRenderer extends DrawableHelperBase
{
  private static WayPointRenderer renderer;
  
  private static WayPointManager manager = WayPointManager.INSTANCE;
  public static void render(float delta)
  {
    if (renderer == null)
    {
      renderer = new WayPointRenderer();
    }
//    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null || client.player == null || client.currentScreen != null)
    {
      return;
    }
  
    List<WayPoint> wayPoints = manager.getWaypointsToRender(client.world.dimension.getType().getRawId());
    for (WayPoint wp :
      wayPoints)
    {
      renderer.renderWaypoint(wp, client.player);
    }
  }
  
  private void renderWaypoint(WayPoint wayPoint, PlayerEntity player)
  {
    float dx = (float) Math.abs(wayPoint.pos.getX() - player.x);
    float dy = (float) Math.abs(wayPoint.pos.getZ() - player.z);
    
    float viewAngle = Math.abs(player.headYaw % 360);
    // todo: get actual field of view
    float viewFov = 90;
    float waypointAngle = ((float) (Math.atan2(dy, dx) * (180 / Math.PI)));
    
    float angleStart = viewAngle - viewFov / 2;
    float angleEnd = viewAngle + viewFov / 2;
//    if (angleStart > angleEnd)
//    {
//      float temp = angleEnd;
//      angleEnd = angleStart;
//      angleStart = temp;
//    }
    
    float angle = (waypointAngle % (angleEnd - angleStart)) + angleStart;
    
    float scale = angle / (viewFov);
    int size = 12;
    int x = (int) MathUtil.clamp(client.window.getScaledWidth() * scale - size / 2, 0, client.window.getScaledWidth() - size);
    int y = 4;
    
    diamond(x, y, size, size, wayPoint.color);
    
    int dist = (int) MathUtil.getDistance(wayPoint.pos, player.getBlockPos());
    
    drawStringCenteredBound(client.textRenderer, dist + "m", x + size / 2, y, 0, client.window.getScaledWidth(), WHITE);
    
//    BeaconBlockEntityRenderer.renderLightBeam(-0.43, -2.66, -0.55, delta, 1, 39361, 0, 1024, ColorUtil.toFloats(wayPoint.color), 0.2, 0.25);
  }
}
