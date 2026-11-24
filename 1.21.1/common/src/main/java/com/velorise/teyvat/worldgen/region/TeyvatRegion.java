package com.velorise.teyvat.worldgen.region;

/**
 * Bounded 7-nation Teyvat regions + Dragonspine & Dark Sea outer world border.
 * Only MONDSTADT is unlocked by default in v1.0: Mondstadt Prologue.
 */
public enum TeyvatRegion {
    DARK_SEA(0, "dark_sea", false, 63),
    MONDSTADT(1, "mondstadt", true, 63),
    DRAGONSPINE(2, "dragonspine", false, 63),
    LIYUE(3, "liyue", false, 63),
    INAZUMA(4, "inazuma", false, 63),
    SUMERU(5, "sumeru", false, 63),
    FONTAINE(6, "fontaine", false, 150),
    NATLAN(7, "natlan", false, 63),
    SNEZHNAYA(8, "snezhnaya", false, 63);

    private static final TeyvatRegion[] BY_ID = values();

    private final int id;
    private final String serializedName;
    private final boolean unlockedByDefault;
    private final int regionalSeaLevel;

    TeyvatRegion(int id, String serializedName, boolean unlockedByDefault, int regionalSeaLevel) {
        this.id = id;
        this.serializedName = serializedName;
        this.unlockedByDefault = unlockedByDefault;
        this.regionalSeaLevel = regionalSeaLevel;
    }

    public int id() {
        return id;
    }

    public String serializedName() {
        return serializedName;
    }

    public boolean isUnlockedByDefault() {
        return unlockedByDefault;
    }

    /**
     * Supports Multi-Aquifer elevated seas (e.g. Fontaine plateau at Y=150).
     */
    public int regionalSeaLevel() {
        return regionalSeaLevel;
    }

    public static TeyvatRegion byId(int id) {
        if (id < 0 || id >= BY_ID.length) {
            return DARK_SEA;
        }
        return BY_ID[id];
    }
}
