package net.frozenorb.foxtrot.server.voucher;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.ArrayList;
import java.util.List;

public class VoucherTemplate {

    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String category; // RANK, AMOUNT, BUNDLE, COMMAND, MONEY, LIVES
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private List<String> lore;
    @Getter
    @Setter
    private String value;
    @Getter
    @Setter
    private String duration;
    @Getter
    @Setter
    private String material;
    @Getter
    @Setter
    private short data;

    public VoucherTemplate(String id) {
        this.id = id;
        this.category = "COMMAND";
        this.displayName = "&cNew Voucher";
        this.lore = new ArrayList<>();
        this.value = "none";
        this.duration = "perm";
        this.material = "PAPER";
        this.data = 0;
    }

    public VoucherTemplate(DBObject document) {
        this.id = (String) document.get("id");
        this.category = (String) document.get("category");
        this.displayName = (String) document.get("displayName");

        this.lore = new ArrayList<>();
        if (document.containsField("lore")) {
            BasicDBList list = (BasicDBList) document.get("lore");
            for (Object obj : list) {
                this.lore.add((String) obj);
            }
        }

        this.value = (String) document.get("value");
        this.duration = (String) document.get("duration");

        if (document.containsField("material")) {
            this.material = (String) document.get("material");
            this.data = ((Number) document.get("data")).shortValue();
        } else {
            this.material = "PAPER";
            this.data = 0;
        }
    }

    public DBObject toDocument() {
        BasicDBObject document = new BasicDBObject();
        document.put("id", id);
        document.put("category", category);
        document.put("displayName", displayName);
        document.put("lore", lore);
        document.put("value", value);
        document.put("duration", duration);
        document.put("material", material);
        document.put("data", data);
        return document;
    }

    public void save() {
        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME)
                    .getCollection("VoucherTemplates");
            BasicDBObject query = new BasicDBObject("id", id);
            collection.update(query, toDocument(), true, false);
        });
    }

    public void delete() {
        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME)
                    .getCollection("VoucherTemplates");
            BasicDBObject query = new BasicDBObject("id", id);
            collection.remove(query);
        });
    }
}
