package com.naxanria.mappy.map.waypoint;

import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.util.MathUtil;
import net.minecraft.client.MainWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

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
  
    List<WayPoint> wayPoints = manager.getWaypointsToRender(client.world.dimension.getType().getId());
    for (WayPoint wp :
      wayPoints)
    {
      renderer.renderWaypoint(wp, client.player);
    }
  }
  
  private void renderWaypoint(WayPoint wayPoint, PlayerEntity player)
  {
    Vec3d vec = player.getPositionVec();
    float dx = (float) Math.abs(wayPoint.pos.getX() - vec.x);
    float dy = (float) Math.abs(wayPoint.pos.getZ() - vec.z);
    
    float viewAngle = Math.abs(player.cameraYaw % 360);
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
  
    MainWindow mainWindow = client.func_228018_at_();
    float scale = angle / (viewFov);
    int size = 12;
    int x = (int) MathUtil.clamp(mainWindow.getScaledWidth() * scale - size / 2, 0, mainWindow.getScaledWidth() - size);
    int y = 4;
    
    diamond(x, y, size, size, wayPoint.color);
    
    int dist = (int) MathUtil.getDistance(wayPoint.pos, player.getPosition());
    
    drawStringCenteredBound(client.fontRenderer, dist + "m", x + size / 2, y, 0, mainWindow.getScaledWidth(), WHITE);
    
//    BeaconBlockEntityRenderer.renderLightBeam(-0.43, -2.66, -0.55, delta, 1, 39361, 0, 1024, ColorUtil.toFloats(wayPoint.color), 0.2, 0.25);
  }
}
