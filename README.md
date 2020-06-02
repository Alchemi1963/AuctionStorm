Description

This is an auction plugin wich focuses on being more dynamic.
Almost every value can be configured!

To create an auction just hold the desired item in your hand and use –

/as start <price> [amount] [increment] [duration]

    price
        The price with which you want the bidding to start.
    amount
        The amount of the item you want to sell (standard = everything in your hand) and can be greater than 64 or "all", if it's "all" it will auction of everything you have of the item.
    increment
        The amount of money you want every bid to increase with; (standard = 10, configurable).
    duration
        How long you want the auction to last; (standard = 60 seconds, configurable).


Features

    Supports Vault permissions and economy!
    Auction info, displayed when hovering above messages.
    Auction logging (can be turned off in config)
        Log all auctions so you can refund the items and money spent (only admin).
    Message customization
        Customize every message players receive in the messages.yml.
    Auction cancelling
        Admins can cancel any auction (with the as.cancel permission node) the player who created the auction can cancel as well.
    Auction ending
        Admins can end the current auction (with the as.end permission node) the player who created the auction can end it as well.
    Blacklist
        Option to blacklist certain items in config.
    Creative blocking
        Auction Storm does not allow the creation of an auction while in creative, this can be configured.
    Anti-Snipe
        When a player bids when the auction has 5 seconds remaining, the remaining time is increased by 30 seconds. Both values can be configured.
    Give Queue
        When the server restarts during an auction, or if a seller is offline when the auction ends, the items are saved and given when the player logs back in.
    Auction silencing
        Players can silence auction broadcasts with /auction silence. With the permission node as.silence you can apply this kind of silence server-wide.

Commands

/auction – Main auction command. Use /auction help to get more information.
Aliases – /auc, /auctionstorm & /as​
/bid [bid amount] [secret bid] – Bid on the current auction, the secret bid is the bid which automatically outbids the next bid.
Alternative for /auction bid​
/asadmin – All admin commands are contained within this command.

Permissions

    as.* – Gives permissions for all Auction Storm commands.
    as.base – Base Auction Storm permission.
    as.creative – Permission to start an auction in creative.
    as.admin – Permmision to use the admin command.
    as.reload – Permission to reload the config files.
    as.defaults – Permission to reset the config files.
    as.return – Permission to refund auctions.
    as.cancel – Permission to cancel any and all auctions.
    as.end - Permission to end the current auction.
    as.silence - This makes it so the player doesn't receive broadcasts
    as.togglesilence - Permission to toggle as.silence.


Setup

This plugin is drag and drop, it does however require Vault, a vault-dependent economy plugin and my library plugin: Alchemic Library. 
