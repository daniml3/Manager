package com.daniml3.manager;

public class Constants {
    public static final String TAG = "Manager";
    public static final String BUILD_VARIABLE_PREFIX = "ARROW_";

    public static final int[] SWITCHES_MAX_VALUES = {1, 2, 1, 3, 1, 1};
    public static final int [][] COLORS_FOR_SWITCHES = {
            {R.color.button_disabled, R.color.blue}, // Sync switch
            {R.color.button_disabled, R.color.blue, R.color.blue}, // Clean switch
            {R.color.button_disabled, R.color.blue}, // CCache switch
            {R.color.button_disabled, R.color.blue, R.color.blue, R.color.blue}, // GApps switch
            {R.color.button_disabled, R.color.blue}, // Official switch
            {R.color.button_disabled, R.color.blue}}; // No repopick switch
    public static final int [][] TEXTS_FOR_SWITCHES = {
            {R.string.sync, R.string.sync}, // Sync switch
            {R.string.clean, R.string.clean_product ,R.string.clean}, // Clean switch
            {R.string.ccache, R.string.ccache}, // CCache switch
            {R.string.default_value, R.string.vanilla, R.string.gapps, R.string.both}, // GApps switch
            {R.string.official, R.string.official}, // Official switch
            {R.string.no_repopick, R.string.no_repopick}}; // No repopick switch
    public static final String[][] VALUES_FOR_SWITCHES = {
            {"--disabled", "--sync"}, // Sync switch
            {"--disabled","--clean-product", "--clean"}, // Clean switch
            {"--disabled", "--ccache"}, // CCache switch
            {"--disabled", "--vanilla", "--gapps", "--both"}, // GApps switch
            {"--disabled", "--official"}, // Official switch
            {"--disabled", "--no-repopick"}}; // No repopick switch
    public static final String[] VARIABLE_NAMES_FOR_SWITCHES = {"Sync", "Clean", "Ccache", "Gapps", "Official", "No_repopick"};
    public static final int[] DEFAULT_SWITCHES_VALUES = {1, 1 ,1 ,2, 1, 0};
}
