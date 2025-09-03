package gungun974.uselessnumerical.mixin;

import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Item.class, remap = false)
public class ItemMixin {
	@Mutable
	@Shadow
	@Final
	public int id;

	@Shadow
	@Final
	public static  Item @NotNull [] itemsList;

	@Mutable
	@Shadow
	@Final
	public NamespaceID namespaceID;

	@Shadow
	@Final
	public static Map itemsMap;

	@Shadow
	public static int highestItemId;

	@Inject(method = "<init>(Lnet/minecraft/core/util/collection/NamespaceID;I)V", at = @At("RETURN"))
	private void hijackRegister(NamespaceID namespaceId, int preferredId, CallbackInfo ci) {
		fakeItemsList[preferredId] = null;
		fakeItemsMap.remove(this.namespaceID);

		this.id = MinecraftIdsConfiguration.getInstance().generateNumericalIdForItem(namespaceId, preferredId);

		if (itemsList[id] != null) {
			throw new IllegalArgumentException("Item slot '" + id + "' is already occupied by '" + itemsList[id] + "' when adding " + this);
		} else if (itemsMap.containsKey(this.namespaceID)) {
			throw new IllegalArgumentException("Item id '" + this.namespaceID + "' is already used by '" + itemsMap.get(this.namespaceID) + "' when adding " + this);
		} else {
			itemsList[this.id] = (Item)(Object)this;
			itemsMap.put(this.namespaceID, this);
			if (this.id > highestItemId) {
				highestItemId = this.id;
			}
		}
	}

	@Redirect(
		method = "<init>(Lnet/minecraft/core/util/collection/NamespaceID;I)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/item/Item;itemsList:[Lnet/minecraft/core/item/Item;"
		)
	)
	private Item[] dontTouchItemsList() {
		return fakeItemsList;
	}

	@Unique
	private static final Item [] fakeItemsList = new Item['耀'];

	@Unique
	private static Map fakeItemsMap = new HashMap<>();

	@Redirect(
		method = "<init>(Lnet/minecraft/core/util/collection/NamespaceID;I)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/item/Item;itemsMap:Ljava/util/Map;"
		)
	)
	private Map dontTouchItemsMap() {
		return fakeItemsMap;
	}

	@Redirect(
		method = "<init>(Lnet/minecraft/core/util/collection/NamespaceID;I)V",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.PUTSTATIC,
			target = "Lnet/minecraft/core/item/Item;highestItemId:I"
		)
	)
	private void dontTouchHighestItemId(int value) {}
}
