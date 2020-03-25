package com.naxanria.mappy.gui.widget;

import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.map.waypoint.IconType;
import com.naxanria.mappy.map.waypoint.WayPoint;
import com.naxanria.mappy.map.waypoint.WayPointEditor;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

import java.util.List;

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
  
    List<String> iconNames = IconType.getIconNames();
    int i = iconNames.indexOf(wayPoint.iconType.name);
    if (i < 0)
    {
      i = 0;
    }
    selectedIndex = i;
  }
  
  @Override
  public void render(int mouseX, int mouseY, float p_render_3_)
  {
//    super.render(p_render_1_, p_render_2_, p_render_3_);
    
    int col = editor.getPreviewColor();
    int hoveredIndex = getMouseOverIndex(mouseX, mouseY);
    List<String> iconNames = IconType.getIconNames();
    for (int i = 0; i < iconNames.size(); i++)
    {
      int xp = x + spacing * i + spacing / 2;
      int yp = y + 5;
//      int col = wayPoint.color;
      if (i == selectedIndex || i == hoveredIndex)
      {
        DrawableHelperBase.rect(xp - 5, yp - 5, height, selectedBoxColor);
      }
  
      IconType.getIcon(iconNames.get(i)).draw(xp - 4, yp - 4, col);
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
      return MathHelper.clamp((mouseX - x) / spacing, 0, IconType.getIconNames().size() - 1);
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
    return IconType.getIcon(IconType.getIconNames().get(selectedIndex));
  }
}
