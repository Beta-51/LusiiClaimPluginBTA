package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class OPDelClaimCommand extends Command {
	public OPDelClaimCommand() {
		super("opdelclaim","opunclaim");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		if (!LusiiClaimChunks.isChunkClaimed(intPair)) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		String oldOwner = LusiiClaimChunks.getTrustedPlayersInChunk(intPair).get(0);
		int refund = LusiiClaimChunks.getOPRefund(oldOwner);
		EntityPlayer player = handler.getPlayer(oldOwner);
		player.score += refund;
		if (LusiiClaimChunks.notifyOPClaim) {
			String posString = "(" + cx + ", " + cz + ")";
			handler.sendMessageToPlayer(player, "§1Claim at §4" + posString + "§1 was deleted by an Operator");
			handler.sendMessageToPlayer(player, "§1You have been refunded §4" + refund + "§1 points.");
		}

		LusiiClaimChunks.deleteClaim(intPair);
		sender.sendMessage("§eClaim deleted via Operator");
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
