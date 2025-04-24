package gungun974.uselessnumerical.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSupplier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Block.class, remap = false)
public interface BlockAccessor {
	@Accessor("id")
	public void setId(int id);

	@Invoker("<init>")
	static <T extends BlockLogic> Block<T> invokeNew(int id, @NotNull String translationKey, @NotNull String namespaceId, @NotNull BlockLogicSupplier<T> logicSupplier) {
		throw new AssertionError();
	}
}
