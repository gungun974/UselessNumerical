package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.collection.NamespaceID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Item.class, remap = false)
public class ItemMixin {
	@Mutable
	@Shadow
	@Final
	public int id;

	@Inject(method = "<init>(Lnet/minecraft/core/util/collection/NamespaceID;I)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/util/collection/NamespaceID;makePermanent()Lnet/minecraft/core/util/collection/NamespaceID;",
		shift = At.Shift.AFTER
	))
	private void hijackRegister(NamespaceID namespaceId, int preferredId, CallbackInfo ci) {
		this.id = MinecraftIdsConfiguration.getInstance().generateNumericalIdForItem(namespaceId, preferredId);
	}
}
