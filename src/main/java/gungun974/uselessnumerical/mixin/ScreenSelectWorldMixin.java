package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.*;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenSelectWorld;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.core.world.save.SaveFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.List;

@Mixin(value = ScreenSelectWorld.class, remap = false)
public abstract class ScreenSelectWorldMixin extends Screen {
	@Shadow
	private List saveList;

	@Shadow
	private boolean selected;

	@Shadow
	protected abstract String getSaveFileName(int i);

	@Shadow
	protected abstract String getSaveName(int i);

	@Inject(method = "selectWorld", at = @At("HEAD"), cancellable = true)
	private void checkIfWorldIsCompatible(int i, CallbackInfo ci) {
		SaveFile save = (SaveFile) this.saveList.get(i);

		if (!(save instanceof ISaveFile)) {
			return;
		}

		File saveDir = ((ISaveFile) save).uselessNumerical$getSaveDirectory();

		MinecraftIdsConfiguration saveConfiguration = new MinecraftIdsConfiguration();

		if (!saveConfiguration.hasWorldConfiguration(saveDir)) {
			return;
		}

		saveConfiguration.loadWorldConfiguration(saveDir);

		MinecraftIdsConflict conflict = saveConfiguration.checkConflictWithInstance(false);

		if (conflict == MinecraftIdsConflict.NONE) {
			return;
		}

		if (conflict == MinecraftIdsConflict.MISSING && !save.isCorrupted()) {
			String s = this.getSaveFileName(i);
			if (s == null) {
				s = "World" + i;
			}

			this.mc.displayScreen(new MissingScreen(this, saveConfiguration, conflict, s, this.getSaveName(i)));

			ci.cancel();
			return;
		}

		this.mc.displayScreen(new ConflictScreen(this, saveConfiguration));

		ci.cancel();
	}
}
