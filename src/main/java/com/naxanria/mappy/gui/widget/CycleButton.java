package com.naxanria.mappy.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.naxanria.mappy.gui.DrawPosition;
import com.naxanria.mappy.util.ArrayUtil;
import com.naxanria.mappy.util.EnumUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import org.apache.commons.lang3.EnumUtils;

import java.util.function.Consumer;

public class CycleButton extends Widget
{
  private final FontRenderer font = Minecraft.getInstance().fontRenderer;
  
  protected final String[] values;
  protected int[] colors;
  protected int index = 0;
  
  protected Consumer<CycleButton> onChangeCallback;
  
  public CycleButton(int xIn, int yIn, String... values)
  {
    this(xIn, yIn, 0, values);
  }
  
  public CycleButton(int xIn, int yIn, int index, String... values)
  {
    super(xIn, yIn, "");
    
    this.values = values;
  
    width = 20;
    height = 20;
    
    for (String value :
      values)
    {
      width = Math.max(width, font.getStringWidth(value) + 4);
    }
    
    this.index = index;
  }
  
  public CycleButton setColors(int... colors)
  {
    this.colors = colors;
    return this;
  }
  
  public CycleButton setOnChangeCallback(Consumer<CycleButton> onChangeCallback)
  {
    this.onChangeCallback = onChangeCallback;
    return this;
  }
  
  @Override
  public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_)
  {
    Minecraft minecraft = Minecraft.getInstance();
    FontRenderer fontrenderer = minecraft.fontRenderer;
    minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
    int i = this.getYImage(this.isHovered());
    GlStateManager.enableBlend();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
    this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
    this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
//    int j = getFGColor();
  
    String val = values[index];
    
    this.drawCenteredString(fontrenderer, val, this.x + this.width / 2, this.y + (this.height - 8) / 2, colors == null ? 0xffffffff : colors[index % colors.length]);
  }
  
  @Override
  public void onClick(double p_onClick_1_, double p_onClick_3_)
  {
    index = ++index % values.length;
    
    if (onChangeCallback != null)
    {
      onChangeCallback.accept(this);
    }
  }
  
  public String getValue()
  {
    return values[index];
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public CycleButton setIndex(int index)
  {
    this.index = index;
    return this;
  }
  
  public static class Boolean extends CycleButton
  {
    public Boolean(int x, int y)
    {
      this(x, y, true);
    }
    
    public Boolean(int x, int y, boolean value)
    {
      super(x, y, value ? 0 : 1, "True", "False");
      setColors(0xff00ff00, 0xff880000);
    }
    
    public Boolean set(boolean value)
    {
      index = value ? 0 : 1;
      return this;
    }
    
    public boolean get()
    {
      return getIndex() == 0;
    }
  
    @Override
    public CycleButton.Boolean setOnChangeCallback(Consumer<CycleButton> onChangeCallback)
    {
      super.setOnChangeCallback(onChangeCallback);
      
      return this;
    }
  }
  
  public static class EnumButton<T extends Enum<T>> extends CycleButton
  {
    private final T[] values;
    private final T value;
    
    public EnumButton(int xIn, int yIn, T value)
    {
      this(xIn, yIn, value.ordinal(), value);
    }
  
    public EnumButton(int xIn, int yIn, int index, T value)
    {
      super(xIn, yIn, index, EnumUtil.getValueNames(value));
      this.value = value;
      values = EnumUtil.getValues(value);
    }
    
    public T get()
    {
      return values[index];
    }
  
    @Override
    public EnumButton<T> setOnChangeCallback(Consumer<CycleButton> onChangeCallback)
    {
      super.setOnChangeCallback(onChangeCallback);
      
      return this;
    }
  }
}
