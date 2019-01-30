package me.skymc.taboomenu.setting;

/**
 * @Author sky
 * @Since 2018-06-05 18:44
 */
public enum MenuSettings {

    /**
     * 菜单名称
     */
    NAME("name|title|display"),

    /**
     * 菜单行数
     */
    ROWS("row(s)?|line(s)?|size"),

    /**
     * 菜单命令
     */
    COMMAND("(open-)?command(s)?"),

    /**
     * 上级菜单
     */
    PREVIOUS("previous(-menu)?"),

    /**
     * 自动刷新
     */
    AUTO_REFRESH("(auto-)?refresh"),

    /**
     * 菜单特效:开启
     */
    OPEN_ACTION("open-action|action-open"),

    /**
     * 菜单特效:关闭
     */
    CLOSE_ACTION("close-action|action-close"),

    /**
     * 是否跳过权限判断
     */
    PERMISSION_BYPASS("permission-bypass|no-permission"),

    /**
     * 是否跳过被取消的事件
     */
    IGNORE_CANCELLED("ignore-cancel(led)?");

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
