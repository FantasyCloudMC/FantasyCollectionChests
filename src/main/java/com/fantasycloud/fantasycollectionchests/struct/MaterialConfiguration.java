package com.fantasycloud.fantasycollectionchests.struct;

import org.bukkit.Material;

public class MaterialConfiguration {

    private final Material material;
    private final int row;
    private final int column;
    private final int maxAmount;
    private final double sellPrice;
    private final String displayName;

    public MaterialConfiguration(Material material, int row, int column, int maxAmount, double sellPrice, String displayName) {
        this.material = material;
        this.row = row;
        this.column = column;
        this.maxAmount = maxAmount;
        this.sellPrice = sellPrice;
        this.displayName = displayName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

}
