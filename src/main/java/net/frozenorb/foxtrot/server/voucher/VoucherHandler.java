package net.frozenorb.foxtrot.server.voucher;

import com.mongodb.DBCursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoucherHandler {

    @Getter private List<VoucherTemplate> templates = new ArrayList<>();

    public VoucherHandler(Foxtrot instance) {
        instance.getServer().getScheduler().runTaskAsynchronously(instance, this::loadData);
    }

    public void loadData() {
        DBCollection collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("VoucherTemplates");
        try (DBCursor cursor = collection.find()) {
            while (cursor.hasNext()) {
                templates.add(new VoucherTemplate(cursor.next()));
            }
        }
        Foxtrot.getInstance().getLogger().info("Loaded " + templates.size() + " Voucher Templates.");
    }

    public Optional<VoucherTemplate> fromName(String name) {
        return templates.stream().filter(v -> v.getId().equalsIgnoreCase(name)).findFirst();
    }
}
