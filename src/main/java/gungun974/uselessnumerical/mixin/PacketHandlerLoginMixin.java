package gungun974.uselessnumerical.mixin;

import com.mojang.nbt.NbtIo;
import gungun974.uselessnumerical.MinecraftIdsConfiguration;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketPreLogin;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public abstract class PacketHandlerLoginMixin {
	@Shadow
	public NetworkManager netManager;

	@Inject(method = "handleHandshake", at = @At(value = "HEAD"))
	public void sendConfiguration(PacketPreLogin preLoginPacket, CallbackInfo ci) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			NbtIo.writeCompressed(MinecraftIdsConfiguration.getInstance().createNBTData(), baos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] buffer = baos.toByteArray();

		this.netManager.addToSendQueue(new PacketCustomPayload("USELESSNUMERICAL", buffer));
	}
}
