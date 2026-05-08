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
    private static final ResourceLocation FIRE_PEASHOOTER_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "fire_peashooter_seed_packet");
    private static final ResourceLocation SOLAR_TOMATO_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "solar_tomato_seed_packet");
    private static final ResourceLocation PEA_NUT_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "pea_nut_seed_packet");
    private static final ResourceLocation PLANT_VITAMINS = new ResourceLocation(PvZ2Mod.MOD_ID, "plant_vitamins");
    private static final ResourceLocation GOLDEN_SHOVEL = new ResourceLocation("minecraft", "golden_shovel");
    private static final ResourceLocation PARSNIP_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "parsnip_seed_packet");
    private static final ResourceLocation HOT_DATE_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "hot_date_seed_packet");
    private static final ResourceLocation WASABI_WHIP_PACKET = new ResourceLocation(PvZ2Mod.MOD_ID, "wasabi_whip_seed_packet");
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

        List<DaveShopEntry> ancientEgypt = new ArrayList<>();
        ancientEgypt.add(DaveShopEntry.plantUnlock(GardenId.DESERT, "fire_peashooter_unlock",
                "Fire Peashooter", "Shoots flaming peas and resists freezing.",
                650, FIRE_PEASHOOTER_PACKET, "fire_peashooter", 0));
        ancientEgypt.add(DaveShopEntry.plantUnlock(GardenId.DESERT, "solar_tomato_unlock",
                "Solar Tomato", "Stuns zombies in an area and causes them to produce Sun.",
                500, SOLAR_TOMATO_PACKET, "solar_tomato", 0));
        ancientEgypt.add(DaveShopEntry.plantUnlock(GardenId.DESERT, "pea_nut_unlock",
                "Pea-nut", "Hybrid wall and shooter. Blocks zombies while firing peas.",
                600, PEA_NUT_PACKET, "pea_nut", 0));
        ancientEgypt.add(DaveShopEntry.item(GardenId.DESERT, "plant_vitamins",
                "Plant Vitamins", "Consumable. Increases one plant's attack speed for 10 seconds.",
                40, PLANT_VITAMINS, 1, true));
        stock.put(GardenId.DESERT, ancientEgypt);

        List<DaveShopEntry> wildWest = new ArrayList<>();
        wildWest.add(DaveShopEntry.upgrade(GardenId.WILD_WEST, "plant_shovel_2",
                "Plant Shovel II", "Plant removal upgrade. Shoveled plants refund 20% of their Sun cost as coins.",
                350, GOLDEN_SHOVEL, "plant_shovel"));
        wildWest.add(DaveShopEntry.plantUnlock(GardenId.WILD_WEST, "parsnip_unlock",
                "Parsnip", "Melee plant that attacks nearby zombies and can charge forward.",
                550, PARSNIP_PACKET, "parsnip", 0));
        wildWest.add(DaveShopEntry.plantUnlock(GardenId.WILD_WEST, "hot_date_unlock",
                "Hot Date", "Lures zombies toward it, then releases fire when eaten.",
                600, HOT_DATE_PACKET, "hot_date", 0));
        wildWest.add(DaveShopEntry.plantUnlock(GardenId.WILD_WEST, "wasabi_whip_unlock",
                "Wasabi Whip", "Melee fire plant that attacks in front and behind.",
                650, WASABI_WHIP_PACKET, "wasabi_whip", 0));
        stock.put(GardenId.WILD_WEST, wildWest);

        stock.get(GardenId.GREENHOUSE).add(DaveShopEntry.plantUnlock(GardenId.GREENHOUSE, "unlock_jalapeno",
                "Jalapeno Permit", "Unlocks Jalapeno in this world's Greenhouse planter stock.",
                40, JALAPENO_PACKET, "jalapeno", 0));
        return Map.copyOf(stock);
    }
}
