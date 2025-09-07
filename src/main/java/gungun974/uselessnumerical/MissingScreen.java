package gungun974.uselessnumerical;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenStats;
import net.minecraft.client.gui.ScrolledSelectionList;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.achievement.stat.StatItem;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.item.Item;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.save.SaveFile;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MissingScreen extends Screen {
	private final Screen textField;
	private final MinecraftIdsConflict saveConflict;

	private final String worldDirName;
	private final String worldName;

	private MissingList missingList;

	public MissingScreen(Screen guiscreen, MinecraftIdsConflict saveConflict, String worldDirName, String worldName) {
		this.textField = guiscreen;
		this.saveConflict = saveConflict;
		this.worldDirName = worldDirName;
		this.worldName = worldName;
	}

	public void init() {
		I18n i18n = I18n.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(new ButtonElement(1, this.width / 2 + 4, this.height - 28, 150, 20, i18n.translateKey("gui.select_world.button.cancel")));
		this.buttons.add(new ButtonElement(2, this.width / 2 - 154, this.height - 28, 150, 20, "Keep loading"));

		this.missingList = new MissingList(saveConflict);
	}


	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void render(int mx, int my, float partialTick) {
		missingList.render(mx, my, partialTick);

		this.drawStringCentered(this.font, "Numerical IDs missing", this.width / 2, 20, 16777215);
		this.drawStringCentered(this.font, "There are " + (saveConflict.localBlockMap.size() + saveConflict.localItemsMap.size()) + " missing blocks and items in this save.", this.width / 2, 40, 16777215);
		this.drawStringCentered(this.font, "If you continue, the missing blocks/items will get removed.", this.width / 2, 60, 16777215);

		super.render(mx, my, partialTick);
	}

	protected void buttonClicked(ButtonElement button) {
		if (button.enabled) {
			if (button.id == 1) {
				this.mc.displayScreen(this.textField);
			} else if (button.id == 2) {
				loadWorld();
			}
		}
	}

	public void loadWorld() {
		this.mc.displayScreen((Screen)null);
		this.mc.playerController = new PlayerControllerSP(this.mc);

		this.mc.startWorld(worldDirName, worldName, 0L);
		this.mc.displayScreen((Screen)null);
	}

	class MissingList extends ScrolledSelectionList {
		List<NamespaceID> missingLocalBlocks = new ArrayList<>();
		List<NamespaceID> missingLocalItems = new ArrayList<>();

		protected MissingList(MinecraftIdsConflict saveConflict) {
			super(MissingScreen.this.mc, MissingScreen.this.width + 124, MissingScreen.this.height, 60 + 16, MissingScreen.this.height - 38, 20);
			this.setRenderSelection(false);
			this.setRenderHeader(true, 8);

			for (Map.Entry<NamespaceID, Integer> block : saveConflict.localBlockMap.entrySet()) {
				missingLocalBlocks.add(block.getKey());
			}

			for (Map.Entry<NamespaceID, Integer> item : saveConflict.localItemsMap.entrySet()) {
				missingLocalItems.add(item.getKey());
			}
		}

		protected void selectItem(int itemIndex, boolean doubleClicked) {
		}

		protected boolean isSelectedItem(int itemIndex) {
			return false;
		}

		protected void renderHoleBackground() {
			MissingScreen.this.renderBackground();
		}

		protected final int getItemCount() {
			return this.missingLocalBlocks.size() + this.missingLocalItems.size();
		}

		protected void renderItem(int index, int x, int y, int height, Tessellator tessellator) {
			int blocksCount = missingLocalBlocks.size();
			int itemsCount  = missingLocalItems.size();

			if (index < blocksCount) {
				NamespaceID block = missingLocalBlocks.get(index);
				MissingScreen.this.drawString(MissingScreen.this.font, block.toString(), x + 2 - 124 / 2, y + 1, index % 2 != 0 ? 9474192 : 16777215);
				return;
			}

			int itemIndex = index - blocksCount;
			if (itemIndex < itemsCount) {
				NamespaceID item = missingLocalItems.get(itemIndex);
				MissingScreen.this.drawString(MissingScreen.this.font, item.toString(), x + 2 - 124 / 2, y + 1, index % 2 != 0 ? 9474192 : 16777215);
			}
		}
	}
}
