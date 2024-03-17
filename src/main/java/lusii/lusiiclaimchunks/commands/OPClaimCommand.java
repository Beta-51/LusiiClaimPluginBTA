package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		List<String> Users = new ArrayList<>(LusiiClaimChunks.map.get(intPair));
		if (args.length == 0) {
			username = sender.getPlayer().username;
			Users.set(0, username);
		} else {
			username = args[0];
			Users.set(0, username);
		}

		LusiiClaimChunks.map.put(intPair, Users);
		LusiiClaimChunks.saveHashMap();
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
