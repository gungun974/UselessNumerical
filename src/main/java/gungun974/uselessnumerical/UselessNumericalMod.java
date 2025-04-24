package gungun974.uselessnumerical;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gungun974.uselessnumerical.mixin.BlockAccessor;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class UselessNumericalMod implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "uselessnumerical";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("UselessNumerical initialized.");
    }

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}

	@Override
	public void beforeGameStart() {
//		Block<?> block = Blocks.STONE;
//		((BlockAccessor)(Object)block).setId(10_000);
//
//		if (Blocks.blocksList[block.id()] != null) {
//			//throw new IllegalArgumentException("Id '" + id + "' of block '" + block.namespaceId() + "' is already being used by '" + blocksList[block.id()].namespaceId() + "'!");
//		} else {
//			Blocks.blocksList[block.id()] = block;
//			if (Blocks.keyToIdMap.containsKey(block.getKey())) {
//				//throw new IllegalArgumentException("Key '" + block.getKey() + "' of block '" + block.namespaceId() + "' is already being used by '" + getBlock((Integer)keyToIdMap.get(block.getKey())).namespaceId() + "'!");
//			} else {
//				Blocks.keyToIdMap.put(block.getKey(), block.id());
//				if (Blocks.blockMap.containsKey(block.namespaceId())) {
//					//throw new IllegalArgumentException("NamespaceId '" + block.namespaceId() + "' of block '" + block.getKey() + "' is already being used by '" + ((Block)blockMap.get(block.namespaceId())).namespaceId() + "'!");
//				} else {
//					Blocks.blockMap.put(block.namespaceId(), block);
//					if (Blocks.highestBlockId < block.id()) {
//						Blocks.highestBlockId = block.id();
//					}
//
//					//return block;
//				}
//			}
//		}
	}

	@Override
	public void afterGameStart() {

	}
}
