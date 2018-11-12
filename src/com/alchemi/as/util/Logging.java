package com.alchemi.as.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.CarbonDating;
import com.alchemi.as.AuctionStorm;

public class Logging {

	public final String fileName;
	public final File file;
	public YamlConfiguration logger = new YamlConfiguration();
	
	public Logging(String fileName) {
		this.fileName = fileName;
		this.file = new File(AuctionStorm.instance.getDataFolder(), fileName);
		if (!this.file.exists()) {
			AuctionStorm.instance.messenger.print(fileName + " does not exist, creating it now...");
			
			try {
				this.file.createNewFile();
				AuctionStorm.instance.messenger.print("Log file created as " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
				AuctionStorm.instance.messenger.print("Could not create " + fileName);
			}
		}
		this.logger = YamlConfiguration.loadConfiguration(file);
	}
		
	public void saveAuctionLog(AuctionLog log) {
	
		CarbonDating datetime = log.getIdentifier();
		logger.createSection(log.getSeller() + "." + datetime.getCarbonDate());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Object", log.getObject());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Price", log.getPrice());
		if (log.getBuyer() != null ) logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Buyer", log.getBuyer());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", log.hasBeenRefunded());
		logger.addDefault(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", false);
		
		saveLog();
	}
	
	public void updateAuctionLog(AuctionLog log) {
		
		CarbonDating datetime = log.getIdentifier();
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Object", log.getObject());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Price", log.getPrice());
		if (log.getBuyer() != null ) logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Buyer", log.getBuyer());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", log.hasBeenRefunded());
		
	}
	
	public void saveLog() {
		try {
			logger.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setRefunded(AuctionLog log, CarbonDating cd) {
		logger.set(log.getSeller() + "." + cd.getCarbonDate() + ".Refunded", true);
		saveLog();
	}
	
	public AuctionLog readLog(String seller, CarbonDating datetime) {
		
		ItemStack Object = logger.getItemStack(seller + "." + datetime.getCarbonDate() + ".Object");
		int price = logger.getInt(seller + "." + datetime.getCarbonDate() + ".Price");
		String buyer = logger.getString(seller + "." + datetime.getCarbonDate() + ".Buyer");
		boolean refunded = logger.getBoolean(seller + "." + datetime.getCarbonDate() + ".Refunded");
		
		if (Object == null) {
			return null;
		}
	
		return new AuctionLog(seller, buyer, price, Object, refunded, datetime);
		
	}
}
