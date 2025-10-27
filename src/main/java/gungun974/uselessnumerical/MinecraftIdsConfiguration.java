package gungun974.uselessnumerical;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.Tag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MinecraftIdsConfiguration {
	private static MinecraftIdsConfiguration instance;
	private static boolean hasLoadedInstance;

	public MinecraftIdsConfiguration() {
	}

	public static synchronized MinecraftIdsConfiguration getInstance() {
		if (instance == null) {
			instance = new MinecraftIdsConfiguration();
		}

		if (!hasLoadedInstance) {
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
				instance.loadServerInstanceConfiguration();
			} else {
				instance.loadClientInstanceConfiguration();
			}
			hasLoadedInstance = true;
		}

		return instance;
	}

	private final Map<NamespaceID, Integer> localBlockMap = new LinkedHashMap<>();
	private final Map<NamespaceID, Integer> localItemsMap = new LinkedHashMap<>();

	public boolean hasWorldConfiguration(File saveDir) {
		if (!saveDir.exists()) {
			return false;
		} else {
			File worldLevelDat = new File(saveDir, "uselessNumericalSave.dat");
			if (worldLevelDat.exists()) {
				return true;
			} else {
				worldLevelDat = new File(saveDir, "uselessNumericalSave.dat_old");
				return worldLevelDat.exists();
			}
		}
	}

	@Environment(value = EnvType.CLIENT)
	public void loadClientInstanceConfiguration() {
		CompoundTag nbtRoot = null;

		File instanceDir = Minecraft.getMinecraft().getMinecraftDir();

		try {
			if (!instanceDir.exists()) {
				throw new IOException();
			} else {
				File worldLevelDat = new File(instanceDir, "uselessNumericalInstance.dat");
				if (worldLevelDat.exists()) {
					nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
				} else {
					worldLevelDat = new File(instanceDir, "uselessNumericalInstance.dat_old");
					if (worldLevelDat.exists()) {
						nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
					}
				}

				if (nbtRoot != null) {
					loadNBTData(nbtRoot);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Environment(value = EnvType.SERVER)
	public void loadServerInstanceConfiguration() {
		PropertyManager propertyManager = new PropertyManager(new File("server.properties"));

		String[] args = FabricLoader.getInstance().getLaunchArguments(true);

		String world = null;
		int pointer = 0;

		while(pointer < args.length) {
			if (args[pointer].equals("--world")) {
				try {
					world = args[pointer + 1];
					++pointer;
				} catch (Exception e) {
					e.printStackTrace();
				}

				++pointer;
			} else {
				++pointer;
			}
		}

		String lName = world == null ? propertyManager.getStringProperty("level-name", "world") : world;

		if (instance.hasWorldConfiguration(new File(lName))) {
			instance.loadWorldConfiguration(new File(lName));
			UselessNumericalMod.LOGGER.info("Load \"{}\" ids configuration", lName);
		}
	}

	public void loadWorldConfiguration(File saveDir) {
		CompoundTag nbtRoot = null;

		try {
			if (!saveDir.exists()) {
				throw new IOException();
			} else {
				File worldLevelDat = new File(saveDir, "uselessNumericalSave.dat");
				if (worldLevelDat.exists()) {
					nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
				} else {
					worldLevelDat = new File(saveDir, "uselessNumericalSave.dat_old");
					if (worldLevelDat.exists()) {
						nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
					}
				}

				if (nbtRoot != null) {
					loadNBTData(nbtRoot);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadNBTData(CompoundTag nbtRoot) {
		CompoundTag blockTags = nbtRoot.getCompound("Blocks");

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

		CompoundTag itemTags = nbtRoot.getCompound("Items");

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

		FabricLoader.getInstance().getEntrypoints("uselessNumerical", UselessNumericalEntrypoint.class).forEach(e -> e.defineAlias((NamespaceID from, NamespaceID to) -> {
			checkNamespaceValid(from);
			checkNamespaceValid(to);

			String fromObjectType = from.value().split("/")[0];
			String toObjectType = to.value().split("/")[0];

			if (!fromObjectType.equals(toObjectType)) {
				throw new IllegalArgumentException(
					String.format("A %s cannot be aliased into a %s!", fromObjectType, toObjectType)
				);
			}

			Integer blockValue = localBlockMap.remove(from);
			if (blockValue != null) {
				localBlockMap.put(to, blockValue);
			}

			Integer itemValue = localItemsMap.remove(from);
			if (itemValue != null) {
				localItemsMap.put(to, itemValue);
			}
		}));
	}

	private static void checkNamespaceValid(NamespaceID tested) {
		String objectType = tested.value().split("/")[0];

		if ("item".equals(objectType) || "block".equals(objectType)) return;

		throw new IllegalArgumentException(
			String.format(
				"Invalid item type for \"%s\"! Have you forgotten to prefix it with \"item/\" or \"block/\"?", objectType
			)
		);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void saveInstanceConfiguration() {
		CompoundTag rootTag = createNBTData();

		try {
			File levelDatNew = new File(Minecraft.getMinecraft().getMinecraftDir(), "uselessNumericalInstance.dat_new");
			File levelDatOld = new File(Minecraft.getMinecraft().getMinecraftDir(), "uselessNumericalInstance.dat_old");
			File levelDat = new File(Minecraft.getMinecraft().getMinecraftDir(), "uselessNumericalInstance.dat");

			NbtIo.writeCompressed(rootTag, Files.newOutputStream(levelDatNew.toPath()));
			if (levelDatOld.exists()) {
				levelDatOld.delete();
			}

			levelDat.renameTo(levelDatOld);
			if (levelDat.exists()) {
				levelDat.delete();
			}

			levelDatNew.renameTo(levelDat);
			if (levelDatNew.exists()) {
				levelDatNew.delete();
			}
		} catch (Exception exception) {
			UselessNumericalMod.LOGGER.error("Failed to save ids data to disk!", exception);
		}

	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void saveWorldConfiguration(File saveDir) {
		CompoundTag rootTag = createNBTData();

		try {
			File levelDatNew = new File(saveDir, "uselessNumericalSave.dat_new");
			File levelDatOld = new File(saveDir, "uselessNumericalSave.dat_old");
			File levelDat = new File(saveDir, "uselessNumericalSave.dat");

			NbtIo.writeCompressed(rootTag, Files.newOutputStream(levelDatNew.toPath()));
			if (levelDatOld.exists()) {
				levelDatOld.delete();
			}

			levelDat.renameTo(levelDatOld);
			if (levelDat.exists()) {
				levelDat.delete();
			}

			levelDatNew.renameTo(levelDat);
			if (levelDatNew.exists()) {
				levelDatNew.delete();
			}
		} catch (Exception exception) {
			UselessNumericalMod.LOGGER.error("Failed to save ids data to disk!", exception);
		}

	}

	public @NotNull CompoundTag createNBTData() {
		CompoundTag rootTag = new CompoundTag();

		CompoundTag blockTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Integer> entry : localBlockMap.entrySet()) {
			if (!Blocks.blockMap.containsKey(entry.getKey())) {
				continue;
			}

			blockTags.putInt(entry.getKey().toString(), entry.getValue());
		}

		rootTag.putCompound("Blocks", blockTags);

		CompoundTag itemTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Integer> entry : localItemsMap.entrySet()) {
			if (!Item.itemsMap.containsKey(entry.getKey())) {
				continue;
			}

			itemTags.putInt(entry.getKey().toString(), entry.getValue());
		}

		rootTag.putCompound("Items", itemTags);
		return rootTag;
	}

	public int getNumericalIdForBlock(NamespaceID id) {
		return localBlockMap.getOrDefault(id, -1);
	}

	public void setNumericalIdForBlock(NamespaceID id, int numericalId) {
		localBlockMap.put(id, numericalId);
	}

	public int generateNumericalIdForBlock(NamespaceID id, int preferredNumericalId) {
		FabricLoader.getInstance().getEntrypoints("uselessNumerical", UselessNumericalEntrypoint.class).forEach(e -> e.defineAlias((NamespaceID from, NamespaceID to) -> {
			if (from.equals(id)) {
				throw new IllegalArgumentException("Can't attribute custom id for block with " + id + " since it's an alias for " + to);
			}
		}));
		int attributedNumericalId = getNumericalIdForBlock(id);

		if (attributedNumericalId != -1) {
			return attributedNumericalId;
		}

		int numericalId = preferredNumericalId;

		while (localBlockMap.containsValue(numericalId)) {
			numericalId++;
		}

		setNumericalIdForBlock(id, numericalId);

		return numericalId;
	}

	public int getNumericalIdForItem(NamespaceID id) {
		return localItemsMap.getOrDefault(id, -1);
	}

	public void setNumericalIdForItem(NamespaceID id, int numericalId) {
		localItemsMap.put(id, numericalId);
	}

	public int generateNumericalIdForItem(NamespaceID id, int preferredNumericalId) {
		FabricLoader.getInstance().getEntrypoints("uselessNumerical", UselessNumericalEntrypoint.class).forEach(e -> e.defineAlias((NamespaceID from, NamespaceID to) -> {
			if (from.equals(id)) {
				throw new IllegalArgumentException("Can't attribute custom id for item with " + id + " since it's an alias for " + to);
			}
		}));

		if (preferredNumericalId < Blocks.blocksList.length) {
			setNumericalIdForItem(id, preferredNumericalId);
			return preferredNumericalId;
		}

		int attributedNumericalId = getNumericalIdForItem(id);

		if (attributedNumericalId != -1) {
			return attributedNumericalId;
		}

		int numericalId = preferredNumericalId;

		while (localItemsMap.containsValue(numericalId)) {
			numericalId++;
		}

		setNumericalIdForItem(id, numericalId);

		return numericalId;
	}

	public MinecraftIdsConflict checkConflictWithInstance(boolean ignoreMissing) {
		if (!ignoreMissing) {
			boolean missing = false;

			Map<NamespaceID, Integer> missingLocalBlockMap = new LinkedHashMap<>();
			Map<NamespaceID, Integer> missingLocalItemsMap = new LinkedHashMap<>();


			for (Map.Entry<NamespaceID, Integer> block : localBlockMap.entrySet()) {
				if (Blocks.blockMap.containsKey(block.getKey())) {
					continue;
				}

				missing = true;

				missingLocalBlockMap.put(block.getKey(), block.getValue());
			}

			for (Map.Entry<NamespaceID, Integer> item : localItemsMap.entrySet()) {
				if (Item.itemsMap.containsKey(item.getKey())) {
					continue;
				}

				missing = true;

				missingLocalItemsMap.put(item.getKey(), item.getValue());
			}

			if (missing) {
				MinecraftIdsConflict conflict = MinecraftIdsConflict.MISSING;

				conflict.localBlockMap = missingLocalBlockMap;
				conflict.localItemsMap = missingLocalItemsMap;

				return conflict;
			}
		}

		boolean needRestart = false;

		Map<NamespaceID, Integer> differentLocalBlockMap = new LinkedHashMap<>();
		Map<NamespaceID, Integer> differentLocalItemsMap = new LinkedHashMap<>();


		for (Map.Entry<NamespaceID, Integer> block : localBlockMap.entrySet()) {
			int loadedId = instance.getNumericalIdForBlock(block.getKey());

			if (loadedId == -1) {
				continue;
			}

			if (loadedId == block.getValue()) {
				continue;
			}

			needRestart = true;

			differentLocalBlockMap.put(block.getKey(), block.getValue());
		}

		for (Map.Entry<NamespaceID, Integer> item : localItemsMap.entrySet()) {
			int loadedId = instance.getNumericalIdForItem(item.getKey());

			if (loadedId == -1) {
				continue;
			}

			if (loadedId == item.getValue()) {
				continue;
			}

			needRestart = true;

			differentLocalItemsMap.put(item.getKey(), item.getValue());
		}

		if (needRestart) {
			MinecraftIdsConflict conflict = MinecraftIdsConflict.NEED_RESTART;

			conflict.localBlockMap = differentLocalBlockMap;
			conflict.localItemsMap = differentLocalItemsMap;

			return conflict;
		}

		return MinecraftIdsConflict.NONE;
	}
}
