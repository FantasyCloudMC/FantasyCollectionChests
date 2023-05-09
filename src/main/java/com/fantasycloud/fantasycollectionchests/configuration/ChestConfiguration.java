package com.fantasycloud.fantasycollectionchests.configuration;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.MaterialConfiguration;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChestConfiguration {

    private static final Pattern COLON_PATTERN = Pattern.compile(":");

    private final List<MaterialConfiguration> materialConfigurations;
    private final EnumSet<Material> acceptedMaterials;

    private final String materialName;
    private final List<String> materialLore;

    private final String chestName;
    private final List<String> chestLore;

    private final String sellAllName;
    private final List<String> sellAllLore;
    private final Material sellAllMaterial;

    public ChestConfiguration() {
        this.materialConfigurations = this.calculateStoredMaterials();

        this.acceptedMaterials = EnumSet.copyOf(
                this.materialConfigurations.stream()
                    .map(MaterialConfiguration::getMaterial)
                    .collect(Collectors.toList())
        );

        this.materialName = this.findMaterialName();
        this.materialLore = this.findMaterialLore();

        this.chestName = this.findChestName();
        this.chestLore = this.findChestLore();

        this.sellAllName = this.findSellAllItemName();
        this.sellAllLore = this.findSellAllItemLore();
        this.sellAllMaterial = this.findSellAllItemMaterial();
    }

    public List<MaterialConfiguration> calculateStoredMaterials() {
        List<MaterialConfiguration> materialConfigurations = new ArrayList<>();
        FantasyCollectionChests.getInstance().getConfig().getStringList("chest-options.stored-materials").forEach(string -> {
            try {
                String[] splitArray = COLON_PATTERN.split(string);
                Material material = Material.getMaterial(splitArray[0]);
                int row = Integer.parseInt(splitArray[1]);
                int column = Integer.parseInt(splitArray[2]);
                int maxAmount = Integer.parseInt(splitArray[3]);
                double sellPrice = Double.parseDouble(splitArray[4]);
                String displayName = splitArray[5];
                MaterialConfiguration materialConfiguration = new MaterialConfiguration(
                        material,
                        row,
                        column,
                        maxAmount,
                        sellPrice,
                        displayName
                );
                materialConfigurations.add(materialConfiguration);
            } catch (Exception e) {
                System.out.println("CAUGHT MALFORMED MATERIAL STRING: " + string);
                System.out.println("IGNORING");
            }
        });
        return materialConfigurations;
    }

    private String findMaterialName() {
        return FantasyCollectionChests.getInstance().getConfig().getString("sellitem.name");
    }

    private List<String> findMaterialLore() {
        return FantasyCollectionChests.getInstance().getConfig().getStringList("sellitem.lore");
    }

    private String findChestName() {
        return CommonsUtil.color(FantasyCollectionChests.getInstance().getConfig().getString("chestitem.name"));
    }

    private List<String> findChestLore() {
        return CommonsUtil.color(FantasyCollectionChests.getInstance().getConfig().getStringList("chestitem.lore"));
    }

    private String findSellAllItemName() {
        return FantasyCollectionChests.getInstance().getConfig().getString("sellallitem.name");
    }

    private List<String> findSellAllItemLore() {
        return FantasyCollectionChests.getInstance().getConfig().getStringList("sellallitem.lore");
    }

    private Material findSellAllItemMaterial() {
        return Material.getMaterial(FantasyCollectionChests.getInstance().getConfig().getString("sellallitem.material"));
    }

    @Nullable
    public MaterialConfiguration getConfiguration(Material material) {
        return this.materialConfigurations
            .stream()
            .filter(configuration -> configuration.getMaterial() == material)
            .findFirst().orElse(null);
    }

    public List<MaterialConfiguration> getMaterialConfigurations() {
        return this.materialConfigurations;
    }

    public Set<Material> getAcceptedMaterials() {
        return this.acceptedMaterials;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public List<String> getMaterialLore() {
        return this.materialLore;
    }

    public String getChestName() {
        return this.chestName;
    }

    public List<String> getChestLore() {
        return this.chestLore;
    }

    public String getSellAllName() {
        return this.sellAllName;
    }

    public List<String> getSellAllLore() {
        return this.sellAllLore;
    }

    public Material getSellAllMaterial() {
        return this.sellAllMaterial;
    }

    public void reloadConfiguration() {
        FantasyCollectionChests.getInstance().reloadConfig();
        FantasyCollectionChests.getInstance().setChestConfiguration(new ChestConfiguration());
    }

}
