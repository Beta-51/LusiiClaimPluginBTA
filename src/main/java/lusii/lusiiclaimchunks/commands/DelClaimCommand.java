package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class DelClaimCommand extends Command {
	public DelClaimCommand() {
		super("delclaim","unclaim");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		String username = sender.getPlayer().username;
		if (!LusiiClaimChunks.isChunkClaimed(intPair)) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		if (!LusiiClaimChunks.getTrustedPlayersInChunk(new LusiiClaimChunks.IntPair(cx, cz)).get(0).equals(username)) {
			sender.sendMessage("§3You do not own this chunk!");
        } else {
			LusiiClaimChunks.deleteClaim(intPair);
			int refund = LusiiClaimChunks.getRefund(username);
			sender.getPlayer().score += refund;
			sender.sendMessage("§4Claim removed!");
			sender.sendMessage("§1Refunded §4" + refund + "§1 points.");

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
