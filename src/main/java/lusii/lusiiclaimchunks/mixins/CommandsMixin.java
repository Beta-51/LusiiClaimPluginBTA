package lusii.lusiiclaimchunks.mixins;


import lusii.lusiiclaimchunks.commands.*;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Commands.class, remap = false)
public final class CommandsMixin {
	@Shadow
	public static List<Command> commands;
	@Inject(method = "initCommands", at = @At("TAIL"))
	private static void initCommands(CallbackInfo ci) {
			commands.add(new ClaimCommand());
			commands.add(new TrustCommand());
			commands.add(new DelClaimCommand());
			commands.add(new UnTrustCommand());
			commands.add(new OPClaimCommand());
			commands.add(new OPDelClaimCommand());
			commands.add(new OPTrustCommand());
			commands.add(new OPUnTrustCommand());
			commands.add(new TrustedCommand());
			commands.add(new DeleteAllClaimsCommand());
			commands.add(new ListClaims());
			commands.add(new TrustAllCommand());
			commands.add(new UnTrustAllCommand());
	}
}
