package com.alchemi.toa.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;


public class Commando implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (Library.checkCmdPermission(cmd, sender, "as.base", "auctionstorm") && sender instanceof Player) {
			Player player = (Player)sender;
			Library.sendMsg("Yo moms is dum", player, player);
			
			System.out.println(args);
			
			return true;
		}
		
		return false;
	}

	
	
}
