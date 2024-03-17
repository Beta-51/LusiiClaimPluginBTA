package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OPUnTrustCommand extends Command {
	public OPUnTrustCommand() {
		super("opuntrust","opclaimuntrust");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		if (LusiiClaimChunks.map.get(intPair) == null) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		List<String> Users = new ArrayList<>(LusiiClaimChunks.map.get(intPair));
		Users.remove(args[0]);
		LusiiClaimChunks.map.remove(intPair, Users);
		sender.sendMessage("§ePlayer §r"+ args[0] + " §euntrusted via Operator.");
		LusiiClaimChunks.saveHashMap();
		return true;
		}
//
	public boolean opRequired(String[] args) {
		return true;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
