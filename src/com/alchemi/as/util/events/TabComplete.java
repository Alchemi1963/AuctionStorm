package com.alchemi.as.util.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> tabSuggest = new ArrayList<>();
        List<String> list = new ArrayList<>();

        if (!sender instanceof Player)
            return tabSuggest;

        if (!sender.hasPermission("as.base"))
            return tabSuggest;

        if(args.length == 1) {
            list.add("auc");
            list.add("bid");
            if (sender.hasPermission("as.admin"))
                list.add("asadmin");
        } else {
            if (args[0].equalsIgnoreCase("auc")) {
                list.add("help");
                list.add("start");
                list.add("info");
                list.add("cancel");
            } else if (args[0].equalsIgnoreCase("bid")) {

            } else if (args[0].equalsIgnoreCase("asadmin") && sender.hasPermission("as.admin")) {
                if(args.length == 2) {
                    list.add("info");
                    if (sender.hasPermission("as.return"))
                        list.add("return");
                    if (sender.hasPermission("as.reload"))
                        list.add("reload");
                    if (sender.hasPermission("as.defaults"))
                        list.add("defaults");
                } else if(args.length == 3 && args[1].equalsIgnoreCase("return") && sender.hasPermission("as.return")) {
                    //en ga zo door
                    //ik ken de exacte opmaak van de commands niet
                }

            }
        }

        for (int i = list.size() - 1; i >= 0; i--)
            if(list.get(i).startsWith(args[args.length - 1]))
                tabSuggest.add(list.get(i));

        Collections.sort(tabSuggest);
        return tabSuggest;
    }
}
