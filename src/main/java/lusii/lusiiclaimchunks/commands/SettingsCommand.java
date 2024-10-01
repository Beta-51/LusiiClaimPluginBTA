package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class SettingsCommand extends Command {
	public SettingsCommand() {
		super("claimsettings","claimtrust");
	}

	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) {
			return false;
		}
		if (args[0].equals("cost")) {
			if (args.length == 1) {
				sender.sendMessage(LusiiClaimChunks.costEquation);
			} else {
				StringBuilder equation = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					equation.append(args[i]);
				}
				LusiiClaimChunks.costEquation = String.valueOf(equation);
				sender.sendMessage("Cost equation changed to "+equation);
				LusiiClaimChunks.deleteConfig();
			}
			return true;
		} else if (args[0].equals("maxclaims")) {
			if (args.length == 1) {
				sender.sendMessage(String.valueOf(LusiiClaimChunks.maxClaims));
			} else {
				LusiiClaimChunks.maxClaims = Integer.parseInt(args[1]);
				sender.sendMessage("Max claims changed to "+Integer.parseInt(args[1]));
				LusiiClaimChunks.deleteConfig();
			}
			return true;
		} else if (args[0].equals("refundratio")) {
			if (args.length == 1) {
				sender.sendMessage(String.valueOf(LusiiClaimChunks.refundRatio));
			} else {
				LusiiClaimChunks.refundRatio = Float.parseFloat(args[1]);
				sender.sendMessage("Refund ratio changed to "+Float.parseFloat(args[1]));
				LusiiClaimChunks.deleteConfig();
			}
			return true;
		} else if (args[0].equals("oprefundratio")) {
			if (args.length == 1) {
				sender.sendMessage(String.valueOf(LusiiClaimChunks.adminRefundRatio));
			} else {
				LusiiClaimChunks.adminRefundRatio = Float.parseFloat(args[1]);
				sender.sendMessage("Operator refund ratio changed to "+Float.parseFloat(args[1]));
				LusiiClaimChunks.deleteConfig();
			}
			return true;
		} else if (args[0].equals("notifyopclaim")) {
			if (args.length == 1) {
				sender.sendMessage(String.valueOf(LusiiClaimChunks.notifyOPClaim));
			} else {
				LusiiClaimChunks.notifyOPClaim = Boolean.getBoolean(args[1]);
				sender.sendMessage("Notify operator claim changed to "+Boolean.getBoolean(args[1]));
				LusiiClaimChunks.deleteConfig();
			}
			return true;
		}
        return false;
    }

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/claimsettings cost <equation [x = amount of chunks]>");
		sender.sendMessage("/claimsettings maxclaims <integer>");
		sender.sendMessage("/claimsettings refundratio <0.0 to 1.0>");
		sender.sendMessage("/claimsettings oprefundratio <0.0 to 1.0>");
		sender.sendMessage("/claimsettings notifyopclaim <true/false>");
	}
}
