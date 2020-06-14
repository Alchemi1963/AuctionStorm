package me.alchemi.as.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.base.ConfigBase;
import me.alchemi.as.Storm;

public class Config extends ConfigBase {

	public Config() throws FileNotFoundException, IOException, InvalidConfigurationException {
		super(Storm.getInstance());
	}
	
	public static enum ConfigEnum implements IConfigEnum {
		
		CONFIG(new File(Storm.getInstance().getDataFolder(), "config.yml"), 23);
		
		final File file;
		final int version;
		SexyConfiguration config;

		private ConfigEnum(File file, int version) {
			this.file = file;
			this.version = version;
			this.config = SexyConfiguration.loadConfiguration(file);
		}
		
		@Override
		public SexyConfiguration getConfig() {
			return config;
		}
		
		@Override
		public File getFile() {
			return file;
		}
		
		@Override
		public int getVersion() {
			return version;
		}
		
	}
	
	public static enum AuctionOptions implements IConfig {
		
		SOUND_PLAY("Auction.Sound.Play"),
		SOUND_PAY("Auction.Sound.Pay"),
		SOUND_PAID("Auction.Sound.Paid"),
		SOUND_FAILED("Auction.Sound.Failed"),
		NOTIFY("Auction.Notify"),
		NOTIFYTIMES("Auction.NotifyTimes"),
		ALLOWCREATIVE("Auction.AllowCreative"),
		LOGAUCTIONS("Auction.LogAuctions"),
		HOVERITEM("Auction.HoverItem"),
		HOVERITEMMINECRAFTTOOLTIP("Auction.HoverItemMinecraftToolTip"),
		DISPLAYLORE("Auction.displayLore"),
		ANTISNIPE_TRESHOLD("Auction.AntiSnipe-Treshold"),
		ANTISNIPE_TIME_ADDED("Auction.AntiSnipe-Time-Added"),
		BIDTAX("Auction.BidTax"),
		SELLTAX("Auction.SellTax"),
		START_DELAY("Auction.Start-Delay"),
		START_DEFAULTS_PRICE("Auction.Start-Defaults.Price"),
		START_DEFAULTS_INCREMENT("Auction.Start-Defaults.Increment"),
		START_DEFAULTS_DURATION("Auction.Start-Defaults.Duration"),
		MAXIMUM_VALUES_BID("Auction.Maximum-Values.Bid"),
		MAXIMUM_VALUES_PRICE("Auction.Maximum-Values.Price"),
		MAXIMUM_VALUES_INCREMENT("Auction.Maximum-Values.Increment"),
		MAXIMUM_VALUES_DURATION("Auction.Maximum-Values.Duration"),
		MINIMUM_VALUES_PRICE("Auction.Minimum-Values.Price"),
		MINIMUM_VALUES_AMOUNT("Auction.Minimum-Values.Amount"),
		MINIMUM_VALUES_INCREMENT("Auction.Minimum-Values.Increment"),
		MINIMUM_VALUES_DURATION("Auction.Minimum-Values.Duration"),
		COOLDOWN("Auction.Cooldown"),
		BANNED_ITEMS("Auction.Banned-Items");
		
		private Object value;
		public final String key;
		
		AuctionOptions(String key){
			this.key = key;
			get();
		}
				
		@Override
		public void get() {
			value = ConfigEnum.CONFIG.getConfig().get(key);
		}
		
		@Override
		public Object value() {
			return value;
		}
		
		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}
		
		@Override
		public String asString() {
			return String.valueOf(value);
		}
		
		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}
		
		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}
		
		public double asDouble() {
			return Double.valueOf(asString());
		}
		
		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public Material asMaterial() {
			return Material.getMaterial(asString());
		}
		
		public List<Integer> asIntList(){
			try {
				return (List<Integer>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@Override
		public List<Float> asFloatList() {
			try {
				return (List<Float>) value;
			} catch (ClassCastException e) {return null; }
		}
	}
	
	public static enum Vault implements IConfig {
		
		CURRENCY_SINGULAR("Vault.currencySingular"),
		CURRENCY_PLURAL("Vault.currencyPlural");
		
		private Object value;
		public final String key;
		
		Vault(String key){
			this.key = key;
			get();
		}
		
		@Override
		public void get() {
			value = ConfigEnum.CONFIG.getConfig().get(key);
		}

		@Override
		public Object value() {
			return value;
		}

		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}

		@Override
		public String asString() {
			return String.valueOf(value);
		}

		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}

		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}

		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public Material asMaterial() {
			return Material.getMaterial(asString());
		}
		
		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@Override
		public double asDouble() {
			return Double.parseDouble(asString());
		}

		@Override
		public List<Float> asFloatList() {
			try {
				return (List<Float>) value;
			} catch (ClassCastException e) {return null; }
		}

		@Override
		public List<Integer> asIntList() {
			try {
				return (List<Integer>) value;
			} catch (ClassCastException e) {return null; }
		}
	}
	
	@Override
	public void reload() {
		super.reload();
		
		Storm.banned_items.clear();
		for (String mat : ConfigEnum.CONFIG.getConfig().getStringList("Auction.Banned-Items")) {
				Storm.banned_items.add(Material.getMaterial(mat));
		}
	}

	@Override
	protected IConfigEnum[] getConfigs() {
		return ConfigEnum.values();
	}

	@Override
	protected Set<IConfig> getEnums() {
		Set<IConfig> set = new HashSet<IConfig>();
		set.addAll(Arrays.asList(AuctionOptions.values()));
		set.addAll(Arrays.asList(Vault.values()));
		return set;
	}
	
}
