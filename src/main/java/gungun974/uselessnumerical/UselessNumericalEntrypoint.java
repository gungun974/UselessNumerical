package gungun974.uselessnumerical;

import net.minecraft.core.util.collection.NamespaceID;

import java.util.function.BiConsumer;

/**
 * Represents an entry point for defining numerical aliases within a namespace.
 * <p>
 * Implementations of this interface should define alias mappings between two {@link NamespaceID}s,
 * allowing the destination ID to point to the same internal reference as the origin ID.
 * This mechanism ensures that legacy or renamed {@link NamespaceID}s can still be resolved properly.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>
 * public class Test implements UselessNumericalEntrypoint {
 *     &#64;Override
 *     public void defineAlias(BiConsumer&lt;NamespaceID, NamespaceID&gt; alias) {
 *         alias.accept(
 *             NamespaceID.getPermanent("mymod", "dorr"),
 *             NamespaceID.getPermanent("mymod", "door")
 *         );
 *     }
 * }
 * </pre>
 * <p>
 * In this example, the alias redirects <code>"mymod:dorr"</code> to <code>"mymod:door"</code>.
 * This allows <code>UselessNumerical</code> to ensure that if the {@link NamespaceID}
 * <code>"mymod:dorr"</code> was renamed to <code>"mymod:door"</code>,
 * any world that still references the old ID <code>"mymod:dorr"</code>
 * will automatically have it transferred to <code>"mymod:door"</code>.
 * </p>
 *
 * <p><b>Important:</b></p>
 * It is strictly forbidden to define an alias where the origin {@link NamespaceID}
 * still exists or will later be registered in the registry.
 * The source {@link NamespaceID} must <b>never</b> be registered again later,
 * as this would create a direct conflict between the alias and the registered {@link NamespaceID}.
 * <br>
 * This alias system must only be used when the original {@link NamespaceID}
 * has been removed or renamed and no longer exists in the item or block registry.
 * </p>
 */
public interface UselessNumericalEntrypoint {

	/**
	 * Defines an alias between two {@link NamespaceID} instances.
	 * The alias ensures that the destination {@link NamespaceID} points to the same reference
	 * as the source {@link NamespaceID}, maintaining compatibility between renamed or removed identifiers.
	 * <p>
	 * <b>Note:</b> The source {@link NamespaceID} must not correspond to an ID
	 * that will be registered later, as this would create a conflict
	 * between the aliased and registered {@link NamespaceID}.
	 * </p>
	 *
	 * @param alias a {@link BiConsumer} accepting a source {@link NamespaceID} (origin)
	 *              and a destination {@link NamespaceID} (target),
	 *              establishing the alias relationship between them
	 */
	void defineAlias(BiConsumer<NamespaceID, NamespaceID> alias);
}
