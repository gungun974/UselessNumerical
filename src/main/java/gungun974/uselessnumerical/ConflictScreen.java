package gungun974.uselessnumerical;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.lang.I18n;
import org.lwjgl.input.Keyboard;

public class ConflictScreen extends Screen {
	private final Screen textField;
	private final MinecraftIdsConfiguration saveConfiguration;

	public ConflictScreen(Screen guiscreen, MinecraftIdsConfiguration saveConfiguration) {
		this.textField = guiscreen;
		this.saveConfiguration = saveConfiguration;
	}

	public void init() {
		I18n i18n = I18n.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(new ButtonElement(1, this.width / 2 - 100, this.height / 4 + 128 + 12, i18n.translateKey("gui.create_world.button.cancel")));
		this.buttons.add(new ButtonElement(2, this.width / 2 - 100, this.height / 4 + 104 + 12, "Restart and load IDs"));
	}


	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void render(int mx, int my, float partialTick) {
		this.renderBackground();
		this.drawStringCentered(this.font, "Numerical IDs mismatch", this.width / 2, 20, 16777215);
		this.drawStringCentered(this.font, "The world you tried to load uses a different ID configuration.", this.width / 2, this.height / 2 - 50, 16777215);
		this.drawStringCentered(this.font, "Restarting the game is required to apply it.", this.width / 2, this.height / 2 - 30, 16777215);
		this.drawStringCentered(this.font, "Would you like to restart?", this.width / 2, this.height / 2 + 10, 16777215);

		super.render(mx, my, partialTick);
	}

	protected void buttonClicked(ButtonElement button) {
		if (button.enabled) {
			if (button.id == 1) {
				this.mc.displayScreen(this.textField);
			} else if (button.id == 2) {
				saveConfiguration.saveInstanceConfiguration();
				this.mc.shutdown();
			}
		}
	}
}
