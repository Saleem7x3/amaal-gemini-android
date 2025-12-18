package com.example.amaal.model;

public enum FiqhCategory {
    FARD_AYN(100, "Fard ʿAyn"),
    FARD_KIFAYAH(90, "Fard Kifāyah"),
    SUNNAH_MUAKKADAH(80, "Sunnah Muʾakkadah"),
    SUNNAH_GHAYR_MUAKKADAH(70, "Sunnah Ghayr Muʾakkadah"),
    MUSTAHAB(60, "Mustahab"),
    NAWAFIL(50, "Nawāfil"),
    MUBAH(10, "Mubāh"),
    MAKRUH(-10, "Makrūh");

    private final int weight;
    private final String displayName;

    FiqhCategory(int weight, String displayName) {
        this.weight = weight;
        this.displayName = displayName;
    }

    public int getWeight() {
        return weight;
    }

    public String getDisplayName() {
        return displayName;
    }
}