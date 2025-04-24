package gungun974.uselessnumerical.mixin;

import net.minecraft.core.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Block.class, remap = false)
public interface BlockAccessor {
	@Accessor("id")
	public void setId(int id);
}
