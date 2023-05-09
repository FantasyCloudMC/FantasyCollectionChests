package com.fantasycloud.fantasycollectionchests.struct;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.regex.Pattern;

public class BlockLocation {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\|");

    public static BlockLocation from(String locationString) {
        String[] split = COMMA_PATTERN.split(locationString);
        World world = Bukkit.getWorld(LINE_PATTERN.split(split[0])[1]);
        int x = Integer.parseInt(LINE_PATTERN.split(split[1])[1]);
        int y = Integer.parseInt(LINE_PATTERN.split(split[2])[1]);
        int z = Integer.parseInt(LINE_PATTERN.split(split[3])[1]);
        return new BlockLocation(world, x, y, z);
    }

    private final Location location;

    public BlockLocation(Location location) {
        this.location = location;
    }

    public BlockLocation(World world, int x, int y, int z) {
        this.location = new Location(world, x, y, z);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Location || object instanceof BlockLocation) {
            Location target;
            if (object instanceof BlockLocation) {
                target = ((BlockLocation) object).getLocation();
            } else {
                target = (Location) object;
            }

            if (this.location.getWorld().getUID().equals(target.getWorld().getUID()) &&
                    this.location.getBlockX() == target.getBlockX() &&
                    this.location.getBlockY() == target.getBlockY() &&
                    this.location.getBlockZ() == target.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        long hilo = this.getX() + this.getY() ^ this.getWorld().getUID().hashCode() + this.getZ();
        return ((int)(hilo >> 32)) ^ (int) hilo;
    }

    @Override
    public String toString() {
        return "world|" + this.getWorld().getName() + ",x|" + this.getX() + ",y|" + this.getY() + ",z|" + this.getZ();
    }

    public Location getLocation() {
        return this.location;
    }

    public int getX() {
        return this.getLocation().getBlockX();
    }

    public int getY() {
        return this.getLocation().getBlockY();
    }

    public int getZ() {
        return this.getLocation().getBlockZ();
    }

    public World getWorld() {
        return this.getLocation().getWorld();
    }

    public Chunk getChunk() {
        return this.location.getChunk();
    }

}
