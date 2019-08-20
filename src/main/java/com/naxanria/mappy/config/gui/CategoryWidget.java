package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.config.ConfigCategoryNode;
import com.naxanria.mappy.gui.DrawableHelperBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CategoryWidget extends DrawableHelperBase implements IRenderable, INestedGuiEventHandler
{
  protected static final FontRenderer font = Minecraft.getInstance().fontRenderer;
  
  protected final List<IGuiEventListener> children = new ArrayList<>();
  protected IGuiEventListener focus = null;
  protected boolean dragging = false;
  
  public int x;
  public int y;
  public final int height = 20;
  public final int width;
  protected ConfigCategoryNode node;
  protected ConfigGui gui;
  
  protected List<GuiButtonExt> categoryButtons = new ArrayList<>();
  
  protected final int currentDepth;
  
  public CategoryWidget(int x, int y, ConfigCategoryNode node, ConfigGui gui)
  {
    this.x = x;
    this.y = y;
    this.node = node;
    this.gui = gui;
    
    String full = node.getFullName(false);
    String[] cats = full.split("\\.");
    
    int xp = x;
    int tw = 0;
    
    int depth = 0;
    for (String name :
      cats)
    {
      int w = font.getStringWidth(name) + 8;
      int d = cats.length - 1 - depth;
      GuiButtonExt b = new GuiButtonExt(xp, y, w, height, name, button -> clicked(d));
      if (d == 0)
      {
        b.active = false;
      }
      
      depth++;
      categoryButtons.add(b);
      xp += w + 1;
      tw += w + 1;
    }
    currentDepth = depth;
    width = tw;
    
    children.addAll(categoryButtons);
  }
  
  private void clicked(int depth)
  {
    ConfigCategoryNode node = this.node;
    if (node.isTop())
    {
      return;
    }
    
    if (depth == 1)
    {
      node = node.pop();
    }
    else
    {
      for (int i = 0; i < depth; i++)
      {
        node = node.pop();
      }
    }
    
    gui.setNode(node);
//    Mappy.LOGGER.info("Clicked! Depth: " + depth + "[" + currentDepth + "]");
  }
  
  @Override
  public List<? extends IGuiEventListener> children()
  {
    return children;
  }
  
  @Override
  public boolean isDragging()
  {
    return dragging;
  }
  
  @Override
  public void setDragging(boolean dragging)
  {
    this.dragging = dragging;
  }
  
  @Nullable
  @Override
  public IGuiEventListener getFocused()
  {
    return focus;
  }
  
  @Override
  public void setFocused(@Nullable IGuiEventListener newFocus)
  {
    focus = newFocus;
  }
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    for (IGuiEventListener child :
      children)
    {
      if (child instanceof IRenderable)
      {
        ((IRenderable) child).render(mouseX, mouseY, partialTicks);
      }
    }
  }
}
