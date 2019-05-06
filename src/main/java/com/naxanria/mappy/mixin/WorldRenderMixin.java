package com.naxanria.mappy.mixin;

import com.naxanria.mappy.map.waypoint.WayPointRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class WorldRenderMixin
{
  @Inject(method = "render", at = @At("RETURN"))
  public void worldRender(float float_1, long long_1, boolean boolean_1, CallbackInfo ci)
  {
//    WayPointRenderer.render(float_1);
  }
}
