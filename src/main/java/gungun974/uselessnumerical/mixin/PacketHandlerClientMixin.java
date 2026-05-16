package gungun974.uselessnumerical.mixin;

import com.mojang.nbt.NbtIo;
import gungun974.uselessnumerical.ConflictScreen;
import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import gungun974.uselessnumerical.MinecraftIdsConflict;
import gungun974.uselessnumerical.UselessNumericalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenMainMenu;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketHandshake;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class PacketHandlerClientMixin {
	@Shadow
	@Final
	private NetworkManager netManager;

	@Shadow
	@Final
	private Minecraft mc;

	@Shadow
	private boolean disconnected;

	@Unique
	private MinecraftIdsConfiguration configuration;

	@Inject(method = "handleCustomPayload", at = @At(value = "TAIL"))
	public void handleCustomPayload(PacketCustomPayload customPayloadPacket, CallbackInfo ci) {
		if ("USELESSNUMERICAL".equals(customPayloadPacket.channel)) {
			UselessNumericalMod.LOGGER.info("Got server configuration");

			this.configuration = new MinecraftIdsConfiguration();

			try {
				configuration.loadNBTData(NbtIo.readCompressed(new ByteArrayInputStream(customPayloadPacket.data)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Inject(method = "handleHandshake", at = @At(value = "HEAD"), cancellable = true)
	public void checkCompatibilityWithServer(PacketHandshake packetHandshake, CallbackInfo ci) {
		if (configuration == null) {
			return;
		}

		MinecraftIdsConflict conflict = configuration.checkConflictWithInstance(true);

		if (conflict == MinecraftIdsConflict.NONE) {
			return;
		}

		this.netManager.networkShutdown("disconnect.uselessnumerical.conflict", new Object[]{});
		ci.cancel();
	}

	@Inject(method = "handleErrorMessage", at = @At(value = "TAIL"), cancellable = true)
	public void redirectToConflictScreen(String message, Object[] objects, CallbackInfo ci) {
		if (Objects.equals(message, "disconnect.uselessnumerical.conflict")) {
			disconnected = true;
			this.mc.displayScreen(new ConflictScreen(new ScreenMainMenu(), configuration));
		}

		ci.cancel();
	}
}
