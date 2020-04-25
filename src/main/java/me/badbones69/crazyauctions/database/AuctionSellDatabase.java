package me.badbones69.crazyauctions.database;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.massivecore.Callback;
import com.massivecraft.massivecore.SQLite;
import com.massivecraft.massivecore.util.Base64SerializationUtil;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionSellDatabase extends SQLite {

    private static AuctionSellDatabase instance = new AuctionSellDatabase();
    public static AuctionSellDatabase get() { return instance; }

    private AuctionSellDatabase() {
        super("sell-logs");
    }

    @Override
    public List<String> getTableContents() {
        return MUtil.list("sellerUUID", "soldMs", "price", "purchaserUUID", "item");
    }

    @Override
    public List<String> getContentTypes() {
        return MUtil.list("TEXT", "LONG", "LONG", "TEXT", "TEXT");
    }

    public void getAuctionLogs(UUID uuid, Callback<List<LogEntry>> callback) {
        if(uuid == null) {
            callback.call(new ArrayList<>());
            return;
        }

        String id = parseId(uuid.toString());

        getAuctionLogs(id, callback);
    }

    public void getAuctionLogs(String id, Callback<List<LogEntry>> callback) {
        List<LogEntry> logEntries = new ArrayList<>();

        this.setupLogTable(id);

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(getPullAll(id));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet != null) {
                resultSet.next();

                UUID sellerUUID = UUID.fromString(resultSet.getString(1));
                long soldMs = resultSet.getLong(2);
                long price = resultSet.getLong(3);
                UUID purchaserUUID = UUID.fromString(resultSet.getString(4));
                ItemStack itemStack = Base64SerializationUtil.get().base64ToItemStack(resultSet.getString(5));

                logEntries.add(new LogEntry(sellerUUID, purchaserUUID, itemStack, price, soldMs));
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        callback.call(logEntries);
    }

    public void getAuctionLog(UUID uuid, long timeMs, Callback<LogEntry> callback) {
        if(uuid == null) {
            callback.call(null);
            return;
        }

        String id = parseId(uuid.toString());

        getAuctionLog(id, timeMs, callback);
    }

    public void getAuctionLog(String id, long timeMs, Callback<LogEntry> callback) {
        this.setupLogTable(id);

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(getPullRow(id, "soldMs"));
            preparedStatement.setLong(1, timeMs);

            ResultSet resultSet = preparedStatement.executeQuery();
            LogEntry logEntry = null;

            if(resultSet != null) {
                resultSet.next();

                UUID sellerUUID = UUID.fromString(resultSet.getString(1));
                long soldMs = resultSet.getLong(2);
                long price = resultSet.getLong(3);
                UUID purchaserUUID = UUID.fromString(resultSet.getString(4));
                ItemStack itemStack = Base64SerializationUtil.get().base64ToItemStack(resultSet.getString(5));

                logEntry = new LogEntry(sellerUUID, purchaserUUID, itemStack, price, soldMs);
            }

            preparedStatement.close();

            callback.call(logEntry);
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        callback.call(null);
    }

    public void addAuctionLog(LogEntry logEntry) {
        String id = parseId(logEntry.getSellerUUID().toString());

        this.setupLogTable(id);

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(getPushRow(id));

            preparedStatement.setString(1, logEntry.getSellerUUID().toString());
            preparedStatement.setLong(2, logEntry.getSoldMs());
            preparedStatement.setLong(3, logEntry.getPrice());
            preparedStatement.setString(4, logEntry.getPurchaserUUID().toString());
            preparedStatement.setString(5, Base64SerializationUtil.get().itemStackToBase64(logEntry.getItem()));

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setupLogTable(String id) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(getCreateTable(id));

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static class LogEntry {

        private UUID sellerUUID, purchaserUUID;
        private long price, soldMs;
        private ItemStack item;

        public LogEntry(UUID sellerUUID, UUID purchaserUUID, ItemStack itemStack, long price, long soldMs) {
            this.sellerUUID = sellerUUID;
            this.purchaserUUID = purchaserUUID;
            this.item = itemStack;
            this.price = price;
            this.soldMs = soldMs;
        }

        public UUID getPurchaserUUID() {
            return this.purchaserUUID;
        }

        public UUID getSellerUUID() {
            return this.sellerUUID;
        }

        public long getSoldMs() {
            return this.soldMs;
        }

        public long getPrice() {
            return this.price;
        }

        public ItemStack getItem() {
            return this.item;
        }
    }

}
