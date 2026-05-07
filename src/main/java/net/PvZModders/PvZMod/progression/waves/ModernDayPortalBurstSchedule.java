package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class ModernDayPortalBurstSchedule {
    private ModernDayPortalBurstSchedule() {
    }

    public static List<PortalBurst> burstsForWave(int wave) {
        List<PortalBurst> bursts = new ArrayList<>();
        if (wave >= 8) {
            bursts.add(new PortalBurst(wave, 20 * 25, ModernDayZombieGroup.ANCIENT_EGYPT, WaveSpawnDirection.NORTH,
                    List.of(new BurstZombie("mummy_zombie", 2), new BurstZombie("ra_zombie", 1))));
        }
        if (wave >= 12) {
            bursts.add(new PortalBurst(wave, 20 * 30, ModernDayZombieGroup.WILD_WEST, WaveSpawnDirection.WEST,
                    List.of(new BurstZombie("cowboy_zombie", 2), new BurstZombie("prospector_zombie", 1))));
        }
        if (wave >= 16) {
            bursts.add(new PortalBurst(wave, 20 * 35, ModernDayZombieGroup.FAR_FUTURE, WaveSpawnDirection.EAST,
                    List.of(new BurstZombie("jetpack_zombie", 1), new BurstZombie("robo_cone_zombie", 1))));
            bursts.add(new PortalBurst(wave, 20 * 42, ModernDayZombieGroup.BIG_WAVE_BEACH, WaveSpawnDirection.SOUTH,
                    List.of(new BurstZombie("beach_zombie", 2), new BurstZombie("fisherman_zombie", 1))));
        }
        if (wave >= 25) {
            bursts.add(new PortalBurst(wave, 20 * 45, ModernDayZombieGroup.NEON_MIXTAPE, WaveSpawnDirection.NORTH,
                    List.of(new BurstZombie("neon_zombie", 2), new BurstZombie("boombox_zombie", 1))));
            bursts.add(new PortalBurst(wave, 20 * 52, ModernDayZombieGroup.DARK_AGES, WaveSpawnDirection.EAST,
                    List.of(new BurstZombie("peasant_zombie", 2), new BurstZombie("wizard_zombie", 1))));
        }
        if (wave >= 30) {
            bursts.add(new PortalBurst(wave, 20 * 60, ModernDayZombieGroup.PIRATE_SEAS, WaveSpawnDirection.WEST,
                    List.of(new BurstZombie("swashbuckler_zombie", 2), new BurstZombie("pirate_captain_zombie", 1))));
            bursts.add(new PortalBurst(wave, 20 * 70, ModernDayZombieGroup.JURASSIC_MARSH, WaveSpawnDirection.SOUTH,
                    List.of(new BurstZombie("jurassic_bully", 1), new BurstZombie("rockpuncher_zombie", 1))));
        }
        return List.copyOf(bursts);
    }

    public record PortalBurst(int wave, int activationTick, ModernDayZombieGroup group, WaveSpawnDirection direction, List<BurstZombie> zombies) {
    }

    public record BurstZombie(String zombieId, int count) {
    }
}
