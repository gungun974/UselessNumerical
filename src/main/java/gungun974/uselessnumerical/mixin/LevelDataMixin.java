package gungun974.uselessnumerical.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.Tag;
import gungun974.uselessnumerical.LocalBlockAndItemIds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin implements LocalBlockAndItemIds {
	@Inject(method = "updateTagCompound", at = @At("TAIL"))
	private void updateTagCompoundWithBlocksAndItems(CompoundTag levelTag, CompoundTag playerTag, CallbackInfo ci) {
		CompoundTag blockTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Block<?>> entry  : Blocks.blockMap.entrySet()) {
			blockTags.putInt(entry.getKey().toString(), entry.getValue().id());
		}

		levelTag.putInt("HighestBlockId", Blocks.highestBlockId);
		levelTag.putCompound("Blocks", blockTags);

		CompoundTag itemTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Item> entry  : Item.itemsMap.entrySet()) {
			itemTags.putInt(entry.getKey().toString(), entry.getValue().id);
		}

		levelTag.putInt("HighestItemId", Item.highestItemId);
		levelTag.putCompound("Items", itemTags);
	}

	@Unique
	private final Map<NamespaceID, Integer> localBlockMap = new LinkedHashMap<>();
	@Unique
	private final Map<NamespaceID, Integer> localItemsMap = new LinkedHashMap<>();

	@Unique
	private int localHighestBlockId = -1;
	@Unique
	private int localHighestItemId = -1;

	@Inject(method = "readFromCompoundTag", at = @At("TAIL"))
	private void readFromCompoundTagWithBlocksAndItems(CompoundTag levelTag, CallbackInfo ci) {
		localHighestBlockId = levelTag.getInteger("HighestBlockId");
		CompoundTag blockTags = levelTag.getCompound("Blocks");

		localBlockMap.clear();

		for (Map.Entry<String, Tag<?>> entry : blockTags.getValue().entrySet()) {
			final Tag<?> tag = entry.getValue();
			if (!(tag instanceof IntTag)) {
				continue;
			}

			final int id = ((IntTag) tag).getValue();

			try {
				final NamespaceID namespaceID = NamespaceID.getPermanent(entry.getKey());

				localBlockMap.put(namespaceID, id);

			} catch (HardIllegalArgumentException ignored) {
			}
		}

		localHighestItemId = levelTag.getInteger("HighestItemId");
		CompoundTag itemTags = levelTag.getCompound("Items");

		localItemsMap.clear();

		for (Map.Entry<String, Tag<?>> entry : itemTags.getValue().entrySet()) {
			final Tag<?> tag = entry.getValue();
			if (!(tag instanceof IntTag)) {
				continue;
			}

			final int id = ((IntTag) tag).getValue();

			try {
				final NamespaceID namespaceID = NamespaceID.getPermanent(entry.getKey());

				localItemsMap.put(namespaceID, id);

			} catch (HardIllegalArgumentException ignored) {
			}
		}
	}

	@Override
	public Map<NamespaceID, Integer> uselessNumerical$getLocalBlockMap() {
		return localBlockMap;
	}

	@Override
	public Map<NamespaceID, Integer> uselessNumerical$getLocalItemsMap() {
		return localItemsMap;
	}

	@Override
	public int uselessNumerical$getLocalHighestBlockId() {
		return localHighestBlockId;
	}

	@Override
	public int uselessNumerical$getLocalHighestItemId() {
		return localHighestItemId;
	}
}
