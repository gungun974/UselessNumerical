package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.LocalBlockAndItemIds;
import gungun974.uselessnumerical.UselessNumericalMod;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.SaveHandlerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = SaveHandlerBase.class, remap = false)
public class SaveHandlerBaseMixin {
	@Inject(method = "getLevelData", at = @At("TAIL"))
	void loadLocalBlocksAndItems(CallbackInfoReturnable<LevelData> cir) {
		LocalBlockAndItemIds localBlockAndItemIds = (LocalBlockAndItemIds) cir.getReturnValue();

		if (localBlockAndItemIds == null) {
			return;
		}

		UselessNumericalMod.LOGGER.info("Blocks: {}", localBlockAndItemIds.uselessNumerical$getLocalBlockMap());
		UselessNumericalMod.LOGGER.info("Items: {}", localBlockAndItemIds.uselessNumerical$getLocalBlockMap());

		for (Map.Entry<NamespaceID, Integer> entry : localBlockAndItemIds.uselessNumerical$getLocalBlockMap().entrySet()) {
			NamespaceID namespaceID = entry.getKey();
			int id = entry.getValue();

			Block<?> block = Blocks.blockMap.get(namespaceID);

			if (block == null) {
				continue;
			}

			if (namespaceID.toString().equals("minecraft:block/stone")) {
				UselessNumericalMod.LOGGER.info("Block: {}", id);
			}

			//Blocks.blocksList[block.id()] = null;

			((BlockAccessor)(Object)block).setId(id);

			//TODO: Check if the replaced is resigned at the end of the loading
			Blocks.blocksList[id] = block;

			Blocks.keyToIdMap.put(block.getKey(), id);
		}

		for (Map.Entry<NamespaceID, Integer> entry : localBlockAndItemIds.uselessNumerical$getLocalItemsMap().entrySet()) {
			NamespaceID namespaceID = entry.getKey();
			int id = entry.getValue();

			Item item = Item.itemsMap.get(namespaceID);

			if (item == null) {
				continue;
			}

			if (namespaceID.toString().equals("minecraft:block/stone")) {
				UselessNumericalMod.LOGGER.info("Item: {}", id);
			}

			//Item.itemsList[item.id] = null;

			((ItemAccessor)item).setId(id);

			//TODO: Check if the replaced is resigned at the end of the loading
			Item.itemsList[id] = item;

			Item.nameToIdMap.put(item.getKey(), id);
		}


		Blocks.highestBlockId = localBlockAndItemIds.uselessNumerical$getLocalHighestBlockId();
		Item.highestItemId = localBlockAndItemIds.uselessNumerical$getLocalHighestItemId();

//		BlocksAccessor.setHasInit(false);
//		Blocks.init();

//		ItemsAccessor.setHasInit(false);
//		Items.init();

//		UselessNumericalMod.LOGGER.info("TBlock: {}", Blocks.STONE.id());
//		UselessNumericalMod.LOGGER.info("TItem: {}", Blocks.STONE.asItem().id);
	}
}
