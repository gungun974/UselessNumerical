package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(value = Block.class, remap = false)
public class BlockMixin {
	@Shadow
	@Final
	private @NotNull NamespaceID namespaceID;

	/**
	 * @author gungun974
	 * @reason swap block id
	 */
	@Overwrite
	private int setupNumericId(int preferredId) {
		int id = MinecraftIdsConfiguration.getInstance().generateNumericalIdForBlock(namespaceID, preferredId);
		if (id < 0) {
			throw new IllegalArgumentException("Numeric id of block '" + this.namespaceID + "' must not be negative!");
		} else if (id >= Blocks.blocksList.length) {
			throw new IllegalArgumentException("Numeric id of block '" + this.namespaceID + "' must not be greater than '" + (Blocks.blocksList.length - 1) + "'!");
		} else {
			return id;
		}
	}
}
