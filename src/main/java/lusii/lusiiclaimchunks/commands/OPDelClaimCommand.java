package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class OPDelClaimCommand extends Command {
	public OPDelClaimCommand() {
		super("opdelclaim","opunclaim");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		if (LusiiClaimChunks.map.get(intPair) == null) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
			LusiiClaimChunks.map.remove(intPair);
			sender.sendMessage("§eClaim deleted via Operator");
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
