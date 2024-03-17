package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.*;

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
		if (LusiiClaimChunks.map.get(intPair) == null) {
			sender.sendMessage("§3No one owns this chunk!");
			return true;
		}
		if (!Objects.equals(LusiiClaimChunks.map.get(new LusiiClaimChunks.IntPair(cx, cz)).get(0), username)) {
			sender.sendMessage("§eYou do not own this chunk!");
			return true;
		} else {
			List<String> Users = new ArrayList<>(LusiiClaimChunks.map.get(intPair));
			Users.add(player);
			LusiiClaimChunks.map.put(intPair, Users);
			sender.sendMessage("§3Player §r"+ player + " §3trusted.");
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
