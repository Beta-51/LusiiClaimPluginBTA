package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class OPClaimCommand extends Command {
	public OPClaimCommand() {
		super("opclaim");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		String username;
        if (args.length == 0) {
			username = sender.getPlayer().username;
		} else {
			username = args[0];
		}

		if (LusiiClaimChunks.isChunkClaimed(intPair)) {
			String oldOwner = LusiiClaimChunks.getTrustedPlayersInChunk(intPair).get(0);
			int refund = LusiiClaimChunks.getOPRefund(oldOwner);
			EntityPlayer player = handler.getPlayer(oldOwner);
			player.score += refund;
			if (LusiiClaimChunks.notifyOPClaim) {
				String posString = "(" + cx + ", " + cz + ")";
				handler.sendMessageToPlayer(player, "§1Claim at §4" + posString + "§1 was claimed by an Operator");
				handler.sendMessageToPlayer(player, "§1You have been refunded §4" + refund + "§1 points.");

			}
		}

		LusiiClaimChunks.setOwnerToChunk(intPair, username);
		sender.sendMessage("§eClaimed via Operator");
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
