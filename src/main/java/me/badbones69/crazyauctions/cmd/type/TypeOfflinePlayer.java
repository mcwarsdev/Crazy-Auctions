package me.badbones69.crazyauctions.cmd.type;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeAbstract;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class TypeOfflinePlayer extends TypeAbstract<OfflinePlayer> {

    private static TypeOfflinePlayer instance = new TypeOfflinePlayer();
    public static TypeOfflinePlayer get() { return instance; }

    private TypeOfflinePlayer() {
        super(OfflinePlayer.class);
    }

    @Override
    public OfflinePlayer read(String s, CommandSender commandSender) throws MassiveException {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);

        if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) throw new MassiveException().addMsg("&cThis player has never played the server before");

        return offlinePlayer;
    }

    @Override
    public Collection<String> getTabList(CommandSender commandSender, String s) {
        return TypePlayer.get().getTabList(commandSender, s);
    }
}
