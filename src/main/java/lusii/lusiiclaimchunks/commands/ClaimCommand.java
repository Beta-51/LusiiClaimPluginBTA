package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class ClaimCommand extends Command {
	public ClaimCommand() {
		super("claim");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.getPlayer().score < LusiiClaimChunks.cost) {
			sender.sendMessage("§eInsufficient funds!");
			return true;
		}
		if (sender.getPlayer().dimension != 0) {
			sender.sendMessage("§eOverworld only!");
			return true;
		}

		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		String username = sender.getPlayer().username;
		if (LusiiClaimChunks.getTrustedPlayersInChunk(intPair) == null) {
			LusiiClaimChunks.addTrustedPlayerToChunk(intPair, username);
			sender.sendMessage("§4Chunk claimed!");
			sender.getPlayer().score -= LusiiClaimChunks.cost;
        } else {
			sender.sendMessage("§eThis chunk is already claimed by §r" + LusiiClaimChunks.getTrustedPlayersInChunk(intPair).get(0) +"!");
        }
        return true;
    }
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
