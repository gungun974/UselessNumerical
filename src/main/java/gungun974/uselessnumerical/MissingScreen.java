package gungun974.uselessnumerical;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.save.SaveFile;
import org.lwjgl.input.Keyboard;

public class MissingScreen extends Screen {
	private final Screen textField;
	private final MinecraftIdsConflict saveConflict;

	private final String worldDirName;
	private final String worldName;

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
	}


	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void render(int mx, int my, float partialTick) {
		this.renderBackground();
		this.drawStringCentered(this.font, "Numerical IDs missing", this.width / 2, 20, 16777215);
		this.drawStringCentered(this.font, "There are " + (saveConflict.localBlockMap.size() + saveConflict.localItemsMap.size()) + " missing blocks and items in this save.", this.width / 2, this.height / 2 - 50, 16777215);
		this.drawStringCentered(this.font, "If you continue, the missing blocks/items will get removed.", this.width / 2, this.height / 2 - 30, 16777215);
		//this.drawStringCentered(this.font, "Missing Blocks/Items:", this.width / 2, this.height / 2 + 10, 16777215);

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
}
