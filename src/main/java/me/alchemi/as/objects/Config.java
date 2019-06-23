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
import me.alchemi.as.main;

public class Config extends ConfigBase {

	public Config() throws FileNotFoundException, IOException, InvalidConfigurationException {
		super(main.getInstance());
		
	}
	
	public static enum ConfigEnum implements IConfigEnum {
		
		CONFIG(new File(main.getInstance().getDataFolder(), "config.yml"), 23), 
		MESSAGES(new File(main.getInstance().getDataFolder(), "messages.yml"), 25);
		
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
	
	public static enum MESSAGES implements IMessage{
		
		AUCTION_START("AuctionStorm.Auction.Start"),
		AUCTION_STARTNAMED("AuctionStorm.Auction.StartNamed"),
		AUCTION_QUEUED("AuctionStorm.Auction.Queued"),
		AUCTION_WRONG_NONE("AuctionStorm.Auction.Wrong.None"),
		AUCTION_WRONG_ITEM("AuctionStorm.Auction.Wrong.Item"),
		AUCTION_WRONG_CREATIVE("AuctionStorm.Auction.Wrong.Creative"),
		AUCTION_WRONG_ENOUGH("AuctionStorm.Auction.Wrong.Enough"),
		AUCTION_WRONG_PRICE("AuctionStorm.Auction.Wrong.Price"),
		AUCTION_WRONG_PRICEINF("AuctionStorm.Auction.Wrong.PriceInf"),
		AUCTION_WRONG_DURATION("AuctionStorm.Auction.Wrong.Duration"),
		AUCTION_WRONG_DURATIONINF("AuctionStorm.Auction.Wrong.DurationInf"),
		AUCTION_WRONG_INCREMENT("AuctionStorm.Auction.Wrong.Increment"),
		AUCTION_WRONG_INCREMENTINF("AuctionStorm.Auction.Wrong.IncrementInf"),
		AUCTION_WRONG_AMOUNT("AuctionStorm.Auction.Wrong.Amount"),
		AUCTION_WRONG_BANNED("AuctionStorm.Auction.Wrong.Banned"),
		AUCTION_BID_OWN_AUCTION("AuctionStorm.Auction.Bid.Own-Auction"),
		AUCTION_BID_NO_MONEY("AuctionStorm.Auction.Bid.No-Money"),
		AUCTION_BID_BID("AuctionStorm.Auction.Bid.Bid"),
		AUCTION_BID_OUTBID("AuctionStorm.Auction.Bid.Outbid"),
		AUCTION_BID_LOW("AuctionStorm.Auction.Bid.Low"),
		AUCTION_BID_MAX("AuctionStorm.Auction.Bid.Max"),
		AUCTION_TIME_ADDED("AuctionStorm.Auction.Time.Added"),
		AUCTION_TIME_HALFTIME("AuctionStorm.Auction.Time.Halftime"),
		AUCTION_TIME_NOTIFY("AuctionStorm.Auction.Time.Notify"),
		AUCTION_END_END("AuctionStorm.Auction.End.End"),
		AUCTION_END_NO_BIDS("AuctionStorm.Auction.End.No-Bids"),
		AUCTION_END_PAID_BY("AuctionStorm.Auction.End.Paid-By"),
		AUCTION_END_PAID_TO("AuctionStorm.Auction.End.Paid-To"),
		AUCTION_END_CANCELLED("AuctionStorm.Auction.End.Cancelled"),
		AUCTION_END_FORCED("AuctionStorm.Auction.End.Forced"),
		AUCTION_END_FORCEDSELLER("AuctionStorm.Auction.End.ForcedSeller"),
		AUCTION_END_FORCEDREASON("AuctionStorm.Auction.End.ForcedReason"),
		AUCTION_END_FORCEDREASONSELLER("AuctionStorm.Auction.End.ForcedReasonSeller"),
		AUCTION_INFO_GET("AuctionStorm.Auction.Info.Get"),
		AUCTION_INFO_HEADER("AuctionStorm.Auction.Info.Header"),
		AUCTION_INFO_LOGHEADER("AuctionStorm.Auction.Info.LogHeader"),
		AUCTION_INFO_ITEM("AuctionStorm.Auction.Info.Item"),
		AUCTION_INFO_ITEMNAMED("AuctionStorm.Auction.Info.ItemNamed"),
		AUCTION_INFO_LORE("AuctionStorm.Auction.Info.Lore"),
		AUCTION_INFO_DURABILITY("AuctionStorm.Auction.Info.Durability"),
		AUCTION_INFO_ENCHANTMENTHEADER("AuctionStorm.Auction.Info.EnchantmentHeader"),
		AUCTION_INFO_ENCHANTMENT("AuctionStorm.Auction.Info.Enchantment"),
		AUCTION_INFO_AMOUNT("AuctionStorm.Auction.Info.Amount"),
		AUCTION_INFO_STARTINGBID("AuctionStorm.Auction.Info.StartingBid"),
		AUCTION_INFO_PRICE("AuctionStorm.Auction.Info.Price"),
		AUCTION_INFO_BIDDER("AuctionStorm.Auction.Info.Bidder"),
		AUCTION_INFO_CURRENTBID("AuctionStorm.Auction.Info.CurrentBid"),
		AUCTION_INFO_TIME("AuctionStorm.Auction.Info.Time"),
		AUCTION_INFO_FOOTER("AuctionStorm.Auction.Info.Footer"),
		AUCTION_QUEUE_HEADER("AuctionStorm.Auction.Queue.Header"),
		AUCTION_QUEUE_AUCTION("AuctionStorm.Auction.Queue.Auction"),
		AUCTION_QUEUE_FOOTER("AuctionStorm.Auction.Queue.Footer"),
		AUCTION_QUEUE_EMPTY("AuctionStorm.Auction.Queue.Empty"),
		AUCTION_QUEUE_NOTAUCTION("AuctionStorm.Auction.Queue.NotAuction"),
		COMMAND_NO_PERMISSION("AuctionStorm.Command.NoPermission"),
		COMMAND_WRONG_FORMAT("AuctionStorm.Command.Wrong-Format"),
		COMMAND_UNKNOWN("AuctionStorm.Command.Unknown"),
		COMMAND_GIVEN("AuctionStorm.Command.Given"),
		COMMAND_SILENCED("AuctionStorm.Command.Silenced"),
		COMMAND_UNSILENCED("AuctionStorm.Command.Unsilenced"),
		COMMAND_ADMIN_LOGGING_DISABLED("AuctionStorm.Command.Admin.Logging-Disabled"),
		COMMAND_ADMIN_NO_LOGS("AuctionStorm.Command.Admin.No-Logs"),
		COMMAND_ADMIN_LOG_NON_EXISTENT("AuctionStorm.Command.Admin.Log-Non-Existent"),
		COMMAND_ADMIN_ITEMS_RETURNED("AuctionStorm.Command.Admin.Items-Returned"),
		COMMAND_ADMIN_MONEY_RETURNED("AuctionStorm.Command.Admin.Money-Returned"),
		COMMAND_ADMIN_MONEY_TAKEN("AuctionStorm.Command.Admin.Money-Taken"),
		COMMAND_ADMIN_ALREADY_REFUNDED("AuctionStorm.Command.Admin.Already-Refunded"),
		COMMAND_ADMIN_NO_BUYER("AuctionStorm.Command.Admin.No-Buyer");
		
