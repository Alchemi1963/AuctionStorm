package me.alchemi.as.objects;

import me.alchemi.al.configurations.IMessage;

public enum Messages implements IMessage{
	
	AUCTION_STARTCOOLDOWN("AuctionStorm.Auction.StartCooldown"),
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
	AUCTION_END_NO_MONEY("AuctionStorm.Auction.End.No-Money"),
	AUCTION_END_NO_MONEY_BIDDER("AuctionStorm.Auction.End.No-Money-Bidder"),
	AUCTION_END_NO_MONEY_SELLER("AuctionStorm.Auction.End.No-Money-Seller"),
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
	final String key;
	
	private Messages(String key) {
		this.key = key;
	}
	
	@Override
	public void setValue(Object value) {
		this.value = value.toString();
	}
	
	@Override
	public String value() {
		return value;
	}
	
	@Override
	public String key() {
		return key;
	}
	
	@Override
	public String toString() {
		return value();
	}
}