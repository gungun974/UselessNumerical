package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Inject(method = "startGame", at = @At("HEAD"))
	public void beforeGameStartEntrypoint(CallbackInfo ci){
		MinecraftIdsConfiguration.getInstance().loadInstanceConfiguration();
	}

	@Inject(method = "startGame", at = @At("TAIL"))
	public void afterGameStartEntrypoint(CallbackInfo ci){
		MinecraftIdsConfiguration.getInstance().saveInstanceConfiguration();
	}
}
