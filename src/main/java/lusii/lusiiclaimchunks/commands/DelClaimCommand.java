package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

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
		if (LusiiClaimChunks.map.get(intPair) == null) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		if (!Objects.equals(LusiiClaimChunks.map.get(new LusiiClaimChunks.IntPair(cx, cz)).get(0), username)) {
			sender.sendMessage("§3You do not own this chunk!");
			return true;
		} else {
			LusiiClaimChunks.map.remove(intPair);
			LusiiClaimChunks.saveHashMap();
			return true;
		}
    }
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
