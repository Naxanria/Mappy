package com.naxanria.mappy.map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.Map;

public class PlayerHeadIcon
{
  public static final int HEAD_SIZE = 8;
  
  private static HashMap<UUID, PlayerHeadIcon> icons = new HashMap<>();
  
  private static boolean initialized = false;
  
  private UUID uuid;
  private boolean retry = false;
  private int wait = 2;
  private long time = System.currentTimeMillis();
  
  private Identifier skinId;
  
  private PlayerEntity player;
  
  private PlayerHeadIcon(PlayerEntity player)
  {
    this.player = player;
    uuid = player.getUuid();
  }
  
  private static PlayerHeadIcon getIcon(PlayerEntity player)
  {
    if (!initialized)
    {
      initialize();
    }
    PlayerHeadIcon icon;
    long now = System.currentTimeMillis();
    
    if (icons.containsKey(player.getUuid()))
    {
      icon = icons.get(player.getUuid());
      
      if (icon.retry)
      {
        if (now - icon.time - icon.wait * 1000 > 0)
        {
          loadSkin(icon);
        }
      }
      else if (now - icon.time > 120000) // 2 min
      {
        icon.retry = true;
        icon.wait = 1;
        loadSkin(icon);
      }
    }
    else
    {
      icon = new PlayerHeadIcon(player);
      register(icon);
    }

    return icon;
  }
  
  public static void drawHead(PlayerEntity player, int x, int y)
  {
    PlayerHeadIcon icon = getIcon(player);
    MinecraftClient.getInstance().getTextureManager().bindTexture(icon.skinId);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    DrawableHelper.blit(x, y, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
  }
  
  
  private static void register(PlayerHeadIcon icon)
  {
    loadSkin(icon);

    icons.put(icon.uuid, icon);
  }
  
  private static void loadSkin(PlayerHeadIcon icon)
  {
    System.out.println("Loading player head icon for " + icon.player.getName().getString());
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerSkinProvider provider = client.getSkinProvider();
    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = provider.getTextures(icon.player.getGameProfile());
  
    if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
    {
      System.out.println("Loading cached skin for " + icon.player.getName().getString());
      icon.skinId = provider.loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
      icon.retry = false;
    }
    else
    {
      System.out.println("Loading default skin for " + icon.player.getName().getString());
      icon.skinId = DefaultSkinHelper.getTexture(icon.uuid);
      icon.retry = true;
      icon.wait *= 2;
    }
  }
  
  private static void initialize()
  {
    System.out.println("Initializing PlayerHeadIcons");
    if (initialized)
    {
      return;
    }
    initialized = true;
  }
}
