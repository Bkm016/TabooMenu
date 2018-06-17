package me.skymc.taboomenu.setting;

/**
 * @Author sky
 * @Since 2018-06-05 18:44
 */
public enum MenuSettings {

    /**
     * 菜单名称
     */
    NAME("NAME"),

    /**
     * 菜单行数
     */
    ROWS("ROWS"),

    /**
     * 菜单命令
     */
    COMMAND("COMMAND"),

    /**
     * 上级菜单
     */
    PREVIOUS("PREVIOUS"),

    /**
     * 自动刷新
     */
    AUTO_REFRESH("AUTO-REFRESH"),

    /**
     * 菜单特效
     */
    OPEN_ACTION("OPEN-ACTION"),

    /**
     * 是否跳过权限判断
     */
    PERMISSION_BYPASS("PERMISSION-BYPASS");

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    private final String text;

    MenuSettings(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
