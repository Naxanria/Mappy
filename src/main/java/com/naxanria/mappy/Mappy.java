package com.naxanria.mappy;

import com.naxanria.mappy.map.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.world.ClientWorld;

import java.io.File;

@Mod(Mappy.MODID)
public class Mappy
{
  public static final String MODID = "mappy";
  public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MODID);
  
  public static Map map;
  private File output;
  
  public static boolean debugMode = false;
  public static boolean showMap = true;
  
//  public static Path configFolder;
  
  public Mappy()
  {
    DistExecutor.runWhenOn(Dist.CLIENT,
    () -> () ->
      {
        ClientSetup.setup();
      }
    );
  }
}
