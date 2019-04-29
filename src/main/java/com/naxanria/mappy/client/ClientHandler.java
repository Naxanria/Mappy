package com.naxanria.mappy.client;

import com.naxanria.mappy.Mappy;
import net.minecraft.client.MinecraftClient;

public class ClientHandler
{
  public static void tick(MinecraftClient client)
  {
    KeyHandler.INSTANCE.update();
    Mappy.map.update();
  }
}
