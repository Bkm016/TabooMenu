package me.skymc.taboomenu.setting;

/**
 * @Author 坏黑
 * @Since 2018-10-27 14:11
 */
public enum SimilarDegreeMode {

    CURRENT_VERSION, OLD_VERSION, NEW_VERSION;

    public static SimilarDegreeMode fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception ignored) {
        }
        return CURRENT_VERSION;
    }

}
