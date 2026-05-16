package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import gungun974.uselessnumerical.UselessNumericalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.settings.WorldConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	private File mcDataDir;

	@Shadow
	private ISaveFormat saveFormat;

	@Inject(method = "startGame", at = @At("TAIL"))
	public void afterGameStartEntrypoint(CallbackInfo ci) {
		MinecraftIdsConfiguration.getInstance().saveInstanceConfiguration();
	}

	@Inject(method = "createAndStartWorld", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/Minecraft;changeWorld(Lnet/minecraft/client/world/WorldClient;Ljava/lang/String;)V"
	))
	public void saveInstanceConfigurationInWorld(WorldConfiguration worldConfiguration, CallbackInfo ci) {
		MinecraftIdsConfiguration.getInstance().saveWorldConfiguration(new File(this.mcDataDir, "saves/" + worldConfiguration.getFolderName(this.saveFormat)));
	}
}
