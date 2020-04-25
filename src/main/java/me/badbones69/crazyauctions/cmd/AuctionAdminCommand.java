package me.badbones69.crazyauctions.cmd;

import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import me.badbones69.crazyauctions.cmd.auctionadmin.AuctionAdminLogCmd;
import me.badbones69.crazyauctions.cmd.auctionadmin.AuctionAdminReturnCmd;

public class AuctionAdminCommand extends MassiveCommand {

    private static AuctionAdminCommand instance = new AuctionAdminCommand();
    public static AuctionAdminCommand get() { return instance; }

    private AuctionAdminCommand() {
        this.setAliases("auctionadmin");
        this.setDesc("Handle administration commands of the CrazyAuctions plugin");

        this.addChild(new AuctionAdminLogCmd());
        this.addChild(AuctionAdminReturnCmd.get());

        this.addRequirements(RequirementHasPerm.get("crazyauctions.admin"));
    }
}
