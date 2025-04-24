package gungun974.uselessnumerical.mixin;

import net.minecraft.core.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Items.class, remap = false)
public interface ItemsAccessor {
	@Accessor("hasInit")
	public static void setHasInit(boolean hasInit) {
		throw new AssertionError();
	}
}
