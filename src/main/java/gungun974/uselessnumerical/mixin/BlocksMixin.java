package gungun974.uselessnumerical.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSupplier;
import net.minecraft.core.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Blocks.class, remap = false)
public class BlocksMixin {
	@Inject(method = "register", at = @At("HEAD"), cancellable = true)
	private static <T extends BlockLogic> void hijackRegister(String key, String namespaceId, int id, BlockLogicSupplier<T> logicSupplier, CallbackInfoReturnable<Block<T>> cir) {
		cir.cancel();

		while (Blocks.blocksList[id] != null) {
			id += 1;
		}

		Block<T> container = BlockAccessor.invokeNew(id, key, namespaceId, logicSupplier);

		Blocks.blocksList[container.id()] = container;

		if (Blocks.keyToIdMap.containsKey(container.getKey())) {
			throw new IllegalArgumentException("Key '" + container.getKey() + "' of block '" + container.namespaceId() + "' is already being used by '" + Blocks.getBlock((Integer) Blocks.keyToIdMap.get(container.getKey())).namespaceId() + "'!");
		}

		Blocks.keyToIdMap.put(container.getKey(), container.id());

		if (Blocks.blockMap.containsKey(container.namespaceId())) {
			throw new IllegalArgumentException("NamespaceId '" + container.namespaceId() + "' of block '" + container.getKey() + "' is already being used by '" + ((Block) Blocks.blockMap.get(container.namespaceId())).namespaceId() + "'!");
		}

		Blocks.blockMap.put(container.namespaceId(), container);

		if (Blocks.highestBlockId < container.id()) {
			Blocks.highestBlockId = container.id();
		}

		cir.setReturnValue(container);

	}
}
