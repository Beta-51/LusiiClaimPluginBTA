package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class TrustCommand extends Command {
	public TrustCommand() {
		super("trust","claimtrust");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		String username = sender.getPlayer().username;
		String player;
		player = args[0];
		if (!LusiiClaimChunks.isChunkClaimed(intPair)) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		if (!LusiiClaimChunks.isPlayerOwner(new LusiiClaimChunks.IntPair(cx, cz), username)) {
			sender.sendMessage("§eYou do not own this chunk!");
        } else {
			LusiiClaimChunks.addTrustedPlayerToChunk(intPair, player);
			sender.sendMessage("§3Player §r"+ player + " §3trusted.");
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
