package me.badbones69.crazyauctions.cmd.auctionadmin;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.NumberUtil;
import com.massivecraft.massivecore.util.Txt;
import me.badbones69.crazyauctions.cmd.type.TypeOfflinePlayer;
import me.badbones69.crazyauctions.database.AuctionSellDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AuctionAdminLogCmd extends MassiveCommand {

    public AuctionAdminLogCmd() {
        this.setAliases("log");
        this.setDesc("View a sellers auction logs");

        this.addParameter(TypeOfflinePlayer.get(), "player");
        this.addParameter(Parameter.getPage());

        this.addRequirements(RequirementIsPlayer.get(), RequirementHasPerm.get("crazyauctions.admin"));
    }

    @Override
    public void perform() throws MassiveException {
        OfflinePlayer offlinePlayer = this.readArg();
        Player sender = this.me;
        int page = this.readArg();

        AuctionSellDatabase.get().getAuctionLogs(offlinePlayer.getUniqueId(), logEntries -> {

            Collections.reverse(logEntries);

            Pager<AuctionSellDatabase.LogEntry> pager = new Pager<>(this, "Auction Logs", page, logEntries, (Msonifier<AuctionSellDatabase.LogEntry>) (logEntry, index) -> {
                StringBuilder stringBuilder = new StringBuilder();
                OfflinePlayer sellerPlayer = Bukkit.getOfflinePlayer(logEntry.getSellerUUID());
                OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(logEntry.getPurchaserUUID());
                ItemStack itemStack = logEntry.getItem();

                stringBuilder.append(ChatColor.GREEN).append(sellerPlayer.getName());
                stringBuilder.append(ChatColor.WHITE).append(" sold ");
                stringBuilder.append(ChatColor.GREEN).append(NumberUtil.format(itemStack.getAmount())).append("x ").append(Txt.getItemName(itemStack));
                stringBuilder.append(ChatColor.WHITE).append(" to ");
                stringBuilder.append(ChatColor.GREEN).append(buyerPlayer.getName());
                stringBuilder.append(ChatColor.WHITE).append(" for ");
                stringBuilder.append(ChatColor.GREEN).append("$").append(NumberUtil.format(logEntry.getPrice()));

                return Mson.mson(stringBuilder.toString()).item(itemStack).command(AuctionAdminReturnCmd.get(), offlinePlayer.getName(), ""+logEntry.getSoldMs(), sender.getName());
            });

            pager.message();
        });
    }
}
