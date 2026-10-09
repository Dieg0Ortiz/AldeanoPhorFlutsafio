package aldeanoforaflut.aldeanoforaflut.trade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * World-level saved data that stores the configurable trade pool.
 * Admins can add/remove items and set their weight (percentage chance).
 */
public class TradeConfigData extends SavedData {

    private static final String DATA_NAME = "phora_trade_config";
    public static final int MAX_ENTRIES = 256;

    public static class TradeEntry {
        public String itemId; // e.g. "minecraft:diamond"
        public int minCount;
        public int maxCount;
        public int weight; // Percentage weight (1-100)
        public int requiredLevel; // 1-5

        public TradeEntry(String itemId, int minCount, int maxCount, int weight, int requiredLevel) {
            this.itemId = itemId;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.weight = weight;
            this.requiredLevel = requiredLevel;
        }

        public Item getItem() {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("item", itemId);
            tag.putInt("min", minCount);
            tag.putInt("max", maxCount);
            tag.putInt("weight", weight);
            tag.putInt("level", requiredLevel);
            return tag;
        }

        public static TradeEntry load(CompoundTag tag) {
            return new TradeEntry(
                tag.getString("item"),
                tag.getInt("min"),
                tag.getInt("max"),
                tag.getInt("weight"),
                tag.getInt("level")
            );
        }
    }

    private final List<TradeEntry> entries = new ArrayList<>();

    public TradeConfigData() {
        super();
        // Default entries if empty
        if (entries.isEmpty()) {
            initDefaults();
        }
    }

    private TradeConfigData(CompoundTag tag) {
        super();
        load(tag);
    }
    
    public void load(CompoundTag tag) {
        entries.clear();
        ListTag list = tag.getList("Trades", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            entries.add(TradeEntry.load(list.getCompound(i)));
        }
        if (entries.isEmpty()) {
            initDefaults();
        }
    }

    private void initDefaults() {
        // Level 1
        entries.add(new TradeEntry("minecraft:stick", 32, 64, 20, 1));
        entries.add(new TradeEntry("minecraft:dirt", 64, 64, 20, 1));
        entries.add(new TradeEntry("minecraft:cobblestone", 64, 64, 20, 1));
        entries.add(new TradeEntry("minecraft:wheat", 20, 30, 20, 1));
        entries.add(new TradeEntry("minecraft:sand", 64, 64, 20, 1));
        // Level 2
        entries.add(new TradeEntry("minecraft:coal", 10, 20, 15, 2));
        entries.add(new TradeEntry("minecraft:baked_potato", 10, 20, 15, 2));
        entries.add(new TradeEntry("minecraft:apple", 5, 10, 15, 2));
        entries.add(new TradeEntry("minecraft:string", 15, 30, 15, 2));
        entries.add(new TradeEntry("minecraft:rotten_flesh", 30, 40, 15, 2));
        entries.add(new TradeEntry("minecraft:beetroot", 10, 20, 15, 2));
        // Level 3
        entries.add(new TradeEntry("minecraft:iron_ingot", 5, 10, 10, 3));
        entries.add(new TradeEntry("minecraft:gold_ingot", 5, 10, 10, 3));
        entries.add(new TradeEntry("minecraft:bamboo", 30, 64, 10, 3));
        entries.add(new TradeEntry("minecraft:honeycomb", 5, 10, 10, 3));
        entries.add(new TradeEntry("minecraft:lapis_lazuli", 10, 20, 10, 3));
        // Level 4
        entries.add(new TradeEntry("minecraft:diamond", 1, 3, 5, 4));
        entries.add(new TradeEntry("minecraft:obsidian", 3, 10, 5, 4));
        entries.add(new TradeEntry("minecraft:ender_pearl", 2, 5, 5, 4));
        entries.add(new TradeEntry("minecraft:slime_ball", 5, 10, 5, 4));
        entries.add(new TradeEntry("minecraft:emerald", 3, 6, 5, 4));
        // Level 5
        entries.add(new TradeEntry("minecraft:netherite_ingot", 1, 1, 2, 5));
        entries.add(new TradeEntry("minecraft:ghast_tear", 1, 2, 2, 5));
        entries.add(new TradeEntry("minecraft:blaze_rod", 5, 10, 2, 5));
        entries.add(new TradeEntry("minecraft:phantom_membrane", 5, 10, 2, 5));
        entries.add(new TradeEntry("minecraft:golden_apple", 1, 2, 2, 5));
    }

    public List<TradeEntry> getEntries() {
        return entries;
    }

    public List<TradeEntry> getEntriesForLevel(int level) {
        List<TradeEntry> result = new ArrayList<>();
        for (TradeEntry e : entries) {
            if (e.requiredLevel == level) result.add(e);
        }
        return result;
    }

    public void addEntry(TradeEntry entry) {
        entries.add(entry);
        setDirty();
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            setDirty();
        }
    }

    public void updateEntry(int index, TradeEntry entry) {
        if (index >= 0 && index < entries.size()) {
            entries.set(index, entry);
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (TradeEntry entry : entries) {
            list.add(entry.save());
        }
        tag.put("Trades", list);
        return tag;
    }

    /**
     * Reemplaza el pool con datos enviados por un cliente, descartando entradas inválidas
     * y limitando los valores a rangos seguros.
     */
    public void loadFromClient(CompoundTag tag) {
        entries.clear();
        ListTag list = tag.getList("Trades", Tag.TAG_COMPOUND);
        int size = Math.min(list.size(), MAX_ENTRIES);
        for (int i = 0; i < size; i++) {
            TradeEntry entry = TradeEntry.load(list.getCompound(i));
            ResourceLocation id = ResourceLocation.tryParse(entry.itemId);
            if (id == null || !ForgeRegistries.ITEMS.containsKey(id) || entry.getItem() == Items.AIR) {
                continue;
            }
            int maxStack = entry.getItem().getMaxStackSize();
            int min = Mth.clamp(entry.minCount, 1, maxStack);
            int max = Mth.clamp(entry.maxCount, min, maxStack);
            entries.add(new TradeEntry(
                    id.toString(), min, max,
                    Mth.clamp(entry.weight, 1, 100),
                    Mth.clamp(entry.requiredLevel, 1, 5)));
        }
        setDirty();
    }

    /** El pool es global: siempre se guarda en el Overworld, sin importar la dimensión. */
    public static TradeConfigData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
            TradeConfigData::new,
            TradeConfigData::new,
            DATA_NAME
        );
    }
}
