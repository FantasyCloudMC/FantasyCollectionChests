package com.fantasycloud.fantasycollectionchests.struct;

import org.bukkit.Chunk;

public class SimpleChunk {

    private final Chunk chunk;

    public SimpleChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleChunk)) return false;
        SimpleChunk simpleChunk = (SimpleChunk) o;
        return simpleChunk.getChunk().getX() == this.getChunk().getX() &&
               simpleChunk.getChunk().getZ() == this.getChunk().getZ() &&
               simpleChunk.getChunk().getWorld().getUID() == this.getChunk().getWorld().getUID();
    }

    @Override
    public int hashCode() {
        long hilo = this.getChunk().getX() ^ this.getChunk().getWorld().getUID().hashCode() + this.getChunk().getZ();
        return ((int)(hilo >> 32)) ^ (int) hilo;
    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
