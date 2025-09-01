package gungun974.uselessnumerical;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class MinecraftIdsConfiguration {
	private static MinecraftIdsConfiguration instance;

	public MinecraftIdsConfiguration() {}

	public static synchronized MinecraftIdsConfiguration getInstance() {
		if (instance == null) {
			instance = new MinecraftIdsConfiguration();
		}
		return instance;
	}

	@Unique
	private final Map<NamespaceID, Integer> localBlockMap = new LinkedHashMap<>();
	@Unique
	private final Map<NamespaceID, Integer> localItemsMap = new LinkedHashMap<>();

	public boolean hasWorldConfiguration(File saveDir) {
		try {
			if (!saveDir.exists()) {
				throw new IOException();
			} else {
				File worldLevelDat = new File(saveDir, "uselessNumericalSave.dat");
				if (worldLevelDat.exists()) {
					return true;
				} else {
					worldLevelDat = new File(saveDir, "uselessNumericalSave.dat_old");
					if (worldLevelDat.exists()) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public void loadInstanceConfiguration() {
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

	private void loadNBTData(CompoundTag nbtRoot) {
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
	}

	public void saveInstanceConfiguration() {
		CompoundTag rootTag = CreateNBTData();

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
			UselessNumericalMod.LOGGER.error((String)"Failed to save ids data to disk!", (Throwable)exception);
		}

	}

	public void saveWorldConfiguration(File saveDir) {
		CompoundTag rootTag = CreateNBTData();

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
			UselessNumericalMod.LOGGER.error((String)"Failed to save ids data to disk!", (Throwable)exception);
		}

	}

	private static @NotNull CompoundTag CreateNBTData() {
		CompoundTag rootTag = new CompoundTag();

		CompoundTag blockTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Block<?>> entry  : Blocks.blockMap.entrySet()) {
			blockTags.putInt(entry.getKey().toString(), entry.getValue().id());
		}

		rootTag.putCompound("Blocks", blockTags);

		CompoundTag itemTags = new CompoundTag();

		for (Map.Entry<NamespaceID, Item> entry  : Item.itemsMap.entrySet()) {
			itemTags.putInt(entry.getKey().toString(), entry.getValue().id);
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

	public MinecraftIdsConflict checkConflictWithInstance() {

		for (Map.Entry<NamespaceID, Integer> block : localBlockMap.entrySet()) {
			int loadedId = instance.getNumericalIdForBlock(block.getKey());

			if (loadedId == -1) {
				continue;
			}

			if (loadedId == block.getValue()) {
				continue;
			}

			return MinecraftIdsConflict.NEED_RESTART;
		}

		for (Map.Entry<NamespaceID, Integer> item : localItemsMap.entrySet()) {
			int loadedId = instance.getNumericalIdForItem(item.getKey());

			if (loadedId == -1) {
				continue;
			}

			if (loadedId == item.getValue()) {
				continue;
			}

			return MinecraftIdsConflict.NEED_RESTART;
		}

		return MinecraftIdsConflict.NONE;
	}
}
