package me.badbones69.crazyauctions.cmd.auctionadmin;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.type.primitive.TypeLong;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.InventoryUtil;
import me.badbones69.crazyauctions.cmd.type.TypeOfflinePlayer;
import me.badbones69.crazyauctions.database.AuctionSellDatabase;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuctionAdminReturnCmd extends MassiveCommand {

    private static AuctionAdminReturnCmd instance = new AuctionAdminReturnCmd();
    public static AuctionAdminReturnCmd get() { return instance; }

    private AuctionAdminReturnCmd() {
        this.setAliases("return");
        this.setDesc("Return items to a player from a auction sale");

        this.addParameter(TypeOfflinePlayer.get(), "sellerPlayer");
        this.addParameter(TypeLong.get(), "sellerTimeMs");
        this.addParameter(TypePlayer.get(), "player");
    }

    @Override
    public void perform() throws MassiveException {
        OfflinePlayer offlinePlayer = this.readArg();
        long auctionTimeMs = this.readArg();
        Player player = this.readArg();

        CommandSender commandSender = this.sender;

        AuctionSellDatabase.get().getAuctionLog(offlinePlayer.getUniqueId(), auctionTimeMs, logEntry -> {
            if(logEntry == null) {
                MixinMessage.get().msgOne(commandSender, "&6&lAUCTION HOUSE &8» &cThere is no transaction saved for that player with that timeMs.");
                return;
            }

            MixinMessage.get().msgOne(commandSender, "&6&lAUCTION HOUSE &8» &eYou have just reimbursed the items to &6%s&e.", player.getName());
            InventoryUtil.giveItemStack(player, logEntry.getItem());
        });
    }
}
