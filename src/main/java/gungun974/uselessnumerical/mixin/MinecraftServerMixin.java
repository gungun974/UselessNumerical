package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import gungun974.uselessnumerical.UselessNumericalMod;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(value = MinecraftServer.class, remap = false)
public class MinecraftServerMixin {
	@Shadow
	@Nullable
	public String argWorld;

	@Shadow
	public PropertyManager propertyManager;

	@Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/registry/Registries;<init>()V"))
	public void beforeGameStartEntrypoint(CallbackInfoReturnable<Boolean> cir) {
		String lName = this.argWorld == null ? this.propertyManager.getStringProperty("level-name", "world") : this.argWorld;

		if (MinecraftIdsConfiguration.getInstance().hasWorldConfiguration(new File(lName))) {
			MinecraftIdsConfiguration.getInstance().loadWorldConfiguration(new File(lName));
			UselessNumericalMod.LOGGER.info("Load \"{}\" ids configuration", lName);
		}
	}

	@Inject(method = "startServer", at = @At("TAIL"))
	public void afterGameStartEntrypoint(CallbackInfoReturnable<Boolean> cir) {
		String lName = this.argWorld == null ? this.propertyManager.getStringProperty("level-name", "world") : this.argWorld;

		UselessNumericalMod.LOGGER.info("Save \"{}\" ids configuration", lName);
		MinecraftIdsConfiguration.getInstance().saveWorldConfiguration(new File(lName));
	}
}
