package net.PvZModders.PvZMod.progression.shop;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DaveShopRegistry {
    private static final ResourceLocation SUNDROP = new ResourceLocation(PvZ2Mod.MOD_ID, "sundrop");
    private static final ResourceLocation JALAPENO_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "jalapeno_seed_packet");
    private static final Map<GardenId, List<DaveShopEntry>> STOCK = createStock();

    private DaveShopRegistry() {
    }

    public static List<DaveShopEntry> getShopStockForGarden(GardenId gardenId) {
        return STOCK.getOrDefault(gardenId, List.of());
    }

    public static Optional<DaveShopEntry> getEntry(GardenId gardenId, String entryId) {
        return getShopStockForGarden(gardenId).stream()
                .filter(entry -> entry.id().equals(entryId))
                .findFirst();
    }

    private static Map<GardenId, List<DaveShopEntry>> createStock() {
        Map<GardenId, List<DaveShopEntry>> stock = new EnumMap<>(GardenId.class);
        for (GardenId gardenId : GardenId.values()) {
            List<DaveShopEntry> entries = new ArrayList<>();
            entries.add(DaveShopEntry.item(gardenId, "sample_sun_cache", "Sun Cache",
                    "Testing stock. Later this slot becomes garden-specific shop inventory.",
                    5, SUNDROP, 3, true));
            stock.put(gardenId, entries);
        }

        stock.get(GardenId.GREENHOUSE).add(DaveShopEntry.plantUnlock(GardenId.GREENHOUSE, "unlock_jalapeno",
                "Jalapeno Permit", "Unlocks Jalapeno in this world's Greenhouse planter stock.",
                40, JALAPENO_PACKET, "jalapeno", 0));
        return Map.copyOf(stock);
    }
}
