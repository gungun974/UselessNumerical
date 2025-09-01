package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.ISaveFile;
import net.minecraft.core.world.save.SaveFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;

@Mixin(value = SaveFile.class, remap = false)
public class SaveFileMixin implements ISaveFile {
	@Unique
	private File saveDir;

	@Override
	public void uselessNumerical$setSaveDirectory(File dir) {
		this.saveDir = dir;
	}

	@Override
	public File uselessNumerical$getSaveDirectory() {
		return this.saveDir;
	}
}
