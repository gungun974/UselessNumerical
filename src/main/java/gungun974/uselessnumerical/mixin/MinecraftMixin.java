package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.type.WorldTypeGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow private File mcDataDir;

	@Inject(method = "startGame", at = @At("HEAD"))
	public void beforeGameStartEntrypoint(CallbackInfo ci){
		MinecraftIdsConfiguration.getInstance().loadInstanceConfiguration();
	}

	@Inject(method = "startGame", at = @At("TAIL"))
	public void afterGameStartEntrypoint(CallbackInfo ci){
		MinecraftIdsConfiguration.getInstance().saveInstanceConfiguration();
	}

	@Inject(method = "startWorld(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/core/world/type/WorldTypeGroups$Group;)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;changeWorld(Lnet/minecraft/client/world/WorldClient;Ljava/lang/String;)V"
	))
	public void saveInstanceConfigurationInWorld(String worldDirName, String worldName, long seed, WorldTypeGroups.Group worldTypeGroup, CallbackInfo ci) {
		MinecraftIdsConfiguration.getInstance().saveWorldConfiguration(new File(this.mcDataDir, "saves/" + worldDirName));
	}
}
