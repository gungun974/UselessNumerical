package gungun974.uselessnumerical.mixin;

import net.minecraft.core.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Item.class, remap = false)
public interface ItemAccessor {
	@Accessor("id")
	@Mutable
	void setId(int id);
}
