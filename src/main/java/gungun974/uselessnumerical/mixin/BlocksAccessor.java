package gungun974.uselessnumerical.mixin;

import net.minecraft.core.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Blocks.class, remap = false)
public interface BlocksAccessor {
	@Accessor("hasInit")
	public static void setHasInit(boolean hasInit) {
		throw new AssertionError();
	}
}
