package com.darkblade12.itemslotmachine.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

public final class Cuboid implements Iterable<Block> {

    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;
    private final String worldName;

    public Cuboid(Location l1, Location l2) {
        if (l1 == null || l2 == null) {
            throw new NullPointerException("Location can not be null");
        } else if (l1.getWorld() == null) {
            throw new IllegalStateException("Can not create a Cuboid for an unloaded world");
        } else if (!l1.getWorld().getName().equals(l2.getWorld().getName())) {
            throw new IllegalStateException("Can not create a Cuboid between two different worlds");
        }
        worldName = l1.getWorld().getName();
        x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }

    public boolean isInside(Location l) {
        if (!l.getWorld().getName().equals(worldName)) {
            return false;
        }
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        if (x >= x1 && x <= x2) {
            if (y >= y1 && y <= y2) {
                return z >= z1 && z <= z2;
            }
        }
        return false;
    }

    public boolean contains(Material m) {
        if (m.isBlock()) {
            throw new IllegalArgumentException("'" + m.name() + "' is not a valid block material");
        }
        for (Block b : this) {
            if (b.getType() == m) {
                return true;
            }
        }
        return false;
    }

    public Location getLowerNE() {
        return new Location(getWorld(), x1, y1, z1);
    }

    public Location getUpperSW() {
        return new Location(getWorld(), x2, y2, z2);
    }

    private World getWorld() {
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            throw new IllegalStateException("World '" + worldName + "' is not loaded");
        }
        return w;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(getWorld(), x1, y1, z1, x2, y2, z2);
    }

    private class CuboidIterator implements Iterator<Block> {

        private final World w;
        private final int baseX;
        private final int baseY;
        private final int baseZ;
        private int x, y, z;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;

        CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            baseX = x1;
            baseY = y1;
            baseZ = z1;
            sizeX = Math.abs(x2 - x1) + 1;
            sizeY = Math.abs(y2 - y1) + 1;
            sizeZ = Math.abs(z2 - z1) + 1;
            x = y = z = 0;
        }

        @Override
        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        @Override
        public Block next() {
            Block b = w.getBlockAt(baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return b;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This operation is not available");
        }
    }
}
