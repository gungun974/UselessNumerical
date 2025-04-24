package gungun974.uselessnumerical;

import net.minecraft.core.util.collection.NamespaceID;

import java.util.Map;

public interface LocalBlockAndItemIds {
	public Map<NamespaceID, Integer> uselessNumerical$getLocalBlockMap();
	public Map<NamespaceID, Integer> uselessNumerical$getLocalItemsMap();

	public int uselessNumerical$getLocalHighestBlockId();
	public int uselessNumerical$getLocalHighestItemId();
}
