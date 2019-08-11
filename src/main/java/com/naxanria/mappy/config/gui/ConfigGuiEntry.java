package com.naxanria.mappy.config.gui;

import com.naxanria.mappy.Mappy;
import com.naxanria.mappy.gui.DrawableHelperBase;
import com.naxanria.mappy.gui.widget.TextWidget;
import com.naxanria.mappy.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConfigGuiEntry<CV, CT extends ForgeConfigSpec.ConfigValue<CV>> extends DrawableHelperBase implements IRenderable, INestedGuiEventHandler
{
  protected static final FontRenderer font = Minecraft.getInstance().fontRenderer;
  
  protected int width = 250;
  protected int height = 20;
  public int x, y;
  
  protected ForgeConfigSpec configSpec;
  protected final CT configValue;
  protected final ForgeConfigSpec.ValueSpec valueSpec;
  
  protected CV displayValue;
  protected final CV value;
  protected final CV def;
  protected String name;
  
  protected TextWidget nameWidget;
  protected GuiButtonExt resetDefaultButton;
  protected GuiButtonExt resetStartValueButton;
  
  protected IGuiEventListener focus = null;
  protected boolean dragging = false;
  protected final List<Widget> children = new ArrayList<>();
  
  protected GuiTooltip tooltip = new GuiTooltip();
  
  boolean hovered = false;
  
  public ConfigGuiEntry(ForgeConfigSpec configSpec, CT configValue)
  {
    this.configSpec = configSpec;
    this.configValue = configValue;
    valueSpec = configSpec.get(configValue.getPath());
    value = configValue.get();
    def = (CV) valueSpec.getDefault();
    displayValue = value;
    String key = valueSpec.getTranslationKey();
    name = (key != null) ? I18n.format(key) : StringUtil.combine(configValue.getPath(), ".");
    
    int bWidth = 45;
    resetDefaultButton = new GuiButtonExt(0, 0, bWidth, height, "default", this::resetToDefault);
    resetStartValueButton = new GuiButtonExt(0, 0, bWidth, height, "reset", this::resetToStartValue);
    nameWidget = new TextWidget(0, 0, name);
    
    init();
  }
  
  public void init()
  {
    children.clear();
    
    nameWidget.x = x + 2;
    nameWidget.y = y + height / 2 - nameWidget.getHeight() / 2;
    children.add(nameWidget);
    
    resetDefaultButton.y = y;
    resetDefaultButton.active = !isDefault();
    children.add(rightAlign(resetDefaultButton, width));
    
    resetStartValueButton.y = y;
    resetStartValueButton.active = isChanged();
    children.add(rightAlign(resetStartValueButton, resetDefaultButton, 1));
  }
  
  public void resetToDefault(Button ctx)
  {
    Mappy.LOGGER.info("DEFAULTS");
  }
  
  public void resetToStartValue(Button ctx)
  {
    Mappy.LOGGER.info("START VALUE");
  }
  
  public void save()
  {
    if (displayValue != value)
    {
      configValue.set(displayValue);
    }
  }
  
  public ConfigGuiEntry<CV, CT> setPosition(int x, int y)
  {
    this.x = x;
    this.y = y;
    
    init();
    
    return this;
  }
  
  protected Widget rightAlign(Widget w, int right)
  {
    w.x = right - w.getWidth();
    return w;
  }
  
  protected Widget rightAlign(Widget w, Widget right)
  {
    return rightAlign(w, right, 0);
  }
  
  protected Widget rightAlign(Widget w, Widget right, int spacing)
  {
    w.x = right.x - w.getWidth() - spacing;
    
    return w;
  }
  
  protected int getXRightAligned(Widget w, int right)
  {
    return right - w.getWidth();
  }
  
  protected int getXRightAligned(Widget w, Widget toAlignTo, int spacing)
  {
    return getXRightAligned(w, toAlignTo.x - spacing);
  }
  
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    hovered = isMouseOver(mouseX, mouseY);
    
    fill(x, y + 1, x + width, y + height - 1, 0xff888888);
//    drawString(font, name, x + 2, y + 3, 0xffffffff);
//    int right = resetStartValueButton.x - 2;
//    drawRightAlignedString(font, displayValue + " [" + def + "]", right, y + 3, 0xffffffff);
  
    for (Widget widget :
      children)
    {
      widget.render(mouseX, mouseY, partialTicks);
    }
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
    if (focus != null)
    {
      focus.changeFocus(false);
    }
    
    focus = newFocus;
  
    if (focus != null)
    {
      focus.changeFocus(true);
    }
  }
  
  public boolean isChanged()
  {
    return displayValue != value;
  }
  
  public boolean isDefault()
  {
    return displayValue == def;
  }
  
  public GuiTooltip getTooltip()
  {
    return  (hovered && !tooltip.isEmpty()) ? tooltip : null;
  }
  
  @Override
  public boolean isMouseOver(double mouseX, double mouseY)
  {
    return mouseX >= x && mouseX < x + width
      && mouseY >= y && mouseY < y + height;
  }
}
