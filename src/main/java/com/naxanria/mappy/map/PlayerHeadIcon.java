package com.naxanria.mappy.map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHeadIcon
{
  public static final int HEAD_SIZE = 8;
  
  private static HashMap<UUID, PlayerHeadIcon> icons = new HashMap<>();
  
  private static boolean initialized = false;
  
  private UUID uuid;
  private boolean retry = false;
  private int wait = 2;
  private long time = System.currentTimeMillis();
  
  private ResourceLocation skinId;
  
  private PlayerEntity player;
  
  private PlayerHeadIcon(PlayerEntity player)
  {
    this.player = player;
    uuid = player.getUniqueID();
  }
  
  private static PlayerHeadIcon getIcon(PlayerEntity player)
  {
    if (!initialized)
    {
      initialize();
    }
    PlayerHeadIcon icon;
    long now = System.currentTimeMillis();
    
    if (icons.containsKey(player.getUniqueID()))
    {
      icon = icons.get(player.getUniqueID());
      
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
    
    Minecraft.getInstance().getTextureManager().bindTexture(icon.skinId);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    
    AbstractGui.blit(x, y, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
  }
  
  
  private static void register(PlayerHeadIcon icon)
  {
    loadSkin(icon);

    icons.put(icon.uuid, icon);
  }
  
  private static void loadSkin(PlayerHeadIcon icon)
  {
//    System.out.println("Loading player head icon for " + icon.player.getName().getString());
    Minecraft client = Minecraft.getInstance();
  
    SkinManager skinManager = client.getSkinManager();
    
//    PlayerSkinProvider provider = client.getSkinProvider();
    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = skinManager.loadSkinFromCache(icon.player.getGameProfile());
  
    if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
    {
//      System.out.println("Loading cached skin for " + icon.player.getName().getString());
      icon.skinId = skinManager.loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
      icon.retry = false;
    }
    else
    {
//      System.out.println("Loading default skin for " + icon.player.getName().getString());
      icon.skinId = DefaultPlayerSkin.getDefaultSkin(icon.uuid);
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
