package com.daniml3.manager;

public class Constants {
    public static final String TAG = "Manager";

    public static final int[] SWITCHES_MAX_VALUES = {1, 2, 1, 3, 1, 1, 1, 2};
    public static final int[][] COLORS_FOR_SWITCHES = {
            {R.color.button_disabled, R.color.blue}, // Sync switch
            {R.color.button_disabled, R.color.light_blue, R.color.blue}, // Clean switch
            {R.color.button_disabled, R.color.blue}, // CCache switch
            {R.color.button_disabled, R.color.blue, R.color.blue, R.color.blue}, // GApps switch
            {R.color.button_disabled, R.color.blue}, // Official switch
            {R.color.button_disabled, R.color.blue}, // No repopick switch
            {R.color.blue, R.color.blue, R.color.blue}, // Devices switch
            {R.color.blue, R.color.blue, R.color.blue}}; // Build type switch
    public static final int[][] TEXTS_FOR_SWITCHES = {{R.string.sync, R.string.sync}, // Sync switch
            {R.string.clean, R.string.clean, R.string.clean}, // Clean switch
            {R.string.ccache, R.string.ccache}, // CCache switch
            {R.string.default_value, R.string.vanilla, R.string.gapps,
                    R.string.both}, // GApps switch
            {R.string.official, R.string.official}, // Official switch
            {R.string.no_repopick, R.string.no_repopick}, // No repopick switch
            {R.string.device_sweet, R.string.device_davinci,
                    R.string.device_potter}, // Devices switch
            {R.string.user_build, R.string.userdebug_build,
                    R.string.eng_build}}; // Build type switch
    public static final String[][] VALUES_FOR_SWITCHES = {{"", "--sync"}, // Sync switch
            {"", "--clean-product", "--clean"}, // Clean switch
            {"", "--ccache"}, // CCache switch
            {"", "--vanilla", "--gapps", "--both"}, // GApps switch
            {"", "--official"}, // Official switch
            {"--no-repopick", ""}, // No repopick switch
            {"--device=sweet", "--device=davinci", "--device=potter"}, // Devices switch
            {"--buildtype=user", "--buildtype=userdebug", "--buildtype=eng"}}; // Build type switch
    public static final int[] DEFAULT_SWITCHES_VALUES = {1, 1, 1, 2, 1, 1, 0, 1};

    public static final String BUILD_CARD_COUNT_DEFAULT = "4";
    public static final String BUILD_CARD_REFRESH_FREQ_DEFAULT = "2";
    public static final String LOG_LINE_COUNT_DEFAULT = "15";
    public static final String BUILD_TOKEN_PREFERENCE = "build_token_preference";
    public static final String BUILD_CAR_COUNT_PREFERENCE = "build_card_count_preference";
    public static final String BUILD_CARD_REFRESH_FREQ_PREFERENCE =
            "build_card_refresh_frequency_preference";
    public static final String LOG_LINE_COUNT_PREFERENCE = "log_line_count_preference";

    public static final String SERVER_BASE_URL = "https://server.danielml.dev";
}