		String value;
		String key;
		
		private MESSAGES(String key) {
			this.key = key;
		}
		
		public void get() { 
			value = ConfigEnum.MESSAGES.getConfig().getString(key);
			
		}
		
		public String value() {
			return value;
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.MESSAGES.getConfig();
		}
	}
	
	public static enum AUCTION implements IConfig {
		
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
		BANNED_ITEMS("Auction.Banned-Items");
		
		private Object value;
		public final String key;
		
		AUCTION(String key){
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
		
		@SuppressWarnings("unchecked")
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
			return Material.valueOf(asString());
		}
		
		@SuppressWarnings("unchecked")
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
	}
	
	public static enum VAULT implements IConfig {
		
		VALUTA_SINGULAR("Vault.valutaSingular"),
		VALUTA_PLURAL("Vault.valutaPlural");
		
		private Object value;
		public final String key;
		
		VAULT(String key){
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

		@SuppressWarnings("unchecked")
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
			return Material.valueOf(asString());
		}
		
		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}
	}
	
	@Override
	public void reload() {
		super.reload();
		
		main.banned_items.clear();
		for (String mat : ConfigEnum.CONFIG.getConfig().getStringList("Auction.Banned-Items")) {
				main.banned_items.add(Material.getMaterial(mat));
		}
	}

	@Override
	protected IConfigEnum[] getConfigs() {
		return ConfigEnum.values();
	}

	@Override
	protected Set<IConfig> getEnums() {
		Set<IConfig> set = new HashSet<IConfig>();
		set.addAll(Arrays.asList(AUCTION.values()));
		set.addAll(Arrays.asList(VAULT.values()));
		return set;
	}

	@Override
	protected Set<IMessage> getMessages() {
		return new HashSet<IMessage>() {{
			addAll(Arrays.asList(MESSAGES.values()));
		}};
	}
	
}
