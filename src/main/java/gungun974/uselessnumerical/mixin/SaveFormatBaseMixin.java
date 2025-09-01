package gungun974.uselessnumerical.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import gungun974.uselessnumerical.ISaveFile;
import net.minecraft.core.world.save.SaveFile;
import net.minecraft.core.world.save.SaveFormatBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = SaveFormatBase.class, remap = false)
public class SaveFormatBaseMixin {
	@Inject(method = "getSaveFileList", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
	private void injectWorldDirectory(CallbackInfoReturnable<List<?>> cir, @Local ArrayList<SaveFile> saveFileList, @Local File dir) {
		SaveFile file = saveFileList.get(saveFileList.size() - 1);

		if (file instanceof ISaveFile) {
			((ISaveFile) file).uselessNumerical$setSaveDirectory(dir);
		}
	}
}
