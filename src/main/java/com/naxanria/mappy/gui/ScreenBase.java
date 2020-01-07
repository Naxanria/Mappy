package com.naxanria.mappy.gui;

import com.mojang.blaze3d.platform.GlStateManager;
//import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import org.lwjgl.opengl.GL11;

public class ScreenBase extends Screen
{
  public final Screen parent;
  protected int mouseX, mouseY;
  protected int windowWidth, windowHeight;
  
  protected ScreenBase(TextComponent title)
  {
    this(title, null);
  }
  
  public ScreenBase(TextComponent title, Screen parent)
  {
    super(title);
    this.parent = parent;
    minecraft = Minecraft.getInstance();
    font = minecraft.fontRenderer;
  }
  
  @Override
  public void render(int mouseX, int mouseY, float partialTicks)
  {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    MainWindow mainWindow = minecraft.func_228018_at_();
    windowWidth = mainWindow.getScaledWidth();
    windowHeight = mainWindow.getScaledHeight();
    
    renderPre();
    
    renderBackground();
    
    renderPreChildren();
  
    processChildren(mouseX, mouseY, partialTicks);
  
    renderPostChildren();
    
    renderForeground();
    
    renderPost();
  }
  
  protected void processChildren(int mouseX, int mouseY, float partialTicks)
  {
    for (IGuiEventListener listener :
      children)
    {
      processChild(mouseX, mouseY, partialTicks, listener);
    }
  }
  
  protected void processChild(int mouseX, int mouseY, float partialTicks, IGuiEventListener child)
  {
    if (child instanceof IRenderable)
    {
      ((IRenderable) child).render(mouseX, mouseY, partialTicks);
    }
  }
  
  public void renderPre()
  {
  }

  public void renderPreChildren()
  {
  }
  
  public void renderPostChildren()
  {
  }
  
  public void renderPost()
  {
  }
  
  public void renderForeground()
  {
  }
  
  @Override
  public void onClose()
  {
    minecraft.displayGuiScreen(parent);
  }
  
  public void renderTexture(int x, int y, int width, int height, String id)
  {
    renderTexture(x, y, width, height, new ResourceLocation(id));
  }
  
  public void renderTexture(int x, int y, int width, int height, ResourceLocation id)
  {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    minecraft.getTextureManager().bindTexture(id);
    RenderSystem.color4f(1, 1, 1, 1);
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    
    builder.func_225582_a_(x, y + height, 0).func_225583_a_(0f, 1f).func_227885_a_(255, 255, 255, 255).endVertex();
    builder.func_225582_a_(x + width, y + height, 0).func_225583_a_(1f, 1f).func_227885_a_(255, 255, 255, 255).endVertex();
    builder.func_225582_a_(x + width, y, 0).func_225583_a_(1f, 0f).func_227885_a_(255, 255, 255, 255).endVertex();
    builder.func_225582_a_(x, y, 0).func_225583_a_(0f, 0f).func_227885_a_(255, 255, 255, 255).endVertex();
    
    tessellator.draw();
  }
  
  public void renderTexture(int x, int y, int width, int height, float u, float v, int r, int g, int b, int a, ResourceLocation id)
  {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    minecraft.getTextureManager().bindTexture(id);
    RenderSystem.color4f(1, 1, 1, 1);
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
  
    builder.func_225582_a_(x, y + height, 0).func_225583_a_(0f, v).func_227885_a_(r, g, b, a).endVertex();
    builder.func_225582_a_(x + width, y + height, 0).func_225583_a_(u, v).func_227885_a_(r, g, b, a).endVertex();
    builder.func_225582_a_(x + width, y, 0).func_225583_a_(u, 0f).func_227885_a_(r, g, b, a).endVertex();
    builder.func_225582_a_(x, y, 0).func_225583_a_(0f, 0f).func_227885_a_(r, g, b, a).endVertex();
  
    tessellator.draw();
  }
  
  public void renderTextureModal(int x, int y, int width, int height, int textureWidth, int textureHeight, ResourceLocation id)
  {
    renderTexture(x, y, width, height, (float) width / (float) textureWidth, (float) height / (float) textureHeight, 255, 255, 255, 255, id);
  }
  
  public void renderTextureRepeating(int x, int y, int width, int height, int textureHeight, int textureWidth, String id)
  {
    renderTextureRepeating(x, y, width, height, textureHeight, textureWidth, new ResourceLocation(id));
  }
  
  public void renderTextureRepeating(int x, int y, int width, int height, int textureHeight, int textureWidth, ResourceLocation id)
  {
    for (int xp = 0; xp < width; xp += textureWidth)
    {
      int w = (xp + textureWidth < width) ? textureWidth : width - xp;
      for (int yp = 0; yp < height; yp += textureHeight)
      {
        int h = (yp + textureHeight < height) ? textureHeight : height - yp;
        renderTextureModal(x + xp, y + yp, w, h, textureWidth, textureHeight, id);
      }
    }
  }
  
  public String lang(String key)
  {
    return I18n.format("mappy.gui." + key); //I18n.translate("mappy.gui." + key);
  }
 
  public PlayerEntity getLocalPlayer()
  {
    return minecraft.player;
  }
  
}
