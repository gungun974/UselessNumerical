package gungun974.uselessnumerical;

import net.minecraft.core.util.collection.NamespaceID;

import java.util.Map;

public enum MinecraftIdsConflict {
	NONE,
	NEED_RESTART,
	MISSING;

	Map<NamespaceID, Integer> localBlockMap;

	Map<NamespaceID, Integer> localItemsMap;
}
