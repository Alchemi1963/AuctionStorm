package com.alchemi.toa;

import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.Library;

public class TheOncomingAuction extends JavaPlugin {
	public static String pluginname;

	@Override
	public void onEnable() {
		
		pluginname = getDescription().getName();

		Library.print(getDescription().getFullName(), pluginname);
		Library.print("Vworp vworp vworp", pluginname);
		
	}
	
	@Override
	public void onDisable() {
		
		Library.print("I don't wanna go...", pluginname);
		
	}
	
}
