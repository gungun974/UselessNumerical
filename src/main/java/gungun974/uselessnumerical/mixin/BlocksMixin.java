package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSupplier;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Blocks.class, remap = false)
public class BlocksMixin {
	@Unique
	static private @NotNull NamespaceID setupNamespaceId(String namespaceId) {
		if (namespaceId == null) {
			throw new NullPointerException("NamespaceId must not be null!");
		} else {
			if (!namespaceId.contains(":")) {
				namespaceId = "minecraft:" + namespaceId;
			}

			try {
				return NamespaceID.getPermanent(namespaceId);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Inject(method = "register", at = @At("HEAD"), cancellable = true)
	private static <T extends BlockLogic> void hijackRegister(String key, String rawNamespaceId, int preferredId, BlockLogicSupplier<T> logicSupplier, CallbackInfoReturnable<Block<T>> cir) {
		cir.cancel();

		NamespaceID namespaceId = setupNamespaceId(rawNamespaceId);

		int id = MinecraftIdsConfiguration.getInstance().generateNumericalIdForBlock(namespaceId, preferredId);

		Block<T> container = BlockAccessor.invokeNew(id, key, rawNamespaceId, logicSupplier);

		Blocks.blocksList[container.id()] = container;

		if (Blocks.keyToIdMap.containsKey(container.getKey())) {
			throw new IllegalArgumentException("Key '" + container.getKey() + "' of block '" + container.namespaceId() + "' is already being used by '" + Blocks.getBlock(Blocks.keyToIdMap.get(container.getKey())).namespaceId() + "'!");
		}

		Blocks.keyToIdMap.put(container.getKey(), container.id());

		if (Blocks.blockMap.containsKey(container.namespaceId())) {
			throw new IllegalArgumentException("NamespaceId '" + container.namespaceId() + "' of block '" + container.getKey() + "' is already being used by '" + Blocks.blockMap.get(container.namespaceId()).namespaceId() + "'!");
		}

		Blocks.blockMap.put(container.namespaceId(), container);

		if (Blocks.highestBlockId < container.id()) {
			Blocks.highestBlockId = container.id();
		}

		cir.setReturnValue(container);

	}
}
