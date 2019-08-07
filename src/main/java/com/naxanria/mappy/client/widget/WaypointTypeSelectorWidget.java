package com.naxanria.mappy.client.widget;

import com.naxanria.mappy.client.DrawableHelperBase;
import com.naxanria.mappy.map.waypoint.IconType;
import com.naxanria.mappy.map.waypoint.WayPoint;
import com.naxanria.mappy.map.waypoint.WayPointEditor;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

public class WaypointTypeSelectorWidget extends Widget
{
  private final WayPoint wayPoint;
  private final WayPointEditor editor;
  
  private int selectedIndex = 0;
  private int selectedBoxColor = 0xffffffff;
  private int spacing = 20;
  
  public WaypointTypeSelectorWidget(int xIn, int yIn, String msg, WayPointEditor editor)
  {
    super(xIn, yIn, msg);
    
    height = 10;
    
    this.editor = editor;
    this.wayPoint = editor.getWaypoint();
    selectedIndex = wayPoint.iconType.ordinal();
  }
  
  @Override
  public void render(int mouseX, int mouseY, float p_render_3_)
  {
//    super.render(p_render_1_, p_render_2_, p_render_3_);
    
    int col = editor.getPreviewColor();
    int hoveredIndex = getMouseOverIndex(mouseX, mouseY);
    IconType[] types = IconType.values();
    for (int i = 0; i < types.length; i++)
    {
      int xp = x + spacing * i + spacing / 2;
      int yp = y + 5;
//      int col = wayPoint.color;
      if (i == selectedIndex || i == hoveredIndex)
      {
        DrawableHelperBase.rect(xp - 5, yp - 5, height, selectedBoxColor);
      }
      
      types[i].draw(xp, yp, col);
    }
  }
  
  @Override
  public void onClick(double mouseX, double mouseY)
  {
    int index = getMouseOverIndex((int) mouseX, (int) mouseY);
    if (index >= 0)
    {
      selectedIndex = index;
    }
  }
  
  private int getMouseOverIndex(int mouseX, int mouseY)
  {
    if (isInside(mouseX, mouseY))
    {
      return MathHelper.clamp((mouseX - x) / spacing, 0, IconType.values().length - 1);
    }
    
    return -1;
  }
  
  private boolean isInside(int mouseX, int mouseY)
  {
    return mouseX >= x && mouseX < x + width
      && mouseY >= y && mouseY < y + height;
  }
  
  public IconType getSelectedType()
  {
    return IconType.values()[selectedIndex];
  }
}
