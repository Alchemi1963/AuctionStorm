package com.alchemi.as.objects;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.objects.CarbonDating;
import com.alchemi.as.main;

public class Logging {

	public final String fileName;
	public final File file;
	public YamlConfiguration logger = new YamlConfiguration();
	
	public Logging(String fileName) {
		this.fileName = fileName;
		this.file = new File(main.instance.getDataFolder(), fileName);
		if (!this.file.exists()) {
			main.messenger.print(fileName + " does not exist, creating it now...");
			
			try {
				this.file.createNewFile();
				main.messenger.print("Log file created as " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
				main.messenger.print("Could not create " + fileName);
			}
		}
		this.logger = YamlConfiguration.loadConfiguration(file);
	}
		
	public void saveAuctionLog(AuctionLog log) {
	
		CarbonDating datetime = log.getIdentifier();
		logger.set("Last-Auction.ID", datetime.getCarbonDate());
		logger.set("Last-Auction.seller", log.getSeller());
		logger.createSection(log.getSeller() + "." + datetime.getCarbonDate());
		logger.set(log.getSeller() + ".UUID", log.getSellerUUID());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Object", log.getObject());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Price", log.getPrice());
		if (log.getBuyer() != null ) {
			logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Buyer", log.getBuyer());
			logger.set(log.getSeller() + "." + datetime.getCarbonDate() + "Buyer.UUID", log.getBuyerUUID());
		}
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", log.hasBeenRefunded());
		logger.addDefault(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", false);
		
		saveLog();
	}
	
	public AuctionLog getLastAuction() {
		
		CarbonDating dt = new CarbonDating(logger.getString("Last-Auction.ID"));
		String seller = logger.getString("Last-Auction.seller");
		
		return this.readLog(seller, dt);
		
	}
	
	public void updateAuctionLog(AuctionLog log) {
		
		CarbonDating datetime = log.getIdentifier();
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Object", log.getObject());
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Price", log.getPrice());
		if (log.getBuyer() != null ) {
			logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Buyer", log.getBuyer());
			logger.set(log.getSeller() + "." + datetime.getCarbonDate() + "Buyer.UUID", log.getBuyerUUID());
		}
		logger.set(log.getSeller() + "." + datetime.getCarbonDate() + ".Refunded", log.hasBeenRefunded());
		
		saveLog();
		
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
	
	public boolean hasLog(String seller, CarbonDating datetime) {
		return logger.contains(seller) && logger.contains(seller + "." + datetime.getCarbonDate());
	}
	
	public boolean hasLatestLog() {
		return logger.contains("Last-Auction") && logger.getString("Last-Auction.ID") != null && logger.getString("Last-Auction.seller") != null;
	}
	
	public AuctionLog readLog(String seller, CarbonDating datetime) {
		
		String sellerID = logger.getString(seller + ".UUID");
		ItemStack Object = logger.getItemStack(seller + "." + datetime.getCarbonDate() + ".Object");
		int price = logger.getInt(seller + "." + datetime.getCarbonDate() + ".Price");
		String buyer = logger.getString(seller + "." + datetime.getCarbonDate() + ".Buyer");
		String buyerID = logger.getString(seller + "." + datetime.getCarbonDate() + "Buyer.UUID", "");
		boolean refunded = logger.getBoolean(seller + "." + datetime.getCarbonDate() + ".Refunded");
		
		
		if (Object == null) {
			return null;
		}
	
		return new AuctionLog(seller, sellerID, buyer, buyerID, price, Object, refunded, datetime);
		
	}
}
