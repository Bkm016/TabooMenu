package me.skymc.taboomenu.display.data;

/**
 * @Author sky
 * @Since 2018-06-06 12:38
 */
public enum ClickType {

    RIGHT,

    SHIFT_RIGHT,

    LEFT,

    SHIFT_LEFT,

    MIDDLE,

    DROP,

    CONTROL_DROP,

    VIEW,

    ALL;

    public static ClickType getByName(String str) {
        try {
            return ClickType.valueOf(str);
        } catch (Exception ignored) {
            return ClickType.ALL;
        }
    }
}
