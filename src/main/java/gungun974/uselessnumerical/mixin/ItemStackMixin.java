package gungun974.uselessnumerical.mixin;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import gungun974.uselessnumerical.UselessNumericalMod;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemStack.class, remap = false)
public class ItemStackMixin {
	@Shadow
	public int itemID;

	@Shadow
	private @NotNull CompoundTag tag;

	/**
	 * @author gungun974
	 * @reason convert from old NamespaceID
	 */
	@Overwrite
	private void conversionLegacyBuckets() {
		NamespaceID newState = null;

		int ironBucketId = Items.BUCKET_IRON.id;

		if (this.itemID == MinecraftIdsConfiguration.getInstance().getNumericalIdForItem(new NamespaceID("minecraft", "item/bucket"))) {
			this.itemID = ironBucketId;
		} else if (this.itemID == MinecraftIdsConfiguration.getInstance().getNumericalIdForItem(new NamespaceID("minecraft", "item/bucket_water"))) {
			this.itemID = ironBucketId;
			newState = ItemBucket.STATE_WATER;
		} else if (this.itemID == MinecraftIdsConfiguration.getInstance().getNumericalIdForItem(new NamespaceID("minecraft", "item/bucket_lava"))) {
			this.itemID = ironBucketId;
			newState = ItemBucket.STATE_LAVA;
		} else if (this.itemID == MinecraftIdsConfiguration.getInstance().getNumericalIdForItem(new NamespaceID("minecraft", "item/bucket_milk"))) {
			this.itemID = ironBucketId;
			newState = ItemBucket.STATE_MILK;
		} else if (this.itemID == MinecraftIdsConfiguration.getInstance().getNumericalIdForItem(new NamespaceID("minecraft", "item/bucket_icecream"))) {
			this.itemID = ironBucketId;
			newState = ItemBucket.STATE_ICECREAM;
		}

		if (newState != null) {
			this.tag.putString("State", newState.toString());
			this.tag.putInt("Charges", 1);
		}

	}
}
